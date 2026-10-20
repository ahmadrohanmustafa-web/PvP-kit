package dev.ahmadrohan.pvpkit;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ahmadrohan.pvpkit.systems.FeatureRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actual client-side PvP HUD and controls for Minecraft Java 26.3.
 * This mod is a client companion: it never sends combat actions or modifies server rules.
 */
public final class PvpKitClient implements ClientModInitializer {
    public static final String MOD_ID = "pvpkit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static FeatureRegistry registry;

    private static final String[] LOADOUTS = {
        "CLASSIC", "SWORD + SHIELD", "AXE DUEL", "CRYSTAL", "POTION", "UHC", "ARCHER", "TRAINING"
    };
    private static int loadoutIndex = 0;
    private static boolean hudVisible = true;
    private static boolean customCrosshair = true;
    private static boolean fullBright = false;
    private static double previousGamma = 1.0;
    private static boolean lowFxMode = true;
    private static int previousHotbarSlot = -1;
    private static long slotChangeAt = 0L;
    private static long lastHitFeedbackAt = 0L;
    private static int lastSelectedSlot = -1;
    private static String statusMessage = "PvP-kit ready";
    private static long statusMessageUntil = 0L;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(MOD_ID, "controls")
    );
    private static final KeyMapping MENU_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "key.pvpkit.menu", InputConstants.Type.KEYBOARD, InputConstants.KEY_H, CATEGORY
    ));
    private static final KeyMapping NEXT_LOADOUT_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "key.pvpkit.next_loadout", InputConstants.Type.KEYBOARD, InputConstants.KEY_N, CATEGORY
    ));
    private static final KeyMapping HUD_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "key.pvpkit.toggle_hud", InputConstants.Type.KEYBOARD, InputConstants.KEY_J, CATEGORY
    ));
    private static final KeyMapping CROSSHAIR_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "key.pvpkit.toggle_crosshair", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, CATEGORY
    ));
    private static final KeyMapping BRIGHTNESS_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "key.pvpkit.toggle_brightness", InputConstants.Type.KEYBOARD, InputConstants.KEY_G, CATEGORY
    ));
    private static final KeyMapping PERFORMANCE_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "key.pvpkit.performance_mode", InputConstants.Type.KEYBOARD, InputConstants.KEY_P, CATEGORY
    ));

    @Override
    public void onInitializeClient() {
        registry = new FeatureRegistry();
        registry.initializeDefaults();

        // Replace the vanilla reticle while our precision reticle is enabled, so two crosshairs
        // never overlap. Restore the vanilla one when the user disables the custom crosshair.
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (graphics, tracker) -> {
            if (!customCrosshair) original.extractRenderState(graphics, tracker);
        });

        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(MOD_ID, "combat_hud"),
            PvpKitClient::renderHud
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MENU_KEY.consumeClick()) {
                hudVisible = !hudVisible;
                if (client.player != null) {
                    showStatus(hudVisible ? "HUD / kit panel enabled" : "HUD hidden");
                }
            }
            while (HUD_KEY.consumeClick()) {
                hudVisible = !hudVisible;
                if (client.player != null) {
                    showStatus("HUD " + (hudVisible ? "ON" : "OFF"));
                }
            }
            while (CROSSHAIR_KEY.consumeClick()) {
                customCrosshair = !customCrosshair;
                if (client.player != null) showStatus("Crosshair " + (customCrosshair ? "ON" : "OFF"));
            }
            while (BRIGHTNESS_KEY.consumeClick()) {
                if (client.player != null) {
                    if (!fullBright) {
                        previousGamma = client.options.gamma().get();
                        client.options.gamma().set(1.0);
                        fullBright = true;
                        showStatus("Brightness boost ON");
                    } else {
                        client.options.gamma().set(previousGamma);
                        fullBright = false;
                        showStatus("Brightness restored");
                    }
                }
            }
            while (PERFORMANCE_KEY.consumeClick()) {
                lowFxMode = !lowFxMode;
                showStatus("Low-FX mode " + (lowFxMode ? "ON" : "OFF"));
            }
            while (NEXT_LOADOUT_KEY.consumeClick()) {
                loadoutIndex = (loadoutIndex + 1) % LOADOUTS.length;
                if (client.player != null) {
                    showStatus("Loadout: " + LOADOUTS[loadoutIndex]);
                }
            }
            if (client.player != null) {
                int slot = client.player.getInventory().getSelectedSlot();
                if (lastSelectedSlot != -1 && slot != lastSelectedSlot) {
                    lastHitFeedbackAt = Util.getMillis();
                }
                lastSelectedSlot = slot;
            } else {
                lastSelectedSlot = -1;
            }
        });

        LOGGER.info("PvP-kit initialized: {} feature systems; visible HUD and controls registered.", registry.systemCount());
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null || client.level == null || client.gui.screen() != null) return;

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
        long now = Util.getMillis();
        int cx = width / 2;
        int cy = height / 2;

        // Tiny precision reticle; target feedback uses the actual entity under Minecraft's crosshair.
        if (customCrosshair) {
            boolean targetingLiving = client.crosshairPickEntity instanceof LivingEntity target && target.isAlive();
            int color = targetingLiving ? 0xFF65FF91 : player.isUsingItem() ? 0xFFFFD166 : 0xFFEAF2FF;
            int arm = lowFxMode ? 2 : 3;
            graphics.fill(cx - arm - 2, cy, cx - 2, cy + 1, 0xA0000000);
            graphics.fill(cx + 2, cy, cx + arm + 2, cy + 1, 0xA0000000);
            graphics.fill(cx, cy - arm - 2, cx + 1, cy - 2, 0xA0000000);
            graphics.fill(cx, cy + 2, cx + 1, cy + arm + 2, 0xA0000000);
            graphics.fill(cx - arm - 1, cy, cx - 2, cy + 1, color);
            graphics.fill(cx + 2, cy, cx + arm + 1, cy + 1, color);
            graphics.fill(cx, cy - arm - 1, cx + 1, cy - 2, color);
            graphics.fill(cx, cy + 2, cx + 1, cy + arm + 1, color);
            graphics.fill(cx, cy, cx + 1, cy + 1, 0xFFFFFFFF);

            // Small corner ticks only appear when a living entity is actually under aim.
            if (targetingLiving) {
                int pulse = lowFxMode ? 0 : (int)((now / 180L) % 2L);
                graphics.fill(cx - 5 - pulse, cy - 5 - pulse, cx - 3, cy - 4, 0xCC65FF91);
                graphics.fill(cx + 4, cy - 5 - pulse, cx + 6 + pulse, cy - 4, 0xCC65FF91);
                graphics.fill(cx - 5 - pulse, cy + 4, cx - 3, cy + 5 + pulse, 0xCC65FF91);
                graphics.fill(cx + 4, cy + 4, cx + 6 + pulse, cy + 5 + pulse, 0xCC65FF91);
            }
        }

        if (!hudVisible) return;

        // A compact strip just above the vanilla hotbar; no large corner dashboard.
        int stripY = height - 58;
        String held = selectedItemName(player);
        int totems = player.getInventory().countItem(Items.TOTEM_OF_UNDYING);
        boolean shield = hasShield(player);
        float cooldown = player.getAttackStrengthScale(0.0F);
        String status = held + "   |   Totems " + totems + "   |   Shield " + (player.isBlocking() ? "BLOCKING" : shield ? "READY" : "—");
        int textWidth = client.font.width(status);
        int left = Math.max(4, (width - textWidth) / 2 - 7);
        graphics.fill(left, stripY - 3, Math.min(width - 4, left + textWidth + 14), stripY + 11, 0x760B111A);
        graphics.text(client.font, status, left + 7, stripY, shield ? 0xFFE9F6FF : 0xFFCCD5E0, true);

        // Minimal cooldown bar in the sightline, not in a screen corner.
        int barWidth = 42;
        int barX = cx - barWidth / 2;
        int barY = stripY - 9;
        graphics.fill(barX, barY, barX + barWidth, barY + 2, 0x66000000);
        graphics.fill(barX, barY, barX + (int)(barWidth * Mth.clamp(cooldown, 0.0F, 1.0F)), barY + 2,
            cooldown >= 0.9F ? 0xFF65FF91 : 0xFFFFD166);

        if (player.getHealth() <= 4.0F) {
            graphics.text(client.font, "LOW HP", cx + 12, cy + 9, 0xFFFF6666, true);
        }

        int selectedSlot = player.getInventory().getSelectedSlot();
        if (selectedSlot != previousHotbarSlot) {
            previousHotbarSlot = selectedSlot;
            slotChangeAt = now;
        }
        if (!lowFxMode && now - slotChangeAt < 120L) {
            int alpha = (int)(0x80 * (1.0F - (now - slotChangeAt) / 120.0F));
            graphics.fill(cx - 10, stripY - 5, cx + 10, stripY - 4, (alpha << 24) | 0x65FF91);
        }

        if (now < statusMessageUntil && statusMessage != null && !statusMessage.isBlank()) {
            int messageWidth = client.font.width(statusMessage);
            graphics.text(client.font, statusMessage, (width - messageWidth) / 2, height - 74, 0xFFEAF2FF, true);
        }
    }

    private static void showStatus(String message) {
        statusMessage = message;
        statusMessageUntil = Util.getMillis() + 2200L;
    }

    private static boolean hasShield(Player player) {
        return player.getMainHandItem().is(Items.SHIELD) || player.getOffhandItem().is(Items.SHIELD);
    }

    private static String selectedItemName(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return "Empty hand";
        String name = stack.getHoverName().getString();
        return name.length() > 18 ? name.substring(0, 18) + "…" : name;
    }

    public static FeatureRegistry registry() {
        if (registry == null) throw new IllegalStateException("PvP-kit has not initialized yet");
        return registry;
    }
}
