package net.errorpnf.bedwarsmod;

import net.errorpnf.bedwarsmod.config.BedwarsModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = BedwarsMod.MODID, name = BedwarsMod.NAME, version = BedwarsMod.VERSION)
public class BedwarsMod {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    @Mod.Instance(MODID)
    public static BedwarsMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static BedwarsModConfig config;

    // Roll a new nickname: /nick help setrandom
    // Claim the nickname: /nick actuallyset <generated username from above>
    // Set Rank: /nick rank <rank>

    //TODO Make sure people are already nicked before they can use /betternick stuff. Default /betternick to just /nick maybe? something like that


    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new BedwarsModConfig();
    }
}