package me.errorpnf.bedwarsmod.utils;

import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;

public class HypixelLocraw {

    public static boolean isInBedwarsGame() {
        LocrawInfo locrawInfo = LocrawUtil.INSTANCE.getLocrawInfo();
        if (locrawInfo == null) {
            return false;
        }
        return locrawInfo.getServerId().toLowerCase().contains("lobby") && locrawInfo.getGameType() != LocrawInfo.GameType.BEDWARS;
    }

    public static boolean isInBedwarsLobby() {
        LocrawInfo locrawInfo = LocrawUtil.INSTANCE.getLocrawInfo();
        if (locrawInfo == null) {
            return false;
        }
        return !locrawInfo.getServerId().toLowerCase().contains("lobby") && locrawInfo.getGameType() != LocrawInfo.GameType.BEDWARS;
    }


}
