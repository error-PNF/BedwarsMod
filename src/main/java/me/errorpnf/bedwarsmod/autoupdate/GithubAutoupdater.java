package me.errorpnf.bedwarsmod.autoupdate;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import okhttp3.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class GithubAutoupdater {
    public static final String pfx = FormatUtils.format("&c[&fBW&c] &r");

    private static final String GITHUB_API_URL = "https://api.github.com/repos/error-PNF/BedwarsMod/releases/latest";
    private static String latestVersion;
    private static String downloadUrl;
    private static boolean hasPromptedUpdate = false;
    private static boolean addedShutdownHook = false;

    public static boolean isOutdated = false;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new GithubAutoupdater());
        checkForUpdates();
    }

    private static void addShutdownHook() {
        if (!addedShutdownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    File currentMod = new File(Minecraft.getMinecraft().mcDataDir, "mods/Bedwars_Mod-1.8.9-forge-" + BedwarsMod.VERSION + ".jar");
                    if (currentMod.exists() && isOutdated) {
                        createDeletionScript(currentMod);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            addedShutdownHook = true;
        }
    }

    private static void createDeletionScript(File modFile) {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        boolean isUnix = os.contains("nix") || os.contains("nux") || os.contains("mac");

        try {
            File deletionScript;
            if (isWindows) {
                // Windows: create a .bat file
                deletionScript = new File(Minecraft.getMinecraft().mcDataDir, "delete_old_mod.bat");
                try (PrintWriter writer = new PrintWriter(deletionScript, "UTF-8")) {
                    writer.println("del \"" + modFile.getAbsolutePath() + "\"");  // Delete the mod file
                    writer.println("exit");  // Close the terminal window
                }
            } else if (isUnix) {
                // macOS/Linux: create a .sh file
                deletionScript = new File(Minecraft.getMinecraft().mcDataDir, "delete_old_mod.sh");
                try (PrintWriter writer = new PrintWriter(deletionScript, "UTF-8")) {
                    writer.println("#!/bin/bash");
                    writer.println("rm \"" + modFile.getAbsolutePath() + "\"");  // Delete the mod file
                    writer.println("exit 0");  // Exit the shell script
                }
                deletionScript.setExecutable(true);  // Make sure the script is executable
            } else {
                System.out.println("Unsupported OS: " + os);
                return;
            }

            // Execute the deletion script
            if (deletionScript.exists()) {
                if (isWindows) {
                    Runtime.getRuntime().exec("cmd /c start " + deletionScript.getAbsolutePath());
                } else if (isUnix) {
                    Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", deletionScript.getAbsolutePath()});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String changelog = "";

    public static void checkForUpdates() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(GITHUB_API_URL)
                .build();
        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    UChat.chat("&cAn unexpected error occurred during the API request. This is likely a rate limit issue.");
                    return;
                }

                try {
                    String jsonResponse = getJsonFromUrl(GITHUB_API_URL);
                    JsonObject json = new JsonParser().parse(jsonResponse).getAsJsonObject();
                    latestVersion = json.get("tag_name").getAsString().replace("v", "");
                    downloadUrl = json.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
                    changelog = json.get("body").getAsString(); // Extract changelog

                    String currentVersion = BedwarsMod.VERSION;

                    System.out.println("Current Mod Version: " + currentVersion);
                    System.out.println("Latest version from GitHub: " + latestVersion);
                    System.out.println("Download URL: " + downloadUrl);
                    System.out.println("Changelog: " + changelog); // Print changelog

                    if (!latestVersion.equals(currentVersion)) {
                        isOutdated = true;
                        System.out.println("An update is available. Current version: " + currentVersion);
                        hasPromptedUpdate = false;
                        addShutdownHook();
                    }
                    future.complete(json);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });
    }


    @SubscribeEvent
    public void onPlayerLoggedIn(EntityJoinWorldEvent event) {
        if (!isOutdated || hasPromptedUpdate || event.entity != Minecraft.getMinecraft().thePlayer) return;

        hasPromptedUpdate = true;
        String currentVersion = BedwarsMod.VERSION;

        IChatComponent message = new ChatComponentText(pfx + FormatUtils.format("&7A new version of &bBedwars Mod &7is available! "));
        IChatComponent downloadLink = new ChatComponentText(pfx + FormatUtils.format("&7Click &b&nhere &r&7to download."));
        downloadLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwarsmod update"));

        // Include changelog in the message
        IChatComponent changelogMessage = new ChatComponentText(pfx + FormatUtils.format("&aChangelog:\n&7" + changelog));

        UChat.chat(pfx + "&cAlert!");
        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
        Minecraft.getMinecraft().thePlayer.addChatMessage(downloadLink);
        Minecraft.getMinecraft().thePlayer.addChatMessage(changelogMessage); // Send changelog to player
        UChat.chat(pfx + "&cBedwars Mod v" + currentVersion + " &b-> " + "&aBedwars Mod v" + latestVersion);
    }


    public static void downloadAndReplaceMod() {
        try {
            File modsFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
            File currentMod = new File(modsFolder, "Bedwars_Mod-1.8.9-forge-" + BedwarsMod.VERSION + ".jar");
            File newMod = new File(modsFolder, "Bedwars_Mod-1.8.9-forge-" + latestVersion + ".jar");

            FileUtils.copyURLToFile(new URL(downloadUrl), newMod);

            UChat.chat(pfx + "&7Downloaded &bBedwars Mod&7! The update will be applied when the game restarts.");

            isOutdated = true;
        } catch (Exception e) {
            e.printStackTrace();
            UChat.chat(pfx + "&7Failed to download the update. If you believe this is an error please report it.");
        }
    }



    private static String getJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }
}