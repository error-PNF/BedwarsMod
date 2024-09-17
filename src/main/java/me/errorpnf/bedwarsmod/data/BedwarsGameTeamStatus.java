package me.errorpnf.bedwarsmod.data;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BedwarsGameTeamStatus {
    public static String getCurrentTeam() {
        return currentTeam;
    }

    private static String currentTeam = "";

    @SubscribeEvent
    public void updateFinalKillStatus(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft() == null) {
            return;
        } else if (Minecraft.getMinecraft().thePlayer == null) {
            return;
        } else if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return;
        }

        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebar != null) {
            List<Score> scores = new ArrayList<>(scoreboard.getScores());
            scores.sort(Comparator.comparingInt(Score::getScorePoints).reversed());

            List<String> found = scores.stream()
                    .filter(score -> score.getObjective().getName().equals(sidebar.getName()))
                    .map(score -> score.getPlayerName() + getSuffixFromContainingTeam(scoreboard, score.getPlayerName()))
                    .collect(Collectors.toList());

            for (String scoreboardLine : found) {
                if (scoreboardLine.endsWith("§7 YOU")) {
                    currentTeam = findTeamColor(scoreboardLine);
                    return;
                }
            }
        }
    }

    private String getSuffixFromContainingTeam(Scoreboard scoreboard, String playerName) {
        for (ScorePlayerTeam team : scoreboard.getTeams()) {
            if (team != null && team.getMembershipCollection().contains(playerName)) {
                return team.getColorPrefix() + team.getColorSuffix();
            }
        }
        return "";
    }

    private String findTeamColor(String input) {
        for (String color : TEAM_COLORS) {
            if (input.contains(color)) {
                return color;
            }
        }
        return null;
    }

    public static final List<String> TEAM_COLORS = Arrays.asList(
            "Red",
            "Blue",
            "Green",
            "Yellow",
            "Aqua",
            "White",
            "Pink",
            "Gray"
    );
}
