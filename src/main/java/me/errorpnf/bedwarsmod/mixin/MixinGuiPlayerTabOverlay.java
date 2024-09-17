package me.errorpnf.bedwarsmod.mixin;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiPlayerTabOverlay.class)
public interface MixinGuiPlayerTabOverlay {
    @Accessor("footer")
    IChatComponent getTabFooter();

    @Accessor("header")
    IChatComponent getTabHeader();
}