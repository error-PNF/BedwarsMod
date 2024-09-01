package net.errorpnf.bedwarsmod.utils.formatting;

import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.data.BedwarsExperience;
import net.errorpnf.bedwarsmod.data.GameModeEnum;
import net.errorpnf.bedwarsmod.data.stats.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.text.NumberFormat;
import java.util.*;

public class PrintChatStats {
    public static final String unfPfx = "&c[&fBW&c] &r";
    public static final String pfx = format(unfPfx);

    private static final Map<String, List<Integer>> messageIdsByUsername = new HashMap<>();

    public static void printChatStats(String username, JsonObject jsonObject, GameModeEnum gamemode) {
        final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

        clearPreviousMessages(username);

        Stats s = new Stats(jsonObject, gamemode);

        List<Integer> messageIds = new ArrayList<>();

        IChatComponent overallStats = new ChatComponentText(format(pfx + "&7Viewing &a" + gamemode.getShortName() + " &7stats for " + s.formattedRank));
        messageIds.add(80448);
        chat.printChatMessageWithOptionalDeletion(overallStats, 80448);

        // Level
        IChatComponent levelTC = new ChatComponentText(pfx + format("&7Level: ") + s.formattedStar)
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                format(
                                        "&7Level: " + s.formattedStar + "\n" +
                                                "&7EXP: &b" + s.currentLevelExperience + "&7/&a" + s.expReqToLevelUp + "\n" +
                                                s.formattedStar + BedwarsExperience.getProgressBar(s.exp) + s.formattedStarPlusOne
                                ))
                        )));
        messageIds.add(80449);
        chat.printChatMessageWithOptionalDeletion(levelTC, 80449);

        // Wins
        IChatComponent winsTC = new ChatComponentText(pfx + format("&7Wins: &a" + fc(s.wins) + " &8| &7WLR: &b" + s.wlr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                format(
                                        "&7Wins: &a" + fc(s.wins) + "\n" +
                                                "&7Losses: &c" + fc(s.losses) + "\n" +
                                                "&7Wins/Star: &a" + s.winsPerStar + "\n" +
                                                "&7Losses/Star: &c" + s.lossesPerStar + "\n" +
                                                "&7WLR: &b" + s.wlr
                                ))
                        )));
        messageIds.add(80450);
        chat.printChatMessageWithOptionalDeletion(winsTC, 80450);

        // Finals
        IChatComponent finalsTC = new ChatComponentText(pfx + format("&7Finals: &a" + fc(s.finalKills) + " &8| &7FKDR: &b" + s.fkdr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                format(
                                        "&7Final Kills: &a" + fc(s.finalKills) + "\n" +
                                                "&7Final Deaths: &c" + fc(s.finalDeaths) + "\n" +
                                                "&7Finals/Game: &a" + s.finalsPerGame + "\n" +
                                                "&7FKDR: &b" + s.fkdr
                                ))
                        )));
        messageIds.add(80451);
        chat.printChatMessageWithOptionalDeletion(finalsTC, 80451);

        // Deaths
        IChatComponent deathsTC = new ChatComponentText(pfx + format("&7Kills: &a" + fc(s.kills) + " &8| &7KDR: &b" + s.kdr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                format(
                                        "&7Kills: &a" + fc(s.kills) + "\n" +
                                                "&7Deaths: &c" + fc(s.deaths) + "\n" +
                                                "&7Kills/Game: &a" + s.killsPerGame + "\n" +
                                                "&7KDR: &b" + s.kdr
                                ))
                        )));
        messageIds.add(80452);
        chat.printChatMessageWithOptionalDeletion(deathsTC, 80452);

        // Beds
        IChatComponent bedsTC = new ChatComponentText(pfx + format("&7Beds: &a" + fc(s.beds) + " &8| &7BBLR: &b" + s.bblr))
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                format(
                                        "&7Beds Broken: &a" + fc(s.beds) + "\n" +
                                                "&7Beds Lost: &c" + fc(s.bedsLost) + "\n" +
                                                "&7Beds/Game: &a" + s.bedsPerGame + "\n" +
                                                "&7KDR: &b" + s.bblr
                                ))
                        )));
        messageIds.add(80453);
        chat.printChatMessageWithOptionalDeletion(bedsTC, 80453);

        // pv
        IChatComponent pv = new ChatComponentText(pfx + format("&7For a more in depth look, click &b&nhere&r&7!"))
                .setChatStyle(
                        new ChatStyle().
                                setChatClickEvent(new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND, "/pv " + username
                                ))
                                .setChatHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                        format(
                                                "&7Runs the command \n&b/pv " + s.displayUsername
                                        )
                                )
                                )));
        messageIds.add(80454);
        chat.printChatMessageWithOptionalDeletion(pv, 80454);

        // save message ids for this username
        messageIdsByUsername.put(username, messageIds);

        printChatModes(s.displayUsername, gamemode);
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

    private static String format(String string) {
        return string.replaceAll("&", "§");
    }

    public static String fc(int number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(number);
    }

    public static void printChatModes(String username, GameModeEnum currentMode) {
        IChatComponent message = new ChatComponentText(format(pfx));

        message.appendSibling(createModeComponent(username, "Overall", GameModeEnum.OVERALL, currentMode));
        message.appendSibling(createModeComponent(username, "4s", GameModeEnum.FOUR_FOUR, currentMode));
        message.appendSibling(createModeComponent(username, "3s", GameModeEnum.FOUR_THREE, currentMode));
        message.appendSibling(createModeComponent(username, "2s", GameModeEnum.EIGHT_TWO, currentMode));
        message.appendSibling(createModeComponent(username, "1s", GameModeEnum.EIGHT_ONE, currentMode));

        final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        chat.printChatMessageWithOptionalDeletion(message, 80455);
    }

    private static IChatComponent createModeComponent(String username, String modeText, GameModeEnum mode, GameModeEnum currentMode) {
        String command = "/bws " + username + " " + mode.getShortName();
        String formattedModeText = format(currentMode.equals(mode) ? "&7[&a" + modeText + "&7] " : "&7[&b" + modeText + "&7] ");

        return new ChatComponentText(formattedModeText)
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(format("&7Click to view " + modeText + " stats")))));
    }
}
