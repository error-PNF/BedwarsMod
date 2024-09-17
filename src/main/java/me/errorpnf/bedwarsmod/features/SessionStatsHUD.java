package me.errorpnf.bedwarsmod.features;

import cc.polyfrost.oneconfig.config.annotations.Exclude;
import cc.polyfrost.oneconfig.hud.TextHud;
import me.errorpnf.bedwarsmod.data.stats.SessionStats;
import me.errorpnf.bedwarsmod.mixin.MixinGuiPlayerTabOverlay;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionStatsHUD extends TextHud {

    public SessionStats getSession() {
        return session;
    }

    @Exclude
    private SessionStats session = new SessionStats("");

    public SessionStatsHUD() {
        super(true);
    }

    @Exclude
    private boolean createdSession = false;

    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (Minecraft.getMinecraft() == null) {
            return;
        } else if (Minecraft.getMinecraft().thePlayer == null) {
            return;
        } else if (Minecraft.getMinecraft().thePlayer.getName() == null) {
            return;
        } else if (session == null) {
            return;
        }

        if (!createdSession) {
            session.sessionUsername = Minecraft.getMinecraft().thePlayer.getName();
            createdSession = true;
        }

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

        double sessionAvgGameTimeTicks;
        if (session.gamesPlayed == 0) {
            sessionAvgGameTimeTicks = session.totalSessionTimeTicks;
        } else if (session.isTimerRunning) {
            sessionAvgGameTimeTicks = (double) session.totalSessionTimeTicks / (session.gamesPlayed + 1);
        } else {
            sessionAvgGameTimeTicks = (double) session.totalSessionTimeTicks / session.gamesPlayed;
        }

        double sessionKDR = FormatUtils.roundToTwoDecimalPlacesForStats(session.kills, session.deaths);
        double sessionFKDR = FormatUtils.roundToTwoDecimalPlacesForStats(session.finalKills, session.finalDeaths);
        double sessionBBLR = FormatUtils.roundToTwoDecimalPlacesForStats(session.bedsBroken, session.bedsLost);
        double sessionWLR = FormatUtils.roundToTwoDecimalPlacesForStats(session.wins, session.losses);

        double sessionWinRate = FormatUtils.formatDecimal((100d / session.gamesPlayed) * session.wins);

        lines.add("§e§lGame");
        lines.add("§fKills: §a" + kills);
        lines.add("§fFinals: §a" + finalKills);
        lines.add("§fBeds: §a" + bedsBroken);
        lines.add("§r");
        lines.add("§e§lSession");
        lines.add("§fKills: §a" + session.kills + " §7| §fKDR: §b" + sessionKDR);
        lines.add("§fFinals: §a" + session.finalKills + " §7| §fFKDR: §b" + sessionFKDR);
        lines.add("§fBeds: §a" + session.bedsBroken + " §7| §fBBLR: §b" + sessionBBLR);
        lines.add("§fWins: §a" + session.wins + " §7| §fWLR: §b" + sessionWLR);
        lines.add("§r");
        lines.add("§e§lExtra");
        lines.add("§fWinstreak: §a" + session.winstreak);
        lines.add("§fWinrate: §a" + sessionWinRate + "%");
        lines.add("§fGames Played: §a" + session.gamesPlayed);
        lines.add("§fSession Time: §b" + formatTime(session.totalSessionTimeTicks));
        if (session.gameTimeTicks > sessionAvgGameTimeTicks) {
            lines.add("§fGame Time: §c" + formatTime(session.gameTimeTicks));
        } else {
            lines.add("§fGame Time: §b" + formatTime(session.gameTimeTicks));
        }
        lines.add("§fAVG Game Time: §b" + formatTime((int) sessionAvgGameTimeTicks));
    }

    public static String formatTime(int ticks) {
        int totalSeconds = ticks / 20;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String formattedSeconds = String.format("%02d", seconds);

        if (hours > 0) {
            return String.format("%d:%02d:%s", hours, minutes, formattedSeconds);
        } else if (minutes > 0) {
            return String.format("%d:%s", minutes, formattedSeconds);
        } else {
            return String.format("0:%s", formattedSeconds);
        }
    }
}
