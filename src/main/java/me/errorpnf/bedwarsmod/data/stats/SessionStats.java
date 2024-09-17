package me.errorpnf.bedwarsmod.data.stats;

import cc.polyfrost.oneconfig.config.annotations.Exclude;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.errorpnf.bedwarsmod.data.BedwarsGameTeamStatus;
import me.errorpnf.bedwarsmod.utils.HypixelLocraw;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionStats {

    // add a crap ton of @Exclude annotations so oneconfig doesn't try to save these variables in the mod's config file

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
    public int winstreak = 0;
    @Exclude
    public int gamesPlayed = 0;

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

    @Exclude
    public boolean isTimerRunning = false;
    @Exclude
    public int gameTimeTicks = 0;
    @Exclude
    public int totalSessionTimeTicks = 0;

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
        String message = FormatUtils.removeResetCode(event.message.getFormattedText());
        String unformattedMessage = event.message.getUnformattedText();

        Matcher matcher;

        if (gameStartPattern.matcher(message).matches()) {
            startGameTimer();
        }

        if (!HypixelLocraw.isInBedwarsGame) return;

        if ((matcher = gameEndPattern.matcher(unformattedMessage)).find()) {
            String matchedTeam = matcher.group("team");
            gamesEnded++;
            stopGameTimer();
            if (matchedTeam.contains(BedwarsGameTeamStatus.getCurrentTeam())) {
                wins++;
                winstreak++;
            } else {
                if (!unformattedMessage.contains("Killer")) {
                    losses++;
                    winstreak = 0;
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
                }

                if (killed.equalsIgnoreCase(sessionUsername)) {
                    finalDeaths++;
                }
            }
        } else if ((matcher = killPattern.matcher(message)).find()) {
            String killer = matcher.group("killer");
            String killed = matcher.group("killed");

            if (killer.equalsIgnoreCase(sessionUsername)) {
                kills++;
            }

            if (killed.equalsIgnoreCase(sessionUsername)) {
                deaths++;
            }
        } else if ((matcher = selfKillPattern.matcher(message)).find()) {
            String killed = matcher.group("killed");
            deaths++;
        } else {
            System.out.println("No pattern matched for message: " + message);
        }
    }

    private void startGameTimer() {
        gameTimeTicks = 0;
        gamesPlayed++;
        isTimerRunning = true;
    }

    private void continueTimer() {
        if (!(gamesPlayed > 0)) gamesPlayed ++;
        isTimerRunning = true;
    }

    private void stopGameTimer() {
        if (!isTimerRunning) return;

        isTimerRunning = false;
    }

    @Exclude
    private boolean didJoinWorld = false;

    @SubscribeEvent
    public void onWorldJoin(EntityJoinWorldEvent event) {
        if (event.entity == Minecraft.getMinecraft().thePlayer) {
            if (HypixelUtils.INSTANCE.isHypixel()) {
                didJoinWorld = true;
            }
        }
    }


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (HypixelLocraw.isInBedwarsGame) {
            if (!isTimerRunning) {
                continueTimer();
            }
        } else {
            stopGameTimer();
        }

//        if (HypixelLocraw.hasUpdatedLastGameServer && didJoinWorld && !HypixelLocraw.lastBedwarsGameServerID.isEmpty()) {
//            HypixelLocraw.hasUpdatedLastGameServer = false;
//            if (HypixelLocraw.lastBedwarsGameServerID.equals(HypixelLocraw.serverID)) {
//                continueTimer();
//            }
//
//            didJoinWorld = false;
//        }


        if (isTimerRunning && event.phase == TickEvent.Phase.START) {
            gameTimeTicks++;
            totalSessionTimeTicks++;
        }
    }

    public void resetSession() {
        bedsBroken = 0;
        bedsLost = 0;
        finalKills = 0;
        finalDeaths = 0;
        kills = 0;
        deaths = 0;
        wins = 0;
        losses = 0;
        gamesPlayed = 0;
    }

    private String formatTime(int ticks) {
        int seconds = ticks / 40;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
