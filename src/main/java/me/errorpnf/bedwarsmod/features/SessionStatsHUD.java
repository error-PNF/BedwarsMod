package me.errorpnf.bedwarsmod.features;

import cc.polyfrost.oneconfig.config.annotations.Exclude;
import cc.polyfrost.oneconfig.hud.TextHud;
import me.errorpnf.bedwarsmod.data.BedwarsGameTeamStatus;
import me.errorpnf.bedwarsmod.data.stats.SessionStats;
import net.minecraft.client.Minecraft;

import java.util.List;

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

        lines.add("Current Team: " + BedwarsGameTeamStatus.getCurrentTeam());
        lines.add("Games Started: " + session.gamesStarted);
        lines.add("Beds Broken: " + session.bedsBroken);
        lines.add("Beds Lost: " + session.bedsLost);
        lines.add("Final Kills: " + session.finalKills);
        lines.add("Final Deaths: " + session.finalDeaths);
        lines.add("Kills: " + session.kills);
        lines.add("Deaths: " + session.deaths);
        lines.add("Wins: " + session.wins);
        lines.add("Losses: " + session.losses);
    }
}
