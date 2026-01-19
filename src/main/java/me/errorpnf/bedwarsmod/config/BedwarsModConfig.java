package me.errorpnf.bedwarsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
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
                    "Makes your hearts appear to be hardcore hearts\n" +
                            "when you lose your bed.\n\n" +
                            "[WARNING] This may cause incompatibility issues\n" +
                            "with mods such as VanillaHUD.",
            size = 2
    )
    public static boolean shouldHardcore = false;

    @Switch(
            name = "Auto-Send Stats",
            category = "Gameplay",
            description = "Automatically sends your stats from the last game."
    )
    public static boolean autoSendStats = true;

    @Dropdown(
            name = "Auto-Send Stats Location",
            description = "Choose where the stats are sent to.",
            options = {"Party Chat", "Public Chat", "Guild Chat"},
            category = "Gameplay"
    )
    public static int autoSendStatsLocation = 0;

    @Slider(
            name = "Profile Viewer Scale",
            category = "Profile Viewer",
            description = "Adjust the scale of the Profile Viewer.\n" +
                    "This may cause weird things to happen.",
            min = 1.0f,
            max = 5.0f
    )
    public static float pvGuiScaleFactor = 3;

    @Switch(
            name = "Only Show HUD in Game",
            description = "Disables the Session Stats HUD when you are not\n" +
                    "actively in a Bedwars game. However, this does not\n" +
                    "reset or clear your stats for the session.",
            category = "Session",
            subcategory = "HUD Behavior"
    )
    public static boolean onlyShowHUDWhileInGame = true;

    @Switch(
            name = "Show HUD in Bedwars Lobby",
            description = "Show the Session Stats HUD in Bedwars lobbies.\n" +
                    "Dependent on Only Show HUD in Game switch.",
            category = "Session",
            subcategory = "HUD Behavior"
    )
    public static boolean showHUDInBedwarsLobby = true;

    @Info(
            text = "Will add a way to rearrange/remove certain lines on the session display in the future.",
            category = "Session",
            size = 2,
            type = InfoType.INFO,
            subcategory = "HUD Config"
    )
    public static boolean ignored = true;

    @HUD(
            name = "Session Stats HUD",
            category = "Session",
            subcategory = "HUD Config"
    )
    public static SessionStatsHUD sessionStatsHUD = new SessionStatsHUD();

    public BedwarsModConfig() {
        super(new Mod(BedwarsMod.NAME, ModType.UTIL_QOL, "/assets/bedwarsmod/textures/modicon.png"), BedwarsMod.MODID + ".json");
        initialize();
        addDependency("showHUDInBedwarsLobby", "onlyShowHUDWhileInGame");
        save();
    }
}
