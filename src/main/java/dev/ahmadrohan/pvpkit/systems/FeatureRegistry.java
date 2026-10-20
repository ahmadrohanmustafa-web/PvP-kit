package dev.ahmadrohan.pvpkit.systems;
import java.util.ArrayList;import java.util.Collections;import java.util.List;
/** Owns all feature modules and provides lookup, reset and diagnostics. */
public final class FeatureRegistry {
 private final List<FeatureModule> modules=new ArrayList<>();private boolean initialized;
 public FeatureRegistry(){
  modules.add(new CrosshairSystem());
  modules.add(new CombatHudSystem());
  modules.add(new LoadoutSystem());
  modules.add(new KeybindSystem());
  modules.add(new AnimationSystem());
  modules.add(new SwordAssistantSystem());
  modules.add(new ShieldAssistantSystem());
  modules.add(new TotemAssistantSystem());
  modules.add(new PotionAssistantSystem());
  modules.add(new InventoryAssistantSystem());
  modules.add(new TargetInfoSystem());
  modules.add(new ComboTrackerSystem());
  modules.add(new CooldownSystem());
  modules.add(new EffectsSystem());
  modules.add(new SettingsSystem());
  modules.add(new HudLayoutSystem());
  modules.add(new SoundCueSystem());
  modules.add(new PerformanceSystem());
  modules.add(new CombatLogSystem());
  modules.add(new ProfileSystem());
  modules.add(new WeaponDurabilitySystem());
  modules.add(new ProjectileAssistantSystem());
  modules.add(new MovementAssistantSystem());
  modules.add(new ResourceTrackerSystem());
  modules.add(new FriendMarkerSystem());
  modules.add(new SessionStatsSystem());
  modules.add(new AccessibilitySystem());
  modules.add(new LayoutPresetSystem());
  modules.add(new WarningSystem());
  modules.add(new TrainingSystem());
  modules.add(new PrivacySystem());
  modules.add(new NotificationSystem());
  modules.add(new QuickActionSystem());
 }
 public synchronized void initializeDefaults(){if(initialized)return;modules.forEach(FeatureModule::resetToDefaults);initialized=true;}
 public synchronized int systemCount(){return modules.size();}public synchronized boolean isInitialized(){return initialized;}
 public synchronized List<FeatureModule> modules(){return Collections.unmodifiableList(new ArrayList<>(modules));}
 public synchronized FeatureModule find(String id){if(id==null)return null;return modules.stream().filter(m->m.id().equalsIgnoreCase(id.trim())).findFirst().orElse(null);}
 public synchronized boolean setEnabled(String id,boolean enabled){FeatureModule m=find(id);if(m==null)return false;m.setEnabled(enabled);return true;}
 public synchronized void resetAll(){modules.forEach(FeatureModule::resetToDefaults);}
 public synchronized List<String> diagnostics(){return modules.stream().map(m->m.id()+": "+m.summary()).toList();}
}
