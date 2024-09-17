package me.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import me.errorpnf.bedwarsmod.mixin.MixinGuiPlayerTabOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;


public class MyCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "getscores";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }


    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        MixinGuiPlayerTabOverlay tabData = (MixinGuiPlayerTabOverlay) Minecraft.getMinecraft().ingameGUI.getTabList();
        UChat.chat(tabData.getTabHeader());
        UChat.chat(tabData.getTabFooter());
        tabData.getTabFooter().getUnformattedText();
//        Minecraft minecraft = Minecraft.getMinecraft();
//        Scoreboard scoreboard = minecraft.theWorld.getScoreboard();
//        if (scoreboard == null) {
//            return;
//        }
//
//        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
//        if (sidebar != null) {
//            List<Score> scores = new ArrayList<>(scoreboard.getScores());
//
//            /*
//               Scores retrieved here do not care for ordering, this is done by the Scoreboard itself.
//               We'll need to do this ourselves in this case.
//               This will appear backwards in chat, but remember that the scoreboard reverses this order
//               to ensure highest scores go first.
//            */
//            scores.sort(Comparator.comparingInt(Score::getScorePoints).reversed());
//
//            List<String> found = scores.stream()
//                    .filter(score -> score.getObjective().getName().equals(sidebar.getName()))
//                    .map(score -> score.getPlayerName() + getSuffixFromContainingTeam(scoreboard, score.getPlayerName()))
//                    .collect(Collectors.toList());
//
//            for (String s : found) {
//                UChat.chat(s);
//                System.out.println(s);
//            }
//        }
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