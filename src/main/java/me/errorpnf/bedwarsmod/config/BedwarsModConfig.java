package me.errorpnf.bedwarsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Switch;
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

    @HUD(
            name = "Session Stats HUD",
            category = "Stats"
    )
    public SessionStatsHUD sessionStatsHUD = new SessionStatsHUD();

    public BedwarsModConfig() {
        super(new Mod(BedwarsMod.NAME, ModType.UTIL_QOL, "/assets/bedwarsmod/textures/modicon.png"), BedwarsMod.MODID + ".json");
        initialize();
        save();
    }
}