package net.errorpnf.bedwarsmod.utils.profileviewer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class PVGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int i, EntityPlayer entityPlayer, World world, int j, int k, int l) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int i, EntityPlayer entityPlayer, World world, int j, int k, int l) {
        return null;
    }
}
