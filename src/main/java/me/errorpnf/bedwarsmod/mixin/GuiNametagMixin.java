package me.errorpnf.bedwarsmod.mixin;

import me.errorpnf.bedwarsmod.features.profileviewer.PVGui;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RendererLivingEntity.class)
public class GuiNametagMixin {
    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private void cancel(CallbackInfoReturnable<Boolean> cir) {
        if (PVGui.renderingNametag) cir.setReturnValue(false);
    }
}
