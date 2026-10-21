package dev.ahmadrohan.pvpkit;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ahmadrohan.pvpkit.systems.FeatureRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PvpKitClient implements ClientModInitializer {
    public static final String MOD_ID = "pvpkit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static FeatureRegistry registry;

    private static final String[] LOADOUTS = {
        "CLASSIC", "SWORD + SHIELD", "AXE DUEL", "CRYSTAL", "POTION", "UHC", "ARCHER", "TRAINING"
    };
    private static int loadoutIndex;
    private static boolean hudVisible = true;
    private static boolean customCrosshair = true;
    private static boolean fullBright;
    private static double previousGamma = 1.0;
    private static boolean lowFxMode = true;
    private static int previousHotbarSlot = -1;
    private static long slotChangeAt;
    private static long hitEffectUntil;
    private static long lastRedCritBurstAt;
    private static int lastSelectedSlot = -1;
    private static String statusMessage = "PvP-kit ready";
    private static long statusMessageUntil;
    private static LivingEntity observedTarget;
    private static int observedTargetHurtTime;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        ResourceLocation.fromNamespaceAndPath(MOD_ID, "controls")
    );
    private static final KeyMapping MENU_KEY = register("key.pvpkit.menu", GLFW.GLFW_KEY_H);
    private static final KeyMapping NEXT_LOADOUT_KEY = register("key.pvpkit.next_loadout", GLFW.GLFW_KEY_N);
    private static final KeyMapping HUD_KEY = register("key.pvpkit.toggle_hud", GLFW.GLFW_KEY_J);
    private static final KeyMapping CROSSHAIR_KEY = register("key.pvpkit.toggle_crosshair", GLFW.GLFW_KEY_K);
    private static final KeyMapping BRIGHTNESS_KEY = register("key.pvpkit.toggle_brightness", GLFW.GLFW_KEY_G);
    private static final KeyMapping PERFORMANCE_KEY = register("key.pvpkit.performance_mode", GLFW.GLFW_KEY_P);

    private static KeyMapping register(String translationKey, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(
            translationKey, InputConstants.Type.KEYSYM, key, CATEGORY
        ));
    }

    @Override
    public void onInitializeClient() {
        registry = new FeatureRegistry();
        registry.initializeDefaults();

        HudRenderCallback.EVENT.register(PvpKitClient::renderHud);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MENU_KEY.consumeClick()) {
                hudVisible = !hudVisible;
                if (client.player != null) showStatus(hudVisible ? "HUD / kit panel enabled" : "HUD hidden");
            }
            while (HUD_KEY.consumeClick()) {
                hudVisible = !hudVisible;
                if (client.player != null) showStatus("HUD " + (hudVisible ? "ON" : "OFF"));
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
                if (client.player != null) showStatus("Low-FX mode " + (lowFxMode ? "ON" : "OFF"));
            }
            while (NEXT_LOADOUT_KEY.consumeClick()) {
                loadoutIndex = (loadoutIndex + 1) % LOADOUTS.length;
                if (client.player != null) showStatus("Loadout: " + LOADOUTS[loadoutIndex]);
            }
            if (client.player != null) {
                int slot = client.player.getInventory().getSelectedSlot();
                if (lastSelectedSlot != -1 && slot != lastSelectedSlot) slotChangeAt = Util.getMillis();
                lastSelectedSlot = slot;
                updateHitEffects(client);
            } else {
                lastSelectedSlot = -1;
                observedTarget = null;
                observedTargetHurtTime = 0;
            }
        });

        LOGGER.info("PvP-kit 1.21.11 initialized: {} feature systems registered.", registry.systemCount());
    }

    private static void renderHud(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null || client.level == null || client.screen != null) return;

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
        long now = Util.getMillis();
        int cx = width / 2;
        int cy = height / 2;

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

        if (customCrosshair) {
            int color = targetingLiving ? (targetInReach ? 0xFF65FF91 : 0xFFFF5555)
                : player.isUsingItem() ? 0xFFFFD166 : 0xFFEAF2FF;
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
            if (targetingLiving) {
                int tickColor = targetInReach ? 0xCC65FF91 : 0xCCFF5555;
                int pulse = lowFxMode ? 0 : (int)((now / 180L) % 2L);
                graphics.fill(cx - 5 - pulse, cy - 5 - pulse, cx - 3, cy - 4, tickColor);
                graphics.fill(cx + 4, cy - 5 - pulse, cx + 6 + pulse, cy - 4, tickColor);
                graphics.fill(cx - 5 - pulse, cy + 4, cx - 3, cy + 5 + pulse, tickColor);
                graphics.fill(cx + 4, cy + 4, cx + 6 + pulse, cy + 5 + pulse, tickColor);
                int reachColor = targetInReach ? 0x4065FF91 : 0x40FF5555;
                graphics.fill(cx - 3, cy + 7, cx + 4, cy + 9, 0x40000000);
                graphics.fill(cx - 3, cy + 7, cx + 4, cy + 9, reachColor);
            }
        }

        if (!hudVisible) return;

        int armorSlotSize = 16;
        int armorGap = 2;
        ItemStack offhandStack = player.getOffhandItem();
        int armorShift = offhandStack.isEmpty() ? 0 : 18;
        int armorX = Math.max(42, cx - 91 - armorSlotSize - 16 - armorShift);
        int armorTop = height - 4 - (armorSlotSize * 4 + armorGap * 3);
        EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < armorSlots.length; i++) {
            int slotY = armorTop + i * (armorSlotSize + armorGap);
            ItemStack armorStack = player.getItemBySlot(armorSlots[i]);
            String durabilityText = "—";
            int durabilityTextColor = 0xFF687382;
            if (!armorStack.isEmpty() && armorStack.isDamageableItem() && armorStack.getMaxDamage() > 0) {
                int percent = Mth.clamp(100 - (int)Math.round(100.0D * armorStack.getDamageValue() / armorStack.getMaxDamage()), 0, 100);
                durabilityText = percent + "%";
                durabilityTextColor = percent > 50 ? 0xFF65FF91 : percent > 20 ? 0xFFFFD166 : 0xFFFF5555;
            } else if (!armorStack.isEmpty()) {
                durabilityText = "∞";
                durabilityTextColor = 0xFFEAF2FF;
            }
            int numberWidth = client.font.width(durabilityText);
            graphics.drawString(client.font, durabilityText, armorX - numberWidth - 3, slotY + 4, durabilityTextColor, true);
            graphics.fill(armorX, slotY, armorX + armorSlotSize, slotY + armorSlotSize, 0xB010151D);
            graphics.fill(armorX, slotY, armorX + armorSlotSize, slotY + 1, 0x805B6A7D);
            if (!armorStack.isEmpty()) {
                graphics.renderItem(armorStack, armorX, slotY);
                if (armorStack.isDamageableItem() && armorStack.getMaxDamage() > 0) {
                    float remaining = 1.0F - (float)armorStack.getDamageValue() / armorStack.getMaxDamage();
                    int durabilityWidth = Mth.clamp((int)(14 * remaining), 0, 14);
                    int durabilityColor = remaining > 0.5F ? 0xFF65FF91 : remaining > 0.2F ? 0xFFFFD166 : 0xFFFF5555;
                    graphics.fill(armorX + 1, slotY + 14, armorX + 15, slotY + 15, 0xFF252A31);
                    if (durabilityWidth > 0) graphics.fill(armorX + 1, slotY + 14, armorX + 1 + durabilityWidth, slotY + 15, durabilityColor);
                }
            } else {
                graphics.drawString(client.font, new String[]{"H", "C", "L", "B"}[i], armorX + 5, slotY + 4, 0xFF687382, false);
            }
        }

        // Vanilla-style potion icons with remaining duration; effects stack vertically with no hard cap.
        int effectIndex = 0;
        int effectX = Math.min(width - 22, cx + 94);
        for (MobEffectInstance effect : player.getActiveEffects()) {
            if (!effect.showIcon()) continue;
            int effectY = height - 54 - effectIndex * 22;
            ResourceLocation effectSprite = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                .getKey(effect.getEffect().value());
            if (effectSprite != null) {
                effectSprite = effectSprite.withPrefix("mob_effect/");
                graphics.blitSprite(effectSprite, effectX, effectY, 18, 18);
            }
            int durationTicks = effect.getDuration();
            String durationText;
            if (durationTicks < 0) {
                durationText = "∞";
            } else {
                int totalSeconds = durationTicks / 20;
                durationText = String.format(java.util.Locale.ROOT, "%d:%02d", totalSeconds / 60, totalSeconds % 60);
            }
            int durationWidth = client.font.width(durationText);
            graphics.drawString(client.font, durationText, effectX - durationWidth - 4, effectY + 5, 0xFFEAF2FF, true);
            effectIndex++;
        }

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

        if (player.getHealth() <= 4.0F) graphics.drawString(client.font, "LOW HP", cx + 12, cy + 9, 0xFFFF6666, true);

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
            graphics.drawString(client.font, statusMessage, (width - messageWidth) / 2, height - 74, 0xFFEAF2FF, true);
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

    public static FeatureRegistry registry() {
        if (registry == null) throw new IllegalStateException("PvP-kit has not initialized yet");
        return registry;
    }
}
