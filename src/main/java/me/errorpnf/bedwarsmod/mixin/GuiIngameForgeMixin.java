package me.errorpnf.bedwarsmod.mixin;

import me.errorpnf.bedwarsmod.utils.FinalKillHearts;
import me.errorpnf.bedwarsmod.config.BedwarsModConfig;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiIngameForge.class)
public abstract class GuiIngameForgeMixin {
    @Redirect(method = "renderHealth(II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;isHardcoreModeEnabled()Z"))
    private boolean redirectIsHardcoreModeEnabled(WorldInfo worldInfo) {
        return shouldRenderHardcoreHearts();
    }

    private boolean shouldRenderHardcoreHearts() {
        return BedwarsModConfig.shouldHardcore && FinalKillHearts.isFinalKill;
    }
}
