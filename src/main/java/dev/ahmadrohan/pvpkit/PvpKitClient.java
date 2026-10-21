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
import net.minecraft.core.particles.DustParticleOptions;
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
    private static LivingEntity observedTarget = null;
    private static int observedTargetHurtTime = 0;
    private static long hitEffectUntil = 0L;
    private static long lastRedCritBurstAt = 0L;

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
                updateHitEffects(client);
            } else {
                lastSelectedSlot = -1;
                observedTarget = null;
                observedTargetHurtTime = 0;
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

        // Tiny item-status strip beside the hotbar. The old large text panel is removed.
        // Icons/text stay compact so they do not compete with the crosshair or hotbar.
        int stripX = Math.min(width - 62, cx + 101);
        int stripY = height - 61;
        ItemStack mainHand = player.getMainHandItem();
        int totems = player.getInventory().countItem(Items.TOTEM_OF_UNDYING);
        boolean shield = hasShield(player);
        boolean burning = player.isOnFire();
        String weaponText = "W ∞";
        int weaponColor = 0xFFEAF2FF;
        if (!mainHand.isEmpty() && mainHand.isDamageableItem() && mainHand.getMaxDamage() > 0) {
            int durability = Mth.clamp(100 - (int)Math.round(100.0D * mainHand.getDamageValue() / mainHand.getMaxDamage()), 0, 100);
            weaponText = "W " + durability + "%";
            weaponColor = durability > 50 ? 0xFF65FF91 : durability > 20 ? 0xFFFFD166 : 0xFFFF5555;
        }
        graphics.fill(stripX - 3, stripY - 2, Math.min(width - 2, stripX + 48), stripY + 29, 0x500D121A);
        graphics.text(client.font, weaponText, stripX, stripY, weaponColor, true);
        graphics.text(client.font, "T " + totems, stripX, stripY + 10, totems > 0 ? 0xFF65FF91 : 0xFFB8C0CC, true);
        graphics.text(client.font, "SH " + (shield ? "ON" : "OFF"), stripX, stripY + 20, shield ? 0xFF65FF91 : 0xFFB8C0CC, true);
        if (burning) {
            graphics.text(client.font, "FIRE!", stripX + 27, stripY, 0xFFFF633D, true);
        }

        // Potion/status effects appear in a slim list to the right of the item strip.
        int effectY = stripY + 34;
        int effectCount = 0;
        for (var effect : player.getActiveEffects()) {
            if (effectCount >= 5) break;
            String effectName = effect.getEffect().value().getDisplayName().getString();
            if (effectName.length() > 9) effectName = effectName.substring(0, 9);
            int seconds = Math.max(0, effect.getDuration() / 20);
            String duration = seconds >= 60 ? (seconds / 60) + "m" : seconds + "s";
            int effectColor = effect.getAmplifier() > 0 ? 0xFFFFD166 : 0xFFB8E7FF;
            graphics.text(client.font, effectName + " " + duration, stripX, effectY + effectCount * 9, effectColor, true);
            effectCount++;
        }

        // Brief red hit-confirm animation centered below the crosshair.
        if (now < hitEffectUntil) {
            int age = (int)(300L - (hitEffectUntil - now));
            int spread = 3 + age / 35;
            int alpha = Mth.clamp((int)(220.0D * (1.0D - age / 300.0D)), 0, 220);
            int red = (alpha << 24) | 0x00FF3030;
            graphics.fill(cx - spread - 3, cy + 11, cx - spread, cy + 12, red);
            graphics.fill(cx + spread, cy + 11, cx + spread + 3, cy + 12, red);
            graphics.fill(cx - spread - 3, cy + 15, cx - spread, cy + 16, red);
            graphics.fill(cx + spread, cy + 15, cx + spread + 3, cy + 16, red);
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

    private static void updateHitEffects(Minecraft client) {
        if (client.level == null || client.player == null) return;
        if (!(client.hitResult instanceof EntityHitResult hit) || !(hit.getEntity() instanceof LivingEntity target)
                || target == client.player || !target.isAlive()) {
            observedTarget = null;
            observedTargetHurtTime = 0;
            return;
        }
        int hurtTime = target.hurtTime;
        if (observedTarget != target) {
            observedTarget = target;
            observedTargetHurtTime = hurtTime;
            return;
        }
        if (hurtTime > observedTargetHurtTime && hurtTime > 0 && Util.getMillis() - lastRedCritBurstAt > 90L) {
            long now = Util.getMillis();
            hitEffectUntil = now + 300L;
            lastHitFeedbackAt = now;
            lastRedCritBurstAt = now;
            spawnRedCritParticles(client, target);
        }
        observedTargetHurtTime = hurtTime;
    }

    private static void spawnRedCritParticles(Minecraft client, LivingEntity target) {
        if (client.level == null) return;
        int count = lowFxMode ? 5 : 10;
        DustParticleOptions redDust = new DustParticleOptions(0xFFFF0606, 0.9F);
        for (int i = 0; i < count; i++) {
            double x = target.getX() + (Math.random() - 0.5D) * target.getBbWidth();
            double y = target.getY() + target.getBbHeight() * (0.35D + Math.random() * 0.55D);
            double z = target.getZ() + (Math.random() - 0.5D) * target.getBbWidth();
            double vx = (Math.random() - 0.5D) * 0.045D;
            double vy = 0.015D + Math.random() * 0.045D;
            double vz = (Math.random() - 0.5D) * 0.045D;
            client.level.addParticle(redDust, x, y, z, vx, vy, vz);
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
