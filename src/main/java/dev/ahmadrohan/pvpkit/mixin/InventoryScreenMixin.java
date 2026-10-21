package dev.ahmadrohan.pvpkit.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Makes the vanilla inventory panel translucent while keeping item rendering untouched. */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Redirect(
        method = "renderBg",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFII)V"
        ),
        require = 0
    )
    private void pvpkit$makeInventoryPanelTransparent(
        GuiGraphics graphics, ResourceLocation texture, int x, int y,
        float u, float v, int width, int height
    ) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 0.40F);
        graphics.blit(texture, x, y, u, v, width, height);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
