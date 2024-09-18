package me.errorpnf.bedwarsmod;

import cc.polyfrost.oneconfig.events.EventManager;
import me.errorpnf.bedwarsmod.autoupdate.GithubAutoupdater;
import me.errorpnf.bedwarsmod.commands.BedwarsChatStats;
import me.errorpnf.bedwarsmod.commands.BedwarsModCommand;
import me.errorpnf.bedwarsmod.commands.MyCommand;
import me.errorpnf.bedwarsmod.commands.PVCommand;
import me.errorpnf.bedwarsmod.config.BedwarsModConfig;
import me.errorpnf.bedwarsmod.data.BedwarsGameTeamStatus;
import me.errorpnf.bedwarsmod.features.FinalKillHearts;
import me.errorpnf.bedwarsmod.utils.HypixelLocraw;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = BedwarsMod.MODID, name = BedwarsMod.NAME, version = BedwarsMod.VERSION)
public class BedwarsMod {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public GuiScreen openGui = null;
    public static final String prefix = FormatUtils.format("&c[&fBW&c] &r");

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    @Mod.Instance(MODID)
    public static BedwarsMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static BedwarsModConfig config;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new BedwarsModConfig();

        ClientCommandHandler.instance.registerCommand(new BedwarsModCommand());
        ClientCommandHandler.instance.registerCommand(new MyCommand());
        ClientCommandHandler.instance.registerCommand(new BedwarsChatStats());
        ClientCommandHandler.instance.registerCommand(new PVCommand());

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new FinalKillHearts());
        MinecraftForge.EVENT_BUS.register(new BedwarsGameTeamStatus());
        //MinecraftForge.EVENT_BUS.register(new ClickChatForStats());
        EventManager.INSTANCE.register(new HypixelLocraw());


        EventManager.INSTANCE.register(new GithubAutoupdater());

        GithubAutoupdater.init();
//        UpdateManager.init();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (Minecraft.getMinecraft().thePlayer == null) {
            openGui = null;
            return;
        }

        if (openGui != null) {
            if (Minecraft.getMinecraft().thePlayer.openContainer != null) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
            Minecraft.getMinecraft().displayGuiScreen(openGui);
            openGui = null;
        }
    }
}