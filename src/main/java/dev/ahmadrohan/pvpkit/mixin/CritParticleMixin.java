package dev.ahmadrohan.pvpkit.mixin;

import net.minecraft.client.particle.CritParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Recolors vanilla critical-hit particles red so they match PvP-kit hit feedback. */
@Mixin(CritParticle.class)
public abstract class CritParticleMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void pvpkit$makeCriticalParticlesRed(CallbackInfo ci) {
        ((CritParticle)(Object)this).setColor(1.0F, 0.02F, 0.02F);
    }
}
