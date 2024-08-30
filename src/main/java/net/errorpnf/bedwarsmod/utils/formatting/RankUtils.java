package net.errorpnf.bedwarsmod.utils.formatting;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.utils.StatUtils;

public class RankUtils {

    public static String formatRankAndUsername(String username, JsonObject apiReq) {
        StatUtils s = new StatUtils(apiReq);

        String packageRank = s.getStat("player.newPackageRank");

        if (packageRank.equals("VIP")) {
            return "&a[VIP] " + username;
        } else if (packageRank.equals("VIP_PLUS")) {
            return "&a[VIP&6+&a] " + username;
        } else if (packageRank.equals("MVP")) {
            return "&b[MVP] " + username;
        } else if (packageRank.equals("MVP_PLUS")) {
            String monthlyPackageRank = s.getStat("player.monthlyPackageRank");
            String plusColor = getColorCode(s.getStat("player.rankPlusColor"));
            String rankColor = getColorCode(s.getStat("player.monthlyRankColor"));

            if (plusColor.isEmpty()) {
                plusColor = "&c";
            }

            if (rankColor.isEmpty()) {
                rankColor = "&6";
            }

            if (monthlyPackageRank.equals("SUPERSTAR")) {
                if (rankColor.equals("&6")) {
                    return rankColor + "[MVP" + plusColor + "++" + rankColor + "] " + username;
                } else {
                    rankColor = "&b";
                    return rankColor + "[MVP" + plusColor + "++" + rankColor + "] " + username;
                }
            } else {
                return "&b[MVP" + plusColor + "+" + "&b] " + username;
            }
        } else {
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
