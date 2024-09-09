package me.errorpnf.bedwarsmod.utils.formatting;

import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.utils.StatUtils;

public class RankUtils {

    public static String formatRankAndUsername(String username, JsonObject apiReq) {
        StatUtils stats = new StatUtils(apiReq);

        String packageRank = stats.getStat("player.newPackageRank");

        String prefix = stats.getStat("player.prefix");
        if (!prefix.equals("0")) {
            return prefix + " " + username;
        }

        String specialRank = stats.getStat("player.rank");
        if (specialRank.equalsIgnoreCase("ADMIN")) {
            return "§c[ADMIN] " + username;
        } else if (specialRank.equalsIgnoreCase("GAME_MASTER")) {
            return "§2[GM] " + username;
        } if (specialRank.equalsIgnoreCase("YOUTUBER")) {
            return "§c[§fYOUTUBE§c] " + username;
        }

        switch (packageRank) {
            case "VIP":
                return "&a[VIP] " + username;
            case "VIP_PLUS":
                return "&a[VIP&6+&a] " + username;
            case "MVP":
                return "&b[MVP] " + username;
            case "MVP_PLUS":
                String monthlyPackageRank = stats.getStat("player.monthlyPackageRank");
                String plusColor = getColorCode(stats.getStat("player.rankPlusColor"));
                String rankColor = getColorCode(stats.getStat("player.monthlyRankColor"));

                if (plusColor.isEmpty()) {
                    plusColor = "&c";
                }

                if (rankColor.isEmpty()) {
                    rankColor = "&6";
                }

                if (monthlyPackageRank.equals("SUPERSTAR")) {
                    if (!rankColor.equals("&6")) {
                        rankColor = "&b";
                    }
                    return rankColor + "[MVP" + plusColor + "++" + rankColor + "] " + username;
                } else {
                    return "&b[MVP" + plusColor + "+" + "&b] " + username;
                }
            default:
                return "&7" + username;
        }
    }

    private static String getColorCode(String colorName) {
        switch (colorName) {
            case "DARK_AQUA": return "&3";
            case "DARK_BLUE": return "&1";
            case "DARK_GREEN": return "&2";
            case "DARK_RED": return "&4";
            case "DARK_PURPLE": return "&5";
            case "GOLD": return "&6";
            case "GRAY": return "&7";
            case "DARK_GRAY": return "&8";
            case "BLUE": return "&9";
            case "GREEN": return "&a";
            case "AQUA": return "&b";
            case "RED": return "&c";
            case "LIGHT_PURPLE": return "&d";
            case "YELLOW": return "&e";
            case "WHITE": return "&f";
            case "BLACK": return "&0";
            default: return ""; // Default color
        }
    }
}
