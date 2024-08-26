package net.errorpnf.bedwarsmod.commands;

import net.errorpnf.bedwarsmod.BedwarsMod;
import net.errorpnf.bedwarsmod.utils.profileviewer.PVGui;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class PVCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "pv";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "does shit";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        BedwarsMod.INSTANCE.openGui = new PVGui();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
