package dev.ahmadrohan.pvpkit.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Makes the vanilla inventory background translucent while leaving item icons and text unchanged. */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Redirect(
        method = "renderBg",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
        ),
        require = 0
    )
    private void pvpkit$makeInventoryPanelTransparent(
        GuiGraphics graphics,
        RenderPipeline pipeline,
        Identifier texture,
        int x,
        int y,
        float u,
        float v,
        int width,
        int height,
        int textureWidth,
        int textureHeight
    ) {
        graphics.blit(pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight, 0x66FFFFFF);
    }
}
