package me.errorpnf.bedwarsmod.autoupdate;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModrinthUpdater {
    private final String pfx;

    public ModrinthUpdater(String modPrefix) {
        this.pfx = modPrefix;
    }

    private String latestVersion;
    private String downloadUrl;
    private boolean hasPromptedUpdate = false;

    public boolean isOutdated = false;

    public void init() {
        MinecraftForge.EVENT_BUS.register(new ModrinthUpdater(pfx));
        checkForUpdates();
    }

    private String changelog = "";

    public void checkForUpdates() {
        OkHttpClient client = new OkHttpClient();
        String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/bedwars-mod/version";
        Request request = new Request.Builder().url(MODRINTH_API_URL).header("User-Agent", ("errorPNF/BedwarsMod/" + BedwarsMod.VERSION)).build();
        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    UChat.chat("&cAn unexpected error occurred during the API request.");
                    return;
                }

                try {
                    String jsonResponse = Objects.requireNonNull(response.body()).string(); // get raw JSON
                    JsonArray jsonArray = new JsonParser().parse(jsonResponse).getAsJsonArray();
                    List<JsonObject> jsonObjects = new ArrayList<>();
                    for (JsonElement element : jsonArray) {
                        jsonObjects.add(element.getAsJsonObject());
                    }
                    // Get the first (latest) version object
                    JsonObject latestVersionObj = jsonObjects.get(0);
                    latestVersion = latestVersionObj.get("version_number").getAsString();
                    changelog = latestVersionObj.get("changelog").getAsString().replaceAll("\n", "\n§7"); // format changelog properly
                    downloadUrl = "https://modrinth.com/mod/bedwars-mod/version/" + latestVersion; // direct link to latest version page

                    String currentVersion = BedwarsMod.VERSION;

                    System.out.println("Current Mod Version: " + currentVersion);
                    System.out.println("Latest Modrinth Version: " + latestVersion);
                    System.out.println("Changelog: " + changelog);

                    if (!latestVersion.equals(currentVersion)) {
                        isOutdated = true;
                        System.out.println("An update is available. Current version: " + currentVersion);
                        hasPromptedUpdate = false;
                    }
                    future.complete(latestVersionObj);
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
        IChatComponent downloadLink = new ChatComponentText(pfx + FormatUtils.format("&7Click &b&nhere&r&7 to download."));
        downloadLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl));

        // Include changelog in the message
        IChatComponent changelogMessage = new ChatComponentText(pfx + FormatUtils.format("&aChangelog:\n&7" + changelog));

        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
        Minecraft.getMinecraft().thePlayer.addChatMessage(downloadLink);
        Minecraft.getMinecraft().thePlayer.addChatMessage(changelogMessage); // Send changelog to player
        UChat.chat(pfx + "&cBedwars Mod v" + currentVersion + " &b-> " + "&aBedwars Mod v" + latestVersion);
    }

    private String getJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
