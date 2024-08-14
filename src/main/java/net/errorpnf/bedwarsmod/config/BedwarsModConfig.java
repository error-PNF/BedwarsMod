package net.errorpnf.bedwarsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.annotations.Button;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import net.errorpnf.bedwarsmod.BedwarsMod;
import net.errorpnf.bedwarsmod.utils.FinalKillHearts;

import java.awt.*;
import java.net.URI;
import java.net.URL;

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



    // API Section

    @Button(
            text = "API Key Guide",
            name = "Need help getting your API key? Click for a guide!",
            description = "Click here for a guide on how to acquire a personal Hypixel API key.",
            category = "API"
    )
    Runnable runnable = () -> {    // using a lambda to create the runnable interface.
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI("https://gist.github.com/camnwalter/c0156c68b1e2a21ec0b084c6f04b63f0"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    @Info(
            text = "Do not share this API key with anyone who you don't trust",
            type = InfoType.ERROR,
            size = 2,
            category = "API"

    )
    private static boolean ignored3;

    @Text(
            name = "API Key",
            description = "Your personal Hypixel API key.",
            category = "API",
            secure = true,
            placeholder = "Paste your personal Hypixel API key."
    )
    public static String apiKey = "";


    public BedwarsModConfig() {
        super(new Mod(BedwarsMod.NAME, ModType.UTIL_QOL), BedwarsMod.MODID + ".json");
        initialize();
        save();
    }
}