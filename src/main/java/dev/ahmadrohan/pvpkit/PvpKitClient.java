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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
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

        // Probe up to six blocks along the view ray so the indicator can show red
        // when a living target is visible but still outside the configured threshold.
        // This is visual-only and does not alter Minecraft's attack reach or server rules.
        float partialTick = tickCounter.getGameTimeDeltaPartialTick(false);
        Vec3 eye = player.getEyePosition(partialTick);
        Vec3 view = player.getViewVector(partialTick);
        Vec3 rayEnd = eye.add(view.scale(6.0D));
        AABB searchBox = player.getBoundingBox().expandTowards(view.scale(6.0D)).inflate(1.0D);
        EntityHitResult reachHit = ProjectileUtil.getEntityHitResult(
            player, eye, rayEnd, searchBox,
            entity -> entity instanceof LivingEntity && entity.isAlive() && entity != player,
            36.0D
        );
        LivingEntity aimedTarget = reachHit != null && reachHit.getEntity() instanceof LivingEntity living ? living : null;
        boolean targetingLiving = aimedTarget != null;
        boolean targetInReach = aimedTarget != null && eye.distanceToSqr(reachHit.getLocation()) <= 16.0D;

        // Tiny precision reticle; target feedback uses the actual entity under Minecraft's crosshair.
        if (customCrosshair) {
            int color = targetingLiving ? (targetInReach ? 0xFF65FF91 : 0xFFFF5555) : player.isUsingItem() ? 0xFFFFD166 : 0xFFEAF2FF;
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

            // Small corner ticks show target lock. Their color also reflects the 4-block threshold.
            if (targetingLiving) {
                int tickColor = targetInReach ? 0xCC65FF91 : 0xCCFF5555;
                int pulse = lowFxMode ? 0 : (int)((now / 180L) % 2L);
                graphics.fill(cx - 5 - pulse, cy - 5 - pulse, cx - 3, cy - 4, tickColor);
                graphics.fill(cx + 4, cy - 5 - pulse, cx + 6 + pulse, cy - 4, tickColor);
                graphics.fill(cx - 5 - pulse, cy + 4, cx - 3, cy + 5 + pulse, tickColor);
                graphics.fill(cx + 4, cy + 4, cx + 6 + pulse, cy + 5 + pulse, tickColor);
            }

            // Tiny translucent reach bar: crosshair-width only, 25% opacity.
            if (targetingLiving) {
                int reachColor = targetInReach ? 0x4065FF91 : 0x40FF5555;
                graphics.fill(cx - 3, cy + 7, cx + 4, cy + 9, 0x40000000);
                graphics.fill(cx - 3, cy + 7, cx + 4, cy + 9, reachColor);
            }
        }

        if (!hudVisible) return;

        // Four compact, dark armor slots stacked vertically just left of the vanilla hotbar.
        int armorSlotSize = 16;
        int armorGap = 2;
        ItemStack offhandStack = player.getOffhandItem();
        // Keep the vanilla offhand slot clear: armor stays closer when the offhand is empty,
        // and shifts farther left when an item is equipped there.
        int armorShift = offhandStack.isEmpty() ? 0 : 18;
        int armorX = Math.max(42, cx - 91 - armorSlotSize - 16 - armorShift);
        int armorTop = height - 4 - (armorSlotSize * 4 + armorGap * 3);
        EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}; // top to bottom
        for (int i = 0; i < armorSlots.length; i++) {
            int slotY = armorTop + i * (armorSlotSize + armorGap);
            ItemStack armorStack = player.getItemBySlot(armorSlots[i]);
            String durabilityText = "—";
            int durabilityTextColor = 0xFF687382;
            if (!armorStack.isEmpty() && armorStack.isDamageableItem() && armorStack.getMaxDamage() > 0) {
                int percent = Mth.clamp(100 - (int) Math.round(100.0D * armorStack.getDamageValue() / armorStack.getMaxDamage()), 0, 100);
                durabilityText = percent + "%";
                durabilityTextColor = percent > 50 ? 0xFF65FF91 : percent > 20 ? 0xFFFFD166 : 0xFFFF5555;
            } else if (!armorStack.isEmpty()) {
                durabilityText = "∞";
                durabilityTextColor = 0xFFEAF2FF;
            }
            int numberWidth = client.font.width(durabilityText);
            graphics.text(client.font, durabilityText, armorX - numberWidth - 3, slotY + 4, durabilityTextColor, true);
            graphics.fill(armorX, slotY, armorX + armorSlotSize, slotY + armorSlotSize, 0xB010151D);
            graphics.fill(armorX, slotY, armorX + armorSlotSize, slotY + 1, 0x805B6A7D);
            if (!armorStack.isEmpty()) {
                graphics.item(armorStack, armorX, slotY);
                if (armorStack.isDamageableItem() && armorStack.getMaxDamage() > 0) {
                    float remaining = 1.0F - (float) armorStack.getDamageValue() / (float) armorStack.getMaxDamage();
                    int durabilityWidth = Mth.clamp((int) (14 * remaining), 0, 14);
                    int durabilityColor = remaining > 0.5F ? 0xFF65FF91 : remaining > 0.2F ? 0xFFFFD166 : 0xFFFF5555;
                    graphics.fill(armorX + 1, slotY + 14, armorX + 15, slotY + 15, 0xFF252A31);
                    if (durabilityWidth > 0) graphics.fill(armorX + 1, slotY + 14, armorX + 1 + durabilityWidth, slotY + 15, durabilityColor);
                }
            } else {
                graphics.text(client.font, new String[]{"H", "C", "L", "B"}[i], armorX + 5, slotY + 4, 0xFF687382, false);
            }
        }


        // Advanced food preview: preserve the vanilla hunger bar and outline only the hunger
        // drumsticks this selected food could restore. Gold means preview, not actual healing.
        int currentFood = player.getFoodData().getFoodLevel();
        float currentSaturation = player.getFoodData().getSaturationLevel();
        ItemStack heldFoodStack = player.getMainHandItem();
        FoodProperties heldFood = heldFoodStack.get(DataComponents.FOOD);
        if (heldFood != null) {
            int nutrition = Math.max(0, heldFood.nutrition());
            int recoverableFood = Math.min(nutrition, Math.max(0, 20 - currentFood));
            boolean canEatNow = currentFood < 20 || heldFood.canAlwaysEat();
            int previewPoints = canEatNow ? recoverableFood : 0;
            int previewDrumsticks = (previewPoints + 1) / 2;
            int foodBarRight = cx + 91;
            int foodBarY = height - 39;

            // Vanilla food icons are arranged right-to-left. Draw a fine gold outline around
            // the icons which would gain hunger; do not replace or hide the vanilla icons.
            for (int i = 0; i < Math.min(10, previewDrumsticks); i++) {
                int iconX = foodBarRight - i * 8;
                graphics.fill(iconX - 1, foodBarY - 1, iconX + 10, foodBarY, 0xFFFFD166);
                graphics.fill(iconX - 1, foodBarY + 10, iconX + 10, foodBarY + 11, 0xFFFFD166);
                graphics.fill(iconX - 1, foodBarY, iconX, foodBarY + 10, 0xFFFFD166);
                graphics.fill(iconX + 9, foodBarY, iconX + 10, foodBarY + 10, 0xFFFFD166);
            }

            String foodName = heldFoodStack.getHoverName().getString();
            if (foodName.length() > 18) foodName = foodName.substring(0, 18) + "…";
            float saturationGain = Math.min(
                Math.max(0.0F, nutrition * heldFood.saturation() * 2.0F),
                Math.max(0.0F, Math.min(20.0F, currentFood + recoverableFood) - currentSaturation)
            );
            String previewLine = canEatNow
                ? "🍗 +" + recoverableFood + " hunger  |  Sat +" + String.format(java.util.Locale.ROOT, "%.1f", saturationGain)
                : "HUNGER FULL";
            int foodPanelY = height - 88;
            graphics.text(client.font, foodName, cx - client.font.width(foodName) / 2, foodPanelY, 0xFFFFD166, true);
            graphics.text(client.font, previewLine, cx - client.font.width(previewLine) / 2, foodPanelY + 11,
                canEatNow ? 0xFFEAF2FF : 0xFFFF7777, true);

            String special = foodEffectHint(heldFoodStack);
            if (!special.isEmpty()) {
                graphics.text(client.font, special, cx - client.font.width(special) / 2, foodPanelY + 22, 0xFFB8C0CC, true);
            }
            if (currentFood <= 6) {
                graphics.text(client.font, "WARNING: LOW HUNGER", cx - 66, foodPanelY - 12, 0xFFFF7777, true);
            } else if (currentSaturation <= 1.0F) {
                graphics.text(client.font, "LOW SATURATION", cx - 54, foodPanelY - 12, 0xFFFFD166, true);
            }
            if (!canEatNow) {
                graphics.text(client.font, "Cannot eat until hunger drops", cx - 77, foodPanelY + 33, 0xFFFF7777, true);
            }
        } else {
            // Warn even when no food is selected, without adding permanent HUD clutter.
            if (currentFood <= 6) {
                graphics.text(client.font, "LOW HUNGER", cx - 34, height - 88, 0xFFFF7777, true);
            } else if (currentSaturation <= 1.0F) {
                graphics.text(client.font, "LOW SATURATION", cx - 54, height - 88, 0xFFFFD166, true);
            }
        }

        // Minimal right-side combat stats: symbols and values only, with no panel background.
        int statsX = Math.min(width - 42, cx + 101);
        int statsY = armorTop + 1;
        int statsGap = 17;
        int food = player.getFoodData().getFoodLevel();
        int totems = player.getInventory().countItem(Items.TOTEM_OF_UNDYING);
        int health = Mth.ceil(player.getHealth());
        ItemStack mainHand = player.getMainHandItem();
        String weaponText = "⚔ ∞";
        int weaponColor = 0xFFEAF2FF;
        if (!mainHand.isEmpty() && mainHand.isDamageableItem() && mainHand.getMaxDamage() > 0) {
            int mainDurability = Mth.clamp(100 - (int) Math.round(100.0D * mainHand.getDamageValue() / mainHand.getMaxDamage()), 0, 100);
            weaponText = "⚔ " + mainDurability + "%";
            weaponColor = mainDurability > 50 ? 0xFF65FF91 : mainDurability > 20 ? 0xFFFFD166 : 0xFFFF5555;
        }
        String[] statLines = {"♥ " + health, "● " + food, "✦ " + totems, weaponText};
        int[] statColors = {health <= 4 ? 0xFFFF5555 : 0xFFFF7777, food <= 6 ? 0xFFFFD166 : 0xFFEAF2FF, totems > 0 ? 0xFF65FF91 : 0xFFB8C0CC, weaponColor};
        for (int i = 0; i < statLines.length; i++) {
            graphics.text(client.font, statLines[i], statsX, statsY + i * statsGap, statColors[i], true);
        }

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
            graphics.fill(cx - 10, height - 61, cx + 10, height - 60, (alpha << 24) | 0x65FF91);
        }

        if (now < statusMessageUntil && statusMessage != null && !statusMessage.isBlank()) {
            int messageWidth = client.font.width(statusMessage);
            graphics.text(client.font, statusMessage, (width - messageWidth) / 2, height - 74, 0xFFEAF2FF, true);
        }
    }

    private static String foodEffectHint(ItemStack stack) {
        if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) return "Absorption + Resistance + Regeneration";
        if (stack.is(Items.GOLDEN_APPLE)) return "Absorption + Regeneration";
        if (stack.is(Items.CHORUS_FRUIT)) return "Teleport effect";
        if (stack.is(Items.HONEY_BOTTLE)) return "Removes Poison";
        if (stack.is(Items.SUSPICIOUS_STEW)) return "Special effect depends on recipe";
        if (stack.is(Items.PUFFERFISH)) return "Warning: Poison + Hunger + Nausea";
        if (stack.is(Items.SPIDER_EYE)) return "Warning: Poison";
        if (stack.is(Items.ROTTEN_FLESH)) return "Warning: may cause Hunger";
        if (stack.is(Items.POISONOUS_POTATO)) return "Warning: may cause Poison";
        if (stack.is(Items.POISONOUS_POTATO)) return "Warning: may cause Poison";
        return "";
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
