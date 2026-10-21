package dev.ahmadrohan.pvpkit.mixin;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Makes only the vanilla player-inventory panel translucent; items, text, and interaction stay vanilla.
 * Alpha 0x66 means about 40% opacity / 60% transparency.
 */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Redirect(
        method = "extractBackground",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
        )
    )
    private void pvpkit$makeInventoryPanelTransparent(
        GuiGraphicsExtractor graphics,
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
