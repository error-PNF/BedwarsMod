package net.errorpnf.bedwarsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import net.errorpnf.bedwarsmod.BedwarsMod;

public class BedwarsModConfig extends Config {

    public BedwarsModConfig() {
        super(new Mod(BedwarsMod.NAME, ModType.UTIL_QOL), BedwarsMod.MODID + ".json");
        initialize();
        //registerKeyBind(autoRerollKeybind, AutoReroll::toggleAutoReroll);
        //registerKeyBind(rerollNickKeybind, BetterNickCommand::rerollNick);
        //registerKeyBind(claimNameKeybind, BetterNickCommand::claimNick);
        save();
    }
}