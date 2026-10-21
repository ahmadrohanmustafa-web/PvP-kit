package dev.ahmadrohan.pvpkit.mixin;

import dev.ahmadrohan.pvpkit.PvpKitClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hides vanilla's large reticle when PvP-kit's compact custom crosshair is enabled. */
@Mixin(Gui.class)
public abstract class CrosshairMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void pvpkit$hideVanillaCrosshair(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci) {
        if (PvpKitClient.isCustomCrosshairEnabled()) ci.cancel();
    }
}
