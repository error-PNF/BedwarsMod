package net.errorpnf.bedwarsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import net.errorpnf.bedwarsmod.BedwarsMod;
import net.errorpnf.bedwarsmod.utils.FinalKillHearts;

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

    @Switch(name = "temp")
    public static boolean bol = false;


    public BedwarsModConfig() {
        super(new Mod(BedwarsMod.NAME, ModType.UTIL_QOL), BedwarsMod.MODID + ".json");
        initialize();
        //registerKeyBind(autoRerollKeybind, AutoReroll::toggleAutoReroll);
        //registerKeyBind(rerollNickKeybind, BetterNickCommand::rerollNick);
        //registerKeyBind(claimNameKeybind, BetterNickCommand::claimNick);
        save();
    }
}