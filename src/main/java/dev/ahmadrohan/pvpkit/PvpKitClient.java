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
    private static long lastHitFeedbackAt = 0L;
    private static int lastSelectedSlot = -1;

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

    @Override
    public void onInitializeClient() {
        registry = new FeatureRegistry();
        registry.initializeDefaults();

        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(MOD_ID, "combat_hud"),
            PvpKitClient::renderHud
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MENU_KEY.consumeClick()) {
                hudVisible = !hudVisible;
                if (client.player != null) {
                    client.player.displayClientMessage(Component.literal(
                        hudVisible ? "§bPvP-kit §7» §aHUD / kit panel opened" : "§bPvP-kit §7» §cHUD hidden"
                    ), true);
                }
            }
            while (HUD_KEY.consumeClick()) {
                hudVisible = !hudVisible;
                if (client.player != null) {
                    client.player.displayClientMessage(Component.literal("§bPvP-kit §7» HUD " + (hudVisible ? "§aON" : "§cOFF")), true);
                }
            }
            while (CROSSHAIR_KEY.consumeClick()) {
                customCrosshair = !customCrosshair;
                if (client.player != null) {
                    client.player.displayClientMessage(Component.literal("§bPvP-kit §7» Crosshair " + (customCrosshair ? "§aON" : "§cOFF")), true);
                }
            }
            while (NEXT_LOADOUT_KEY.consumeClick()) {
                loadoutIndex = (loadoutIndex + 1) % LOADOUTS.length;
                if (client.player != null) {
                    client.player.displayClientMessage(Component.literal("§bPvP-kit §7» Loadout: §f" + LOADOUTS[loadoutIndex]), true);
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
        if (player == null || client.level == null || client.options.hideGui) return;

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
        long now = Util.getMillis();

        // Animated, outlined center crosshair. This is an overlay and does not alter aiming.
        if (customCrosshair && client.screen == null) {
            float pulse = (Mth.sin((now % 1000L) / 1000.0F * Mth.TWO_PI) + 1.0F) * 0.5F;
            int gap = 4 + (int)(pulse * 2.0F);
            int color = player.isUsingItem() ? 0xFFFFC857 : 0xFF55F2C7;
            int cx = width / 2;
            int cy = height / 2;
            graphics.fill(cx - gap - 5, cy - 1, cx - gap, cy + 1, 0xB0000000);
            graphics.fill(cx + gap, cy - 1, cx + gap + 5, cy + 1, 0xB0000000);
            graphics.fill(cx - 1, cy - gap - 5, cx + 1, cy - gap, 0xB0000000);
            graphics.fill(cx - 1, cy + gap, cx + 1, cy + gap + 5, 0xB0000000);
            graphics.fill(cx - gap - 4, cy, cx - gap, cy + 1, color);
            graphics.fill(cx + gap, cy, cx + gap + 4, cy + 1, color);
            graphics.fill(cx, cy - gap - 4, cx + 1, cy - gap, color);
            graphics.fill(cx, cy + gap, cx + 1, cy + gap + 4, color);
            graphics.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFFFFFF);
        }

        if (!hudVisible) return;

        // Top-left compact combat card.
        int x = 8;
        int y = 8;
        graphics.fill(x, y, x + 154, y + 76, 0xB8101825);
        graphics.fill(x, y, x + 154, y + 2, 0xFF55F2C7);
        graphics.outline(x, y, 154, 76, 0xAA55F2C7);
        graphics.text(client.font, "PVP-KIT  /  " + LOADOUTS[loadoutIndex], x + 7, y + 7, 0xFFFFFFFF, true);

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int healthColor = health <= 6.0F ? 0xFFFF5555 : health <= 10.0F ? 0xFFFFC857 : 0xFF70F0A8;
        graphics.text(client.font, "❤ " + Mth.ceil(health) + "/" + Mth.ceil(maxHealth), x + 7, y + 21, healthColor, true);
        graphics.fill(x + 7, y + 33, x + 146, y + 38, 0xFF303B4A);
        if (maxHealth > 0.0F) graphics.fill(x + 7, y + 33, x + 7 + (int)(139 * Mth.clamp(health / maxHealth, 0.0F, 1.0F)), y + 38, healthColor);
        graphics.text(client.font, "Armor " + player.getArmorValue() + "   Food " + player.getFoodData().getFoodLevel(), x + 7, y + 42, 0xFFE3EAF4, true);
        graphics.text(client.font, "Totems " + player.getInventory().count(Items.TOTEM_OF_UNDYING)
            + "   Shield " + (player.isBlocking() ? "BLOCKING" : hasShield(player) ? "READY" : "NONE"),
            x + 7, y + 55, 0xFFE3EAF4, true);
        graphics.text(client.font, "Sword / item: " + selectedItemName(player), x + 7, y + 66, 0xFFB6C7D9, false);

        // Cooldown meter and key reminder along the bottom-left.
        int bx = 8;
        int by = height - 30;
        float cooldown = player.getAttackStrengthScale(0.0F);
        graphics.fill(bx, by, bx + 124, by + 12, 0xB8101825);
        graphics.fill(bx + 2, by + 2, bx + 120, by + 10, 0xFF303B4A);
        graphics.fill(bx + 2, by + 2, bx + 2 + (int)(118 * Mth.clamp(cooldown, 0.0F, 1.0F)), by + 10,
            cooldown >= 0.9F ? 0xFF55F2C7 : 0xFFFFC857);
        graphics.text(client.font, "ATTACK " + Mth.floor(cooldown * 100.0F) + "%", bx + 130, by + 2, 0xFFFFFFFF, true);

        if (now - lastHitFeedbackAt < 220L) {
            graphics.fill(width / 2 - 10, height / 2 - 12, width / 2 + 10, height / 2 - 10, 0xFFFFC857);
        }
        if (health <= 6.0F) {
            graphics.fill(width - 132, 8, width - 8, 30, 0xD8A3212B);
            graphics.text(client.font, "LOW HEALTH!", width - 124, 15, 0xFFFFFFFF, true);
        }

        // H opens/closes the quick panel. It remains a HUD overlay so it works without optional GUI libraries.
        if (client.screen == null && (now % 1600L) < 18L) {
            graphics.fill(width - 142, 8, width - 8, 76, 0xA8101825);
            graphics.outline(width - 142, 8, 134, 68, 0x8855F2C7);
            graphics.text(client.font, "PvP KIT KEYS", width - 134, 15, 0xFF55F2C7, true);
            graphics.text(client.font, "H  Kit panel / HUD", width - 134, 29, 0xFFFFFFFF, false);
            graphics.text(client.font, "N  Next loadout", width - 134, 41, 0xFFFFFFFF, false);
            graphics.text(client.font, "J  Toggle HUD", width - 134, 53, 0xFFFFFFFF, false);
            graphics.text(client.font, "K  Crosshair", width - 134, 65, 0xFFFFFFFF, false);
        }
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
