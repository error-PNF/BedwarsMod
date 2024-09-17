package me.errorpnf.bedwarsmod.data.stats;

import cc.polyfrost.oneconfig.config.annotations.Exclude;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.errorpnf.bedwarsmod.data.BedwarsGameTeamStatus;
import me.errorpnf.bedwarsmod.utils.HypixelLocraw;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionStats {

    // add a crap ton of @Exclude annotations so oneconfig doesn't try to save these variables in the mod's config file

    @Exclude
    public int gamesStarted = 0;
    @Exclude
    public int gamesEnded = 0;
    @Exclude
    public int bedsBroken = 0;
    @Exclude
    public int bedsLost = 0;
    @Exclude
    public int finalKills = 0;
    @Exclude
    public int finalDeaths = 0;
    @Exclude
    public int kills = 0;
    @Exclude
    public int deaths = 0;
    @Exclude
    public int wins = 0;
    @Exclude
    public int losses = 0;

    @Exclude
    private Pattern gameStartPattern;
    @Exclude
    private Pattern gameEndPattern;
    @Exclude
    private Pattern bedBreakPattern;
    @Exclude
    private Pattern finalKillPattern;
    @Exclude
    private Pattern killPattern;
    @Exclude
    private Pattern selfKillPattern;

    @Exclude
    public String sessionUsername;
    
    public SessionStats(String sessionUsername) {
        this.sessionUsername = sessionUsername;
        loadPatternsFromJson();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void loadPatternsFromJson() {
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/bedwarsmod/data/chatregex.json"))) {
            if (reader == null) {
                throw new IllegalArgumentException("File not found: assets/bedwarsmod/data/chatregex.json");
            }
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(reader).getAsJsonObject();

            gameStartPattern = Pattern.compile(jsonObject.get("gameStartPattern").getAsString());
            gameEndPattern = Pattern.compile(jsonObject.get("gameEndPattern").getAsString());
            bedBreakPattern = Pattern.compile(jsonObject.get("bedBreakPattern").getAsString());
            finalKillPattern = Pattern.compile(jsonObject.get("finalKillPattern").getAsString());
            killPattern = Pattern.compile(jsonObject.get("killPattern").getAsString());
            selfKillPattern = Pattern.compile(jsonObject.get("selfKillPattern").getAsString());

            System.out.println("Successfully loaded regex patterns from JSON.");

        } catch (Exception e) {
            System.out.println("Error loading regex patterns from JSON.");
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (!HypixelLocraw.isInBedwarsGame()) return;

        String message = FormatUtils.removeResetCode(event.message.getFormattedText());
        String unformattedMessage = event.message.getUnformattedText();

        Matcher matcher;

        if (gameStartPattern.matcher(message).matches()) {
            gamesStarted++;
            notifyUser("Game started! Session starts: " + gamesStarted);
        } else if ((matcher = gameEndPattern.matcher(unformattedMessage)).find()) {
            String matchedTeam = matcher.group("team");
            gamesEnded++;
            notifyUser("Game Ended: " + gamesEnded);
            if (matchedTeam.contains(BedwarsGameTeamStatus.getCurrentTeam())) {
                wins++;
            } else {
                if (!unformattedMessage.contains("Killer")) {
                    losses++;
                }
            }
        } else if (unformattedMessage.startsWith("BED DESTRUCTION")) {
            if ((matcher = bedBreakPattern.matcher(unformattedMessage)).find()) {
                String team = matcher.group("team");
                String player = matcher.group("player");

                if (unformattedMessage.contains("Your Bed")) {
                    bedsLost++;
                } else if (unformattedMessage.contains(Minecraft.getMinecraft().thePlayer.getName())) {
                    bedsBroken++;
                }
            }
        } else if (message.endsWith("§7. §b§lFINAL KILL!")) {
            if ((matcher = finalKillPattern.matcher(unformattedMessage)).find()) {
                String killer = matcher.group("killer");
                String killed = matcher.group("killed");

                if (killer.equalsIgnoreCase(sessionUsername)) {
                    finalKills++;
                    notifyUser("Final Kill! Session final kills: " + finalKills);
                }

                if (killed.equalsIgnoreCase(sessionUsername)) {
                    finalDeaths++;
                    notifyUser("Final Death! Session final deaths: " + finalDeaths);
                }
            }
        } else if ((matcher = killPattern.matcher(message)).find()) {
            String killer = matcher.group("killer");
            String killed = matcher.group("killed");

            if (killer.equalsIgnoreCase(sessionUsername)) {
                kills++;
                notifyUser("Kill! Session kills: " + kills);
            }

            if (killed.equalsIgnoreCase(sessionUsername)) {
                deaths++;
                notifyUser("Death! Session deaths: " + deaths);
            }
        } else if ((matcher = selfKillPattern.matcher(message)).find()) {
            String killed = matcher.group("killed");
            deaths++;
            notifyUser("Self kill! Session self kills: " + deaths);
        } else {
            System.out.println("No pattern matched for message: " + message);
        }
    }

    private void notifyUser(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public String getSessionStats() {
        return String.format(
                "Session Stats:\n" +
                        "Games Started: %d\n" +
                        "Beds Broken: %d\n" +
                        "Final Kills: %d\n" +
                        "Regular Kills: %d\n" +
                        "Self Kills: %d\n",
                gamesStarted, bedsBroken, finalKills, kills, deaths
        );
    }

    public void resetSession() {
        gamesStarted = 0;
        bedsBroken = 0;
        bedsLost = 0;
        finalKills = 0;
        finalDeaths = 0;
        kills = 0;
        deaths = 0;
        wins = 0;
        losses = 0;
    }
}
