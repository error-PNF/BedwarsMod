package me.errorpnf.bedwarsmod;

import cc.polyfrost.oneconfig.events.EventManager;
import me.errorpnf.bedwarsmod.commands.BedwarsChatStats;
import me.errorpnf.bedwarsmod.commands.MyCommand;
import me.errorpnf.bedwarsmod.commands.PVCommand;
import me.errorpnf.bedwarsmod.config.BedwarsModConfig;
import me.errorpnf.bedwarsmod.utils.ClickChatForStats;
import me.errorpnf.bedwarsmod.features.FinalKillHearts;
import me.errorpnf.bedwarsmod.utils.HypixelLocraw;
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
        MinecraftForge.EVENT_BUS.register(new ClickChatForStats());
        ClientCommandHandler.instance.registerCommand(new BedwarsChatStats());
        ClientCommandHandler.instance.registerCommand(new PVCommand());
        MinecraftForge.EVENT_BUS.register(this);
        EventManager.INSTANCE.register(new HypixelLocraw());
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