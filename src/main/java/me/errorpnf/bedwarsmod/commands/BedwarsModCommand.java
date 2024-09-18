package me.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.autoupdate.GithubAutoupdater;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class BedwarsModCommand extends CommandBase {
    public static final String pfx = BedwarsMod.prefix;

    @Override
    public String getCommandName() {
        return "bedwarsmod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Command for the Bedwars Mod.";
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
            //SessionStatsHUD.queueReset = true;
            UChat.chat("&aReset session stats.");
        } else if (args[0].equalsIgnoreCase("copytexttoclipboard")) {
            StringBuilder textToCopy = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                textToCopy.append(args[i]).append(" ");
            }

            String text = textToCopy.toString().trim();
            copyTextToClipboard(text);

            sender.addChatMessage(new ChatComponentText(pfx + FormatUtils.format("&7Copied text to the clipboard.")));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private void copyTextToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
}
