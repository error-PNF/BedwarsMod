package me.errorpnf.bedwarsmod.data.stats;

import cc.polyfrost.oneconfig.config.annotations.Exclude;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.data.BedwarsGameTeamStatus;
import me.errorpnf.bedwarsmod.mixin.MixinGuiPlayerTabOverlay;
import me.errorpnf.bedwarsmod.utils.HypixelLocraw;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionStats {
    // add a crap ton of @Exclude annotations so oneconfig doesn't try to save these variables in the mod's config file
    @Exclude
    private final String pfx = BedwarsMod.prefix;

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

        if (!HypixelLocraw.getIsInBedwarsGame()) return;

        if ((matcher = gameEndPattern.matcher(unformattedMessage)).find()) {
            String matchedTeam = matcher.group("team");
            gamesEnded++;
            stopGameTimer();

            queueGloatStats();

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

    @Exclude
    private boolean hasGameEnded = false;

    private void startGameTimer() {
        hasGameEnded = false;
        gameTimeTicks = 0;
        gamesPlayed++;
        isTimerRunning = true;
    }

    private void continueTimer() {
        if (!(gamesPlayed > 0)) gamesPlayed++;
        if (hasGameEnded) return;

        isTimerRunning = true;
    }

    private void stopGameTimer() {
        if (!isTimerRunning) return;

        hasGameEnded = true;
        isTimerRunning = false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (isGloatQueued) {
            gloatTicks++;
            if (gloatTicks > 40) {
                gloatStats();
            }
        }

        if (HypixelLocraw.getIsInBedwarsGame()) {
            if (!isTimerRunning) {
                continueTimer();
            }
        } else {
            stopGameTimer();
        }

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


    @Exclude
    private boolean isGloatQueued = false;
    @Exclude
    private int gloatTicks = 0;

    public void queueGloatStats() {
        isGloatQueued = true;
        gloatTicks = 0;
    }

    private void gloatStats() {
        isGloatQueued = false;
        gloatTicks = 0;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.ingameGUI == null) {
            return;
        }

        Object tabList = minecraft.ingameGUI.getTabList();
        if (!(tabList instanceof MixinGuiPlayerTabOverlay)) {
            return;
        }

        MixinGuiPlayerTabOverlay tabData = (MixinGuiPlayerTabOverlay) tabList;
        if (tabData.getTabFooter() == null) {
            return;
        }

        String footerData = tabData.getTabFooter().getUnformattedText();
        if (footerData == null) {
            return;
        }


        Pattern pattern = Pattern.compile("Kills: (\\d+).*?Final Kills: (\\d+).*?Beds Broken: (\\d+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(footerData);

        int kills = 0;
        int finalKills = 0;
        int bedsBroken = 0;

        if (matcher.find()) {
            kills = Integer.parseInt(matcher.group(1));
            finalKills = Integer.parseInt(matcher.group(2));
            bedsBroken = Integer.parseInt(matcher.group(3));
        }

        String statsString = "Kills: " + kills + " | Finals: " + finalKills + " | Beds: " + bedsBroken;

        String formattedMessage = FormatUtils.format(pfx + "&7Click &b&nhere&r &7to copy this game's stats to your clipboard.");
        IChatComponent chatComponent = new ChatComponentText(formattedMessage)
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwarsmod copytexttoclipboard " + statsString))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                FormatUtils.format("&7Click to copy your stats from the last game.")))));

        Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent);
    }
}
