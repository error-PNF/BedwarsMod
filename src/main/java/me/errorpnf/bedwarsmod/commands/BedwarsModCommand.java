package me.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.autoupdate.GithubAutoupdater;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class BedwarsModCommand extends CommandBase {
    public static final String pfx = FormatUtils.format("&c[&fBW&c] &r");

    @Override
    public String getCommandName() {
        return "bedwarsmod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Command for Bedwars Mod.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            String modVersion = BedwarsMod.VERSION;
            UChat.chat(pfx + "&7You are using &bBedwars Mod v" + modVersion);
        } else if (args[0].equalsIgnoreCase("update")) {
            GithubAutoupdater.downloadAndReplaceMod();
        } else if (args[0].equalsIgnoreCase("getstats")) {
            UChat.chat("there used to be something here");
        } else if (args[0].equalsIgnoreCase("resetsession")) {
            BedwarsMod.config.sessionStatsHUD.getSession().resetSession();
            UChat.chat("&aReset session stats.");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private static void checkForUpdates() {

    }
}
