package me.errorpnf.bedwarsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Info;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.features.SessionStatsHUD;

public class BedwarsModConfig extends Config {

    @Switch(
            name = "Final Kill Hearts",
            category = "Gameplay",
            description =
                    "Makes your hearts appear to be hardcore hearts \n" +
                            "when you lose your bed.\n\n" +
                    "[WARNING] This may cause incompatibility issues\n" +
                            "with mods such as VanillaHUD."
    )
    public static boolean shouldHardcore = false;

    @Info(
            text = "Will add a way to rearrange/remove certain lines on the session display in the future.",
            category = "Session",
            size = 2,
            type = InfoType.INFO
    )
    public static boolean ignored = true;

    @HUD(
            name = "Session Stats HUD",
            category = "Session"
    )
    public SessionStatsHUD sessionStatsHUD = new SessionStatsHUD();

    public BedwarsModConfig() {
        super(new Mod(BedwarsMod.NAME, ModType.UTIL_QOL, "/assets/bedwarsmod/textures/modicon.png"), BedwarsMod.MODID + ".json");
        initialize();
        save();
    }
}