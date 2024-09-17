package me.errorpnf.bedwarsmod.utils;

import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;

public class HypixelLocraw {
    public static String gamemode;
    public static String serverID;
    public static String mapName;
    public static String rawGameType;


    @Subscribe
    public void onWorldJoin(LocrawEvent event) {
        gamemode = event.info.getGameMode();
        serverID = event.info.getServerId();
        mapName = event.info.getMapName();
        rawGameType = event.info.getRawGameType();
    }

    public static boolean isInBedwarsGame() {
        if (rawGameType != null && gamemode != null) {
            return HypixelUtils.INSTANCE.isHypixel() && rawGameType.contains("BEDWARS") && gamemode.contains("BEDWARS");
        }
        return false;
    }
}
