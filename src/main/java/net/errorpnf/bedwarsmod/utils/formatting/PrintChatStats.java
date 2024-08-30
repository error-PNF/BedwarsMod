package net.errorpnf.bedwarsmod.utils.formatting;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import cc.polyfrost.oneconfig.libs.universal.wrappers.message.UTextComponent;
import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.data.BedwarsExperience;
import net.errorpnf.bedwarsmod.data.GameModeEnum;
import net.errorpnf.bedwarsmod.data.PrestigeList;
import net.errorpnf.bedwarsmod.data.stats.Stats;
import net.errorpnf.bedwarsmod.utils.StatUtils;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.text.NumberFormat;
import java.util.Locale;

public class PrintChatStats {
    public static final String pfx = "&c[&fBW&c] &r";

    public static void printChatStats(String username, JsonObject jsonObject) {
        Stats s = new Stats(jsonObject);

        UChat.chat((pfx + "&7Viewing overall stats for " + s.formattedRank));

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
        UChat.chat(levelTC);

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
        UChat.chat(winsTC);

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
        UChat.chat(finalsTC);

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
        UChat.chat(deathsTC);

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
        UChat.chat(bedsTC);

        IChatComponent pv = new ChatComponentText(pfx + format("&7For a more in depth look, click &b&nhere&r&7!"))
                .setChatStyle(
                        new ChatStyle().setChatClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND, "/pv " + username
                )));
        UChat.chat(pv);
    }

    private static String format(String string) {
        return string.replaceAll("&", "§");
    }

    public static String fc(int number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(number);
    }
}
