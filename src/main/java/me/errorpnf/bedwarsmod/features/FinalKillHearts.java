package me.errorpnf.bedwarsmod.features;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FinalKillHearts {

    public static boolean isFinalKill = false;

    @SubscribeEvent
    public void updateFinalKillStatus(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc == null) {
            return;
        } else if (mc.thePlayer == null) {
            return;
        } else if (mc.theWorld == null) {
            return;
        }

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

            for (String s : found) {
                Pattern pattern = Pattern.compile("§a[0-9]§7 YOU");
                Matcher matcher = pattern.matcher(s);

                if (s.endsWith("§a§l✓§7 YOU")) {
                    isFinalKill = false;
                    return;
                } else if (matcher.find()) {
                    isFinalKill = true;
                    return;
                } else {
                    isFinalKill = false;
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
}
