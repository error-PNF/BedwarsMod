package net.errorpnf.bedwarsmod;

import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.errorpnf.bedwarsmod.commands.MyCommand;
import net.errorpnf.bedwarsmod.config.BedwarsModConfig;
import net.errorpnf.bedwarsmod.utils.FinalKillHearts;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
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


    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new BedwarsModConfig();
        MinecraftForge.EVENT_BUS.register(new FinalKillHearts());
        ClientCommandHandler.instance.registerCommand(new MyCommand());
    }
}