package me.errorpnf.bedwarsmod.utils.formatting;

import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.data.BedwarsExperience;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.stats.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintChatStats {
    public static final String unfPfx = "&c[&fBW&c] &r";
    public static final String pfx = FormatUtils.format(unfPfx);

    private static final Map<String, List<Integer>> messageIdsByUsername = new HashMap<>();

    public static void printChatStats(String username, JsonObject jsonObject, GameModeEnum gamemode) {
        final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

        clearPreviousMessages(username);

        Stats stats = new Stats(jsonObject, gamemode);

        List<Integer> messageIds = new ArrayList<>();

        IChatComponent overallStats = new ChatComponentText(FormatUtils.format(pfx + "&7Viewing &a" + gamemode.getShortName() + " &7stats for " + stats.formattedRank));
        messageIds.add(80448);
        chat.printChatMessageWithOptionalDeletion(overallStats, 80448);

        // Level
        IChatComponent levelTC = new ChatComponentText(pfx + FormatUtils.format("&7Level: ") + stats.formattedStar)
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                FormatUtils.format(
                                        "&7Level: " + stats.formattedStar + "\n" +
                                                "&7EXP: &b" + stats.currentLevelExperience + "&7/&a" + stats.expReqToLevelUp + "\n" +
                                                stats.formattedStar + BedwarsExperience.getProgressBar(stats.exp) + stats.formattedStarPlusOne
                                ))
                        )));
        messageIds.add(80449);
        chat.printChatMessageWithOptionalDeletion(levelTC, 80449);

        // Wins
        IChatComponent winsTC = new ChatComponentText(pfx + FormatUtils.format("&7Wins: &a" + FormatUtils.formatCommas(stats.wins) + " &8| &7WLR: &b" + stats.wlr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                FormatUtils.format(
                                        "&7Wins: &a" + FormatUtils.formatCommas(stats.wins) + "\n" +
                                                "&7Losses: &c" + FormatUtils.formatCommas(stats.losses) + "\n" +
                                                "&7Wins/Star: &a" + stats.winsPerStar + "\n" +
                                                "&7Losses/Star: &c" + stats.lossesPerStar + "\n" +
                                                "&7WLR: &b" + stats.wlr
                                ))
                        )));
        messageIds.add(80450);
        chat.printChatMessageWithOptionalDeletion(winsTC, 80450);

        // Finals
        IChatComponent finalsTC = new ChatComponentText(pfx + FormatUtils.format("&7Finals: &a" + FormatUtils.formatCommas(stats.finalKills) + " &8| &7FKDR: &b" + stats.fkdr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                FormatUtils.format(
                                        "&7Final Kills: &a" + FormatUtils.formatCommas(stats.finalKills) + "\n" +
                                                "&7Final Deaths: &c" + FormatUtils.formatCommas(stats.finalDeaths) + "\n" +
                                                "&7Finals/Game: &a" + stats.finalsPerGame + "\n" +
                                                "&7FKDR: &b" + stats.fkdr
                                ))
                        )));
        messageIds.add(80451);
        chat.printChatMessageWithOptionalDeletion(finalsTC, 80451);

        // Deaths
        IChatComponent deathsTC = new ChatComponentText(pfx + FormatUtils.format("&7Kills: &a" + FormatUtils.formatCommas(stats.kills) + " &8| &7KDR: &b" + stats.kdr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                FormatUtils.format(
                                        "&7Kills: &a" + FormatUtils.formatCommas(stats.kills) + "\n" +
                                                "&7Deaths: &c" + FormatUtils.formatCommas(stats.deaths) + "\n" +
                                                "&7Kills/Game: &a" + stats.killsPerGame + "\n" +
                                                "&7KDR: &b" + stats.kdr
                                ))
                        )));
        messageIds.add(80452);
        chat.printChatMessageWithOptionalDeletion(deathsTC, 80452);

        // Beds
        IChatComponent bedsTC = new ChatComponentText(pfx + FormatUtils.format("&7Beds: &a" + FormatUtils.formatCommas(stats.beds) + " &8| &7BBLR: &b" + stats.bblr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                FormatUtils.format(
                                        "&7Beds Broken: &a" + FormatUtils.formatCommas(stats.beds) + "\n" +
                                                "&7Beds Lost: &c" + FormatUtils.formatCommas(stats.bedsLost) + "\n" +
                                                "&7Beds/Game: &a" + stats.bedsPerGame + "\n" +
                                                "&7KDR: &b" + stats.bblr
                                ))
                        )));
        messageIds.add(80453);
        chat.printChatMessageWithOptionalDeletion(bedsTC, 80453);

        // pv
        IChatComponent pv = new ChatComponentText(pfx + FormatUtils.format("&7For a more in depth look, click &b&nhere&r&7!"))
                .setChatStyle(
                        new ChatStyle().
                                setChatClickEvent(new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND, "/pv " + username
                                ))
                                .setChatHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                        FormatUtils.format(
                                                "&7Runs the command \n&b/pv " + stats.displayUsername
                                        )
                                )
                                )));
        messageIds.add(80454);
        chat.printChatMessageWithOptionalDeletion(pv, 80454);

        // save message ids for this username
        messageIdsByUsername.put(username, messageIds);

        printChatModes(stats.displayUsername, gamemode);
    }

    private static void clearPreviousMessages(String username) {
        final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

        List<Integer> messageIds = messageIdsByUsername.get(username);
        if (messageIds != null) {
            for (Integer messageId : messageIds) {
                chat.deleteChatLine(messageId);
            }
            messageIdsByUsername.remove(username); // clear the list after deletion
        }
    }



    public static void printChatModes(String username, GameModeEnum currentMode) {
        IChatComponent message = new ChatComponentText(FormatUtils.format(pfx));

        message.appendSibling(createModeComponent(username, "Overall", GameModeEnum.OVERALL, currentMode));
        message.appendSibling(createModeComponent(username, "4s", GameModeEnum.FOUR_FOUR, currentMode));
        message.appendSibling(createModeComponent(username, "3s", GameModeEnum.FOUR_THREE, currentMode));
        message.appendSibling(createModeComponent(username, "2s", GameModeEnum.EIGHT_TWO, currentMode));
        message.appendSibling(createModeComponent(username, "1s", GameModeEnum.EIGHT_ONE, currentMode));

        final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        chat.printChatMessageWithOptionalDeletion(message, 80455);
    }

    private static IChatComponent createModeComponent(String username, String modeText, GameModeEnum mode, GameModeEnum currentMode) {
        String command = "/bedwars " + username + " " + mode.getShortName();
        String formattedModeText = FormatUtils.format(currentMode.equals(mode) ? "&7[&a" + modeText + "&7] " : "&7[&b" + modeText + "&7] ");

        return new ChatComponentText(formattedModeText)
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(FormatUtils.format("&7Click to view " + modeText + " stats")))));
    }
}
