package dev.ahmadrohan.pvpkit.systems;

import java.util.List;
import java.util.Map;

/** Feature-specific configuration classes. Kept in one compilation unit to make the initial code drop easy to navigate. */
final class FeatureModules { private FeatureModules() {} }

final class CrosshairSystem extends FeatureModule {
    private static final String[] DEFAULTS={"center_dot","hit_marker","outline","custom_color","opacity","scale","spread","fade_on_damage","critical_indicator","projectile_hint","block_hint","ally_color","enemy_color","dynamic_gap","hit_feedback"};
    CrosshairSystem(){super("crosshair",DEFAULTS);}
    @Override synchronized String summary(){return "crosshair: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read center dot preference. */
    synchronized boolean centerDotEnabled(){return option("center_dot");}
    /** Update center dot preference. */
    synchronized boolean setCenterDot(boolean v){return setOption("center_dot",v)&&option("center_dot")==v;}

    /** Read hit marker preference. */
    synchronized boolean hitMarkerEnabled(){return option("hit_marker");}
    /** Update hit marker preference. */
    synchronized boolean setHitMarker(boolean v){return setOption("hit_marker",v)&&option("hit_marker")==v;}

    /** Read outline preference. */
    synchronized boolean outlineEnabled(){return option("outline");}
    /** Update outline preference. */
    synchronized boolean setOutline(boolean v){return setOption("outline",v)&&option("outline")==v;}

    /** Read custom color preference. */
    synchronized boolean customColorEnabled(){return option("custom_color");}
    /** Update custom color preference. */
    synchronized boolean setCustomColor(boolean v){return setOption("custom_color",v)&&option("custom_color")==v;}

    /** Read opacity preference. */
    synchronized boolean opacityEnabled(){return option("opacity");}
    /** Update opacity preference. */
    synchronized boolean setOpacity(boolean v){return setOption("opacity",v)&&option("opacity")==v;}

    /** Read scale preference. */
    synchronized boolean scaleEnabled(){return option("scale");}
    /** Update scale preference. */
    synchronized boolean setScale(boolean v){return setOption("scale",v)&&option("scale")==v;}

    /** Read spread preference. */
    synchronized boolean spreadEnabled(){return option("spread");}
    /** Update spread preference. */
    synchronized boolean setSpread(boolean v){return setOption("spread",v)&&option("spread")==v;}

    /** Read fade on damage preference. */
    synchronized boolean fadeOnDamageEnabled(){return option("fade_on_damage");}
    /** Update fade on damage preference. */
    synchronized boolean setFadeOnDamage(boolean v){return setOption("fade_on_damage",v)&&option("fade_on_damage")==v;}

    /** Read critical indicator preference. */
    synchronized boolean criticalIndicatorEnabled(){return option("critical_indicator");}
    /** Update critical indicator preference. */
    synchronized boolean setCriticalIndicator(boolean v){return setOption("critical_indicator",v)&&option("critical_indicator")==v;}

    /** Read projectile hint preference. */
    synchronized boolean projectileHintEnabled(){return option("projectile_hint");}
    /** Update projectile hint preference. */
    synchronized boolean setProjectileHint(boolean v){return setOption("projectile_hint",v)&&option("projectile_hint")==v;}

    /** Read block hint preference. */
    synchronized boolean blockHintEnabled(){return option("block_hint");}
    /** Update block hint preference. */
    synchronized boolean setBlockHint(boolean v){return setOption("block_hint",v)&&option("block_hint")==v;}

    /** Read ally color preference. */
    synchronized boolean allyColorEnabled(){return option("ally_color");}
    /** Update ally color preference. */
    synchronized boolean setAllyColor(boolean v){return setOption("ally_color",v)&&option("ally_color")==v;}

    /** Read enemy color preference. */
    synchronized boolean enemyColorEnabled(){return option("enemy_color");}
    /** Update enemy color preference. */
    synchronized boolean setEnemyColor(boolean v){return setOption("enemy_color",v)&&option("enemy_color")==v;}

    /** Read dynamic gap preference. */
    synchronized boolean dynamicGapEnabled(){return option("dynamic_gap");}
    /** Update dynamic gap preference. */
    synchronized boolean setDynamicGap(boolean v){return setOption("dynamic_gap",v)&&option("dynamic_gap")==v;}

    /** Read hit feedback preference. */
    synchronized boolean hitFeedbackEnabled(){return option("hit_feedback");}
    /** Update hit feedback preference. */
    synchronized boolean setHitFeedback(boolean v){return setOption("hit_feedback",v)&&option("hit_feedback")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class CombatHudSystem extends FeatureModule {
    private static final String[] DEFAULTS={"health","absorption","armor","hunger","totem_count","shield_state","weapon_durability","attack_cooldown","target_health","target_armor","combo_counter","damage_numbers","latency","coordinates","combat_timer","low_health_warning"};
    CombatHudSystem(){super("combat_hud",DEFAULTS);}
    @Override synchronized String summary(){return "combat hud: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read health preference. */
    synchronized boolean healthEnabled(){return option("health");}
    /** Update health preference. */
    synchronized boolean setHealth(boolean v){return setOption("health",v)&&option("health")==v;}

    /** Read absorption preference. */
    synchronized boolean absorptionEnabled(){return option("absorption");}
    /** Update absorption preference. */
    synchronized boolean setAbsorption(boolean v){return setOption("absorption",v)&&option("absorption")==v;}

    /** Read armor preference. */
    synchronized boolean armorEnabled(){return option("armor");}
    /** Update armor preference. */
    synchronized boolean setArmor(boolean v){return setOption("armor",v)&&option("armor")==v;}

    /** Read hunger preference. */
    synchronized boolean hungerEnabled(){return option("hunger");}
    /** Update hunger preference. */
    synchronized boolean setHunger(boolean v){return setOption("hunger",v)&&option("hunger")==v;}

    /** Read totem count preference. */
    synchronized boolean totemCountEnabled(){return option("totem_count");}
    /** Update totem count preference. */
    synchronized boolean setTotemCount(boolean v){return setOption("totem_count",v)&&option("totem_count")==v;}

    /** Read shield state preference. */
    synchronized boolean shieldStateEnabled(){return option("shield_state");}
    /** Update shield state preference. */
    synchronized boolean setShieldState(boolean v){return setOption("shield_state",v)&&option("shield_state")==v;}

    /** Read weapon durability preference. */
    synchronized boolean weaponDurabilityEnabled(){return option("weapon_durability");}
    /** Update weapon durability preference. */
    synchronized boolean setWeaponDurability(boolean v){return setOption("weapon_durability",v)&&option("weapon_durability")==v;}

    /** Read attack cooldown preference. */
    synchronized boolean attackCooldownEnabled(){return option("attack_cooldown");}
    /** Update attack cooldown preference. */
    synchronized boolean setAttackCooldown(boolean v){return setOption("attack_cooldown",v)&&option("attack_cooldown")==v;}

    /** Read target health preference. */
    synchronized boolean targetHealthEnabled(){return option("target_health");}
    /** Update target health preference. */
    synchronized boolean setTargetHealth(boolean v){return setOption("target_health",v)&&option("target_health")==v;}

    /** Read target armor preference. */
    synchronized boolean targetArmorEnabled(){return option("target_armor");}
    /** Update target armor preference. */
    synchronized boolean setTargetArmor(boolean v){return setOption("target_armor",v)&&option("target_armor")==v;}

    /** Read combo counter preference. */
    synchronized boolean comboCounterEnabled(){return option("combo_counter");}
    /** Update combo counter preference. */
    synchronized boolean setComboCounter(boolean v){return setOption("combo_counter",v)&&option("combo_counter")==v;}

    /** Read damage numbers preference. */
    synchronized boolean damageNumbersEnabled(){return option("damage_numbers");}
    /** Update damage numbers preference. */
    synchronized boolean setDamageNumbers(boolean v){return setOption("damage_numbers",v)&&option("damage_numbers")==v;}

    /** Read latency preference. */
    synchronized boolean latencyEnabled(){return option("latency");}
    /** Update latency preference. */
    synchronized boolean setLatency(boolean v){return setOption("latency",v)&&option("latency")==v;}

    /** Read coordinates preference. */
    synchronized boolean coordinatesEnabled(){return option("coordinates");}
    /** Update coordinates preference. */
    synchronized boolean setCoordinates(boolean v){return setOption("coordinates",v)&&option("coordinates")==v;}

    /** Read combat timer preference. */
    synchronized boolean combatTimerEnabled(){return option("combat_timer");}
    /** Update combat timer preference. */
    synchronized boolean setCombatTimer(boolean v){return setOption("combat_timer",v)&&option("combat_timer")==v;}

    /** Read low health warning preference. */
    synchronized boolean lowHealthWarningEnabled(){return option("low_health_warning");}
    /** Update low health warning preference. */
    synchronized boolean setLowHealthWarning(boolean v){return setOption("low_health_warning",v)&&option("low_health_warning")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class LoadoutSystem extends FeatureModule {
    private static final String[] DEFAULTS={"classic","crystal","potion","axe_sword","shield_tank","archer","uhc","netherite","diamond","iron","custom_a","custom_b","custom_c","save_slots","restore_last","server_safe"};
    LoadoutSystem(){super("loadouts",DEFAULTS);}
    @Override synchronized String summary(){return "loadouts: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read classic preference. */
    synchronized boolean classicEnabled(){return option("classic");}
    /** Update classic preference. */
    synchronized boolean setClassic(boolean v){return setOption("classic",v)&&option("classic")==v;}

    /** Read crystal preference. */
    synchronized boolean crystalEnabled(){return option("crystal");}
    /** Update crystal preference. */
    synchronized boolean setCrystal(boolean v){return setOption("crystal",v)&&option("crystal")==v;}

    /** Read potion preference. */
    synchronized boolean potionEnabled(){return option("potion");}
    /** Update potion preference. */
    synchronized boolean setPotion(boolean v){return setOption("potion",v)&&option("potion")==v;}

    /** Read axe sword preference. */
    synchronized boolean axeSwordEnabled(){return option("axe_sword");}
    /** Update axe sword preference. */
    synchronized boolean setAxeSword(boolean v){return setOption("axe_sword",v)&&option("axe_sword")==v;}

    /** Read shield tank preference. */
    synchronized boolean shieldTankEnabled(){return option("shield_tank");}
    /** Update shield tank preference. */
    synchronized boolean setShieldTank(boolean v){return setOption("shield_tank",v)&&option("shield_tank")==v;}

    /** Read archer preference. */
    synchronized boolean archerEnabled(){return option("archer");}
    /** Update archer preference. */
    synchronized boolean setArcher(boolean v){return setOption("archer",v)&&option("archer")==v;}

    /** Read uhc preference. */
    synchronized boolean uhcEnabled(){return option("uhc");}
    /** Update uhc preference. */
    synchronized boolean setUhc(boolean v){return setOption("uhc",v)&&option("uhc")==v;}

    /** Read netherite preference. */
    synchronized boolean netheriteEnabled(){return option("netherite");}
    /** Update netherite preference. */
    synchronized boolean setNetherite(boolean v){return setOption("netherite",v)&&option("netherite")==v;}

    /** Read diamond preference. */
    synchronized boolean diamondEnabled(){return option("diamond");}
    /** Update diamond preference. */
    synchronized boolean setDiamond(boolean v){return setOption("diamond",v)&&option("diamond")==v;}

    /** Read iron preference. */
    synchronized boolean ironEnabled(){return option("iron");}
    /** Update iron preference. */
    synchronized boolean setIron(boolean v){return setOption("iron",v)&&option("iron")==v;}

    /** Read custom a preference. */
    synchronized boolean customAEnabled(){return option("custom_a");}
    /** Update custom a preference. */
    synchronized boolean setCustomA(boolean v){return setOption("custom_a",v)&&option("custom_a")==v;}

    /** Read custom b preference. */
    synchronized boolean customBEnabled(){return option("custom_b");}
    /** Update custom b preference. */
    synchronized boolean setCustomB(boolean v){return setOption("custom_b",v)&&option("custom_b")==v;}

    /** Read custom c preference. */
    synchronized boolean customCEnabled(){return option("custom_c");}
    /** Update custom c preference. */
    synchronized boolean setCustomC(boolean v){return setOption("custom_c",v)&&option("custom_c")==v;}

    /** Read save slots preference. */
    synchronized boolean saveSlotsEnabled(){return option("save_slots");}
    /** Update save slots preference. */
    synchronized boolean setSaveSlots(boolean v){return setOption("save_slots",v)&&option("save_slots")==v;}

    /** Read restore last preference. */
    synchronized boolean restoreLastEnabled(){return option("restore_last");}
    /** Update restore last preference. */
    synchronized boolean setRestoreLast(boolean v){return setOption("restore_last",v)&&option("restore_last")==v;}

    /** Read server safe preference. */
    synchronized boolean serverSafeEnabled(){return option("server_safe");}
    /** Update server safe preference. */
    synchronized boolean setServerSafe(boolean v){return setOption("server_safe",v)&&option("server_safe")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class KeybindSystem extends FeatureModule {
    private static final String[] DEFAULTS={"open_menu","cycle_loadout","toggle_hud","toggle_crosshair","quick_totem","quick_shield","quick_weapon","animation_toggle","settings","reset_layout","mark_target","combat_log","show_coordinates","focus_hud","panic_hide","profile_next"};
    KeybindSystem(){super("keybinds",DEFAULTS);}
    @Override synchronized String summary(){return "keybinds: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read open menu preference. */
    synchronized boolean openMenuEnabled(){return option("open_menu");}
    /** Update open menu preference. */
    synchronized boolean setOpenMenu(boolean v){return setOption("open_menu",v)&&option("open_menu")==v;}

    /** Read cycle loadout preference. */
    synchronized boolean cycleLoadoutEnabled(){return option("cycle_loadout");}
    /** Update cycle loadout preference. */
    synchronized boolean setCycleLoadout(boolean v){return setOption("cycle_loadout",v)&&option("cycle_loadout")==v;}

    /** Read toggle hud preference. */
    synchronized boolean toggleHudEnabled(){return option("toggle_hud");}
    /** Update toggle hud preference. */
    synchronized boolean setToggleHud(boolean v){return setOption("toggle_hud",v)&&option("toggle_hud")==v;}

    /** Read toggle crosshair preference. */
    synchronized boolean toggleCrosshairEnabled(){return option("toggle_crosshair");}
    /** Update toggle crosshair preference. */
    synchronized boolean setToggleCrosshair(boolean v){return setOption("toggle_crosshair",v)&&option("toggle_crosshair")==v;}

    /** Read quick totem preference. */
    synchronized boolean quickTotemEnabled(){return option("quick_totem");}
    /** Update quick totem preference. */
    synchronized boolean setQuickTotem(boolean v){return setOption("quick_totem",v)&&option("quick_totem")==v;}

    /** Read quick shield preference. */
    synchronized boolean quickShieldEnabled(){return option("quick_shield");}
    /** Update quick shield preference. */
    synchronized boolean setQuickShield(boolean v){return setOption("quick_shield",v)&&option("quick_shield")==v;}

    /** Read quick weapon preference. */
    synchronized boolean quickWeaponEnabled(){return option("quick_weapon");}
    /** Update quick weapon preference. */
    synchronized boolean setQuickWeapon(boolean v){return setOption("quick_weapon",v)&&option("quick_weapon")==v;}

    /** Read animation toggle preference. */
    synchronized boolean animationToggleEnabled(){return option("animation_toggle");}
    /** Update animation toggle preference. */
    synchronized boolean setAnimationToggle(boolean v){return setOption("animation_toggle",v)&&option("animation_toggle")==v;}

    /** Read settings preference. */
    synchronized boolean settingsEnabled(){return option("settings");}
    /** Update settings preference. */
    synchronized boolean setSettings(boolean v){return setOption("settings",v)&&option("settings")==v;}

    /** Read reset layout preference. */
    synchronized boolean resetLayoutEnabled(){return option("reset_layout");}
    /** Update reset layout preference. */
    synchronized boolean setResetLayout(boolean v){return setOption("reset_layout",v)&&option("reset_layout")==v;}

    /** Read mark target preference. */
    synchronized boolean markTargetEnabled(){return option("mark_target");}
    /** Update mark target preference. */
    synchronized boolean setMarkTarget(boolean v){return setOption("mark_target",v)&&option("mark_target")==v;}

    /** Read combat log preference. */
    synchronized boolean combatLogEnabled(){return option("combat_log");}
    /** Update combat log preference. */
    synchronized boolean setCombatLog(boolean v){return setOption("combat_log",v)&&option("combat_log")==v;}

    /** Read show coordinates preference. */
    synchronized boolean showCoordinatesEnabled(){return option("show_coordinates");}
    /** Update show coordinates preference. */
    synchronized boolean setShowCoordinates(boolean v){return setOption("show_coordinates",v)&&option("show_coordinates")==v;}

    /** Read focus hud preference. */
    synchronized boolean focusHudEnabled(){return option("focus_hud");}
    /** Update focus hud preference. */
    synchronized boolean setFocusHud(boolean v){return setOption("focus_hud",v)&&option("focus_hud")==v;}

    /** Read panic hide preference. */
    synchronized boolean panicHideEnabled(){return option("panic_hide");}
    /** Update panic hide preference. */
    synchronized boolean setPanicHide(boolean v){return setOption("panic_hide",v)&&option("panic_hide")==v;}

    /** Read profile next preference. */
    synchronized boolean profileNextEnabled(){return option("profile_next");}
    /** Update profile next preference. */
    synchronized boolean setProfileNext(boolean v){return setOption("profile_next",v)&&option("profile_next")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class AnimationSystem extends FeatureModule {
    private static final String[] DEFAULTS={"sword_swing","shield_raise","totem_pop","hit_flash","combo_pulse","hud_slide","fade_in","fade_out","button_press","loadout_swap","crosshair_expand","damage_float","low_health_pulse","critical_spark","particle_burst","screen_shake"};
    AnimationSystem(){super("animations",DEFAULTS);}
    @Override synchronized String summary(){return "animations: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read sword swing preference. */
    synchronized boolean swordSwingEnabled(){return option("sword_swing");}
    /** Update sword swing preference. */
    synchronized boolean setSwordSwing(boolean v){return setOption("sword_swing",v)&&option("sword_swing")==v;}

    /** Read shield raise preference. */
    synchronized boolean shieldRaiseEnabled(){return option("shield_raise");}
    /** Update shield raise preference. */
    synchronized boolean setShieldRaise(boolean v){return setOption("shield_raise",v)&&option("shield_raise")==v;}

    /** Read totem pop preference. */
    synchronized boolean totemPopEnabled(){return option("totem_pop");}
    /** Update totem pop preference. */
    synchronized boolean setTotemPop(boolean v){return setOption("totem_pop",v)&&option("totem_pop")==v;}

    /** Read hit flash preference. */
    synchronized boolean hitFlashEnabled(){return option("hit_flash");}
    /** Update hit flash preference. */
    synchronized boolean setHitFlash(boolean v){return setOption("hit_flash",v)&&option("hit_flash")==v;}

    /** Read combo pulse preference. */
    synchronized boolean comboPulseEnabled(){return option("combo_pulse");}
    /** Update combo pulse preference. */
    synchronized boolean setComboPulse(boolean v){return setOption("combo_pulse",v)&&option("combo_pulse")==v;}

    /** Read hud slide preference. */
    synchronized boolean hudSlideEnabled(){return option("hud_slide");}
    /** Update hud slide preference. */
    synchronized boolean setHudSlide(boolean v){return setOption("hud_slide",v)&&option("hud_slide")==v;}

    /** Read fade in preference. */
    synchronized boolean fadeInEnabled(){return option("fade_in");}
    /** Update fade in preference. */
    synchronized boolean setFadeIn(boolean v){return setOption("fade_in",v)&&option("fade_in")==v;}

    /** Read fade out preference. */
    synchronized boolean fadeOutEnabled(){return option("fade_out");}
    /** Update fade out preference. */
    synchronized boolean setFadeOut(boolean v){return setOption("fade_out",v)&&option("fade_out")==v;}

    /** Read button press preference. */
    synchronized boolean buttonPressEnabled(){return option("button_press");}
    /** Update button press preference. */
    synchronized boolean setButtonPress(boolean v){return setOption("button_press",v)&&option("button_press")==v;}

    /** Read loadout swap preference. */
    synchronized boolean loadoutSwapEnabled(){return option("loadout_swap");}
    /** Update loadout swap preference. */
    synchronized boolean setLoadoutSwap(boolean v){return setOption("loadout_swap",v)&&option("loadout_swap")==v;}

    /** Read crosshair expand preference. */
    synchronized boolean crosshairExpandEnabled(){return option("crosshair_expand");}
    /** Update crosshair expand preference. */
    synchronized boolean setCrosshairExpand(boolean v){return setOption("crosshair_expand",v)&&option("crosshair_expand")==v;}

    /** Read damage float preference. */
    synchronized boolean damageFloatEnabled(){return option("damage_float");}
    /** Update damage float preference. */
    synchronized boolean setDamageFloat(boolean v){return setOption("damage_float",v)&&option("damage_float")==v;}

    /** Read low health pulse preference. */
    synchronized boolean lowHealthPulseEnabled(){return option("low_health_pulse");}
    /** Update low health pulse preference. */
    synchronized boolean setLowHealthPulse(boolean v){return setOption("low_health_pulse",v)&&option("low_health_pulse")==v;}

    /** Read critical spark preference. */
    synchronized boolean criticalSparkEnabled(){return option("critical_spark");}
    /** Update critical spark preference. */
    synchronized boolean setCriticalSpark(boolean v){return setOption("critical_spark",v)&&option("critical_spark")==v;}

    /** Read particle burst preference. */
    synchronized boolean particleBurstEnabled(){return option("particle_burst");}
    /** Update particle burst preference. */
    synchronized boolean setParticleBurst(boolean v){return setOption("particle_burst",v)&&option("particle_burst")==v;}

    /** Read screen shake preference. */
    synchronized boolean screenShakeEnabled(){return option("screen_shake");}
    /** Update screen shake preference. */
    synchronized boolean setScreenShake(boolean v){return setOption("screen_shake",v)&&option("screen_shake")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class SwordAssistantSystem extends FeatureModule {
    private static final String[] DEFAULTS={"attack_cooldown","weapon_durability","sharpness_hint","crit_timing","sprint_reset_hint","reach_display","combo_timing","axe_swap_hint","attack_indicator","shield_break_hint","hotbar_sword","damage_estimate","falling_crit","water_penalty","attack_ready"};
    SwordAssistantSystem(){super("sword_assist",DEFAULTS);}
    @Override synchronized String summary(){return "sword assist: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read attack cooldown preference. */
    synchronized boolean attackCooldownEnabled(){return option("attack_cooldown");}
    /** Update attack cooldown preference. */
    synchronized boolean setAttackCooldown(boolean v){return setOption("attack_cooldown",v)&&option("attack_cooldown")==v;}

    /** Read weapon durability preference. */
    synchronized boolean weaponDurabilityEnabled(){return option("weapon_durability");}
    /** Update weapon durability preference. */
    synchronized boolean setWeaponDurability(boolean v){return setOption("weapon_durability",v)&&option("weapon_durability")==v;}

    /** Read sharpness hint preference. */
    synchronized boolean sharpnessHintEnabled(){return option("sharpness_hint");}
    /** Update sharpness hint preference. */
    synchronized boolean setSharpnessHint(boolean v){return setOption("sharpness_hint",v)&&option("sharpness_hint")==v;}

    /** Read crit timing preference. */
    synchronized boolean critTimingEnabled(){return option("crit_timing");}
    /** Update crit timing preference. */
    synchronized boolean setCritTiming(boolean v){return setOption("crit_timing",v)&&option("crit_timing")==v;}

    /** Read sprint reset hint preference. */
    synchronized boolean sprintResetHintEnabled(){return option("sprint_reset_hint");}
    /** Update sprint reset hint preference. */
    synchronized boolean setSprintResetHint(boolean v){return setOption("sprint_reset_hint",v)&&option("sprint_reset_hint")==v;}

    /** Read reach display preference. */
    synchronized boolean reachDisplayEnabled(){return option("reach_display");}
    /** Update reach display preference. */
    synchronized boolean setReachDisplay(boolean v){return setOption("reach_display",v)&&option("reach_display")==v;}

    /** Read combo timing preference. */
    synchronized boolean comboTimingEnabled(){return option("combo_timing");}
    /** Update combo timing preference. */
    synchronized boolean setComboTiming(boolean v){return setOption("combo_timing",v)&&option("combo_timing")==v;}

    /** Read axe swap hint preference. */
    synchronized boolean axeSwapHintEnabled(){return option("axe_swap_hint");}
    /** Update axe swap hint preference. */
    synchronized boolean setAxeSwapHint(boolean v){return setOption("axe_swap_hint",v)&&option("axe_swap_hint")==v;}

    /** Read attack indicator preference. */
    synchronized boolean attackIndicatorEnabled(){return option("attack_indicator");}
    /** Update attack indicator preference. */
    synchronized boolean setAttackIndicator(boolean v){return setOption("attack_indicator",v)&&option("attack_indicator")==v;}

    /** Read shield break hint preference. */
    synchronized boolean shieldBreakHintEnabled(){return option("shield_break_hint");}
    /** Update shield break hint preference. */
    synchronized boolean setShieldBreakHint(boolean v){return setOption("shield_break_hint",v)&&option("shield_break_hint")==v;}

    /** Read hotbar sword preference. */
    synchronized boolean hotbarSwordEnabled(){return option("hotbar_sword");}
    /** Update hotbar sword preference. */
    synchronized boolean setHotbarSword(boolean v){return setOption("hotbar_sword",v)&&option("hotbar_sword")==v;}

    /** Read damage estimate preference. */
    synchronized boolean damageEstimateEnabled(){return option("damage_estimate");}
    /** Update damage estimate preference. */
    synchronized boolean setDamageEstimate(boolean v){return setOption("damage_estimate",v)&&option("damage_estimate")==v;}

    /** Read falling crit preference. */
    synchronized boolean fallingCritEnabled(){return option("falling_crit");}
    /** Update falling crit preference. */
    synchronized boolean setFallingCrit(boolean v){return setOption("falling_crit",v)&&option("falling_crit")==v;}

    /** Read water penalty preference. */
    synchronized boolean waterPenaltyEnabled(){return option("water_penalty");}
    /** Update water penalty preference. */
    synchronized boolean setWaterPenalty(boolean v){return setOption("water_penalty",v)&&option("water_penalty")==v;}

    /** Read attack ready preference. */
    synchronized boolean attackReadyEnabled(){return option("attack_ready");}
    /** Update attack ready preference. */
    synchronized boolean setAttackReady(boolean v){return setOption("attack_ready",v)&&option("attack_ready")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class ShieldAssistantSystem extends FeatureModule {
    private static final String[] DEFAULTS={"shield_durability","raise_hint","axe_threat","block_state","offhand_check","shield_cooldown","projectile_warning","shield_bash_hint","hotbar_shield","low_durability","block_animation","hand_swap_hint","shield_break_alert","fire_resistance_hint","guard_timer"};
    ShieldAssistantSystem(){super("shield_assist",DEFAULTS);}
    @Override synchronized String summary(){return "shield assist: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read shield durability preference. */
    synchronized boolean shieldDurabilityEnabled(){return option("shield_durability");}
    /** Update shield durability preference. */
    synchronized boolean setShieldDurability(boolean v){return setOption("shield_durability",v)&&option("shield_durability")==v;}

    /** Read raise hint preference. */
    synchronized boolean raiseHintEnabled(){return option("raise_hint");}
    /** Update raise hint preference. */
    synchronized boolean setRaiseHint(boolean v){return setOption("raise_hint",v)&&option("raise_hint")==v;}

    /** Read axe threat preference. */
    synchronized boolean axeThreatEnabled(){return option("axe_threat");}
    /** Update axe threat preference. */
    synchronized boolean setAxeThreat(boolean v){return setOption("axe_threat",v)&&option("axe_threat")==v;}

    /** Read block state preference. */
    synchronized boolean blockStateEnabled(){return option("block_state");}
    /** Update block state preference. */
    synchronized boolean setBlockState(boolean v){return setOption("block_state",v)&&option("block_state")==v;}

    /** Read offhand check preference. */
    synchronized boolean offhandCheckEnabled(){return option("offhand_check");}
    /** Update offhand check preference. */
    synchronized boolean setOffhandCheck(boolean v){return setOption("offhand_check",v)&&option("offhand_check")==v;}

    /** Read shield cooldown preference. */
    synchronized boolean shieldCooldownEnabled(){return option("shield_cooldown");}
    /** Update shield cooldown preference. */
    synchronized boolean setShieldCooldown(boolean v){return setOption("shield_cooldown",v)&&option("shield_cooldown")==v;}

    /** Read projectile warning preference. */
    synchronized boolean projectileWarningEnabled(){return option("projectile_warning");}
    /** Update projectile warning preference. */
    synchronized boolean setProjectileWarning(boolean v){return setOption("projectile_warning",v)&&option("projectile_warning")==v;}

    /** Read shield bash hint preference. */
    synchronized boolean shieldBashHintEnabled(){return option("shield_bash_hint");}
    /** Update shield bash hint preference. */
    synchronized boolean setShieldBashHint(boolean v){return setOption("shield_bash_hint",v)&&option("shield_bash_hint")==v;}

    /** Read hotbar shield preference. */
    synchronized boolean hotbarShieldEnabled(){return option("hotbar_shield");}
    /** Update hotbar shield preference. */
    synchronized boolean setHotbarShield(boolean v){return setOption("hotbar_shield",v)&&option("hotbar_shield")==v;}

    /** Read low durability preference. */
    synchronized boolean lowDurabilityEnabled(){return option("low_durability");}
    /** Update low durability preference. */
    synchronized boolean setLowDurability(boolean v){return setOption("low_durability",v)&&option("low_durability")==v;}

    /** Read block animation preference. */
    synchronized boolean blockAnimationEnabled(){return option("block_animation");}
    /** Update block animation preference. */
    synchronized boolean setBlockAnimation(boolean v){return setOption("block_animation",v)&&option("block_animation")==v;}

    /** Read hand swap hint preference. */
    synchronized boolean handSwapHintEnabled(){return option("hand_swap_hint");}
    /** Update hand swap hint preference. */
    synchronized boolean setHandSwapHint(boolean v){return setOption("hand_swap_hint",v)&&option("hand_swap_hint")==v;}

    /** Read shield break alert preference. */
    synchronized boolean shieldBreakAlertEnabled(){return option("shield_break_alert");}
    /** Update shield break alert preference. */
    synchronized boolean setShieldBreakAlert(boolean v){return setOption("shield_break_alert",v)&&option("shield_break_alert")==v;}

    /** Read fire resistance hint preference. */
    synchronized boolean fireResistanceHintEnabled(){return option("fire_resistance_hint");}
    /** Update fire resistance hint preference. */
    synchronized boolean setFireResistanceHint(boolean v){return setOption("fire_resistance_hint",v)&&option("fire_resistance_hint")==v;}

    /** Read guard timer preference. */
    synchronized boolean guardTimerEnabled(){return option("guard_timer");}
    /** Update guard timer preference. */
    synchronized boolean setGuardTimer(boolean v){return setOption("guard_timer",v)&&option("guard_timer")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class TotemAssistantSystem extends FeatureModule {
    private static final String[] DEFAULTS={"totem_count","offhand_totem","hotbar_totem","low_count_warning","totem_pop_flash","totem_restock_hint","inventory_scan","totem_sound","totem_cooldown","totem_slot_marker","panic_key","offhand_priority","hotbar_priority","death_prevention_status","totem_animation"};
    TotemAssistantSystem(){super("totem_assist",DEFAULTS);}
    @Override synchronized String summary(){return "totem assist: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read totem count preference. */
    synchronized boolean totemCountEnabled(){return option("totem_count");}
    /** Update totem count preference. */
    synchronized boolean setTotemCount(boolean v){return setOption("totem_count",v)&&option("totem_count")==v;}

    /** Read offhand totem preference. */
    synchronized boolean offhandTotemEnabled(){return option("offhand_totem");}
    /** Update offhand totem preference. */
    synchronized boolean setOffhandTotem(boolean v){return setOption("offhand_totem",v)&&option("offhand_totem")==v;}

    /** Read hotbar totem preference. */
    synchronized boolean hotbarTotemEnabled(){return option("hotbar_totem");}
    /** Update hotbar totem preference. */
    synchronized boolean setHotbarTotem(boolean v){return setOption("hotbar_totem",v)&&option("hotbar_totem")==v;}

    /** Read low count warning preference. */
    synchronized boolean lowCountWarningEnabled(){return option("low_count_warning");}
    /** Update low count warning preference. */
    synchronized boolean setLowCountWarning(boolean v){return setOption("low_count_warning",v)&&option("low_count_warning")==v;}

    /** Read totem pop flash preference. */
    synchronized boolean totemPopFlashEnabled(){return option("totem_pop_flash");}
    /** Update totem pop flash preference. */
    synchronized boolean setTotemPopFlash(boolean v){return setOption("totem_pop_flash",v)&&option("totem_pop_flash")==v;}

    /** Read totem restock hint preference. */
    synchronized boolean totemRestockHintEnabled(){return option("totem_restock_hint");}
    /** Update totem restock hint preference. */
    synchronized boolean setTotemRestockHint(boolean v){return setOption("totem_restock_hint",v)&&option("totem_restock_hint")==v;}

    /** Read inventory scan preference. */
    synchronized boolean inventoryScanEnabled(){return option("inventory_scan");}
    /** Update inventory scan preference. */
    synchronized boolean setInventoryScan(boolean v){return setOption("inventory_scan",v)&&option("inventory_scan")==v;}

    /** Read totem sound preference. */
    synchronized boolean totemSoundEnabled(){return option("totem_sound");}
    /** Update totem sound preference. */
    synchronized boolean setTotemSound(boolean v){return setOption("totem_sound",v)&&option("totem_sound")==v;}

    /** Read totem cooldown preference. */
    synchronized boolean totemCooldownEnabled(){return option("totem_cooldown");}
    /** Update totem cooldown preference. */
    synchronized boolean setTotemCooldown(boolean v){return setOption("totem_cooldown",v)&&option("totem_cooldown")==v;}

    /** Read totem slot marker preference. */
    synchronized boolean totemSlotMarkerEnabled(){return option("totem_slot_marker");}
    /** Update totem slot marker preference. */
    synchronized boolean setTotemSlotMarker(boolean v){return setOption("totem_slot_marker",v)&&option("totem_slot_marker")==v;}

    /** Read panic key preference. */
    synchronized boolean panicKeyEnabled(){return option("panic_key");}
    /** Update panic key preference. */
    synchronized boolean setPanicKey(boolean v){return setOption("panic_key",v)&&option("panic_key")==v;}

    /** Read offhand priority preference. */
    synchronized boolean offhandPriorityEnabled(){return option("offhand_priority");}
    /** Update offhand priority preference. */
    synchronized boolean setOffhandPriority(boolean v){return setOption("offhand_priority",v)&&option("offhand_priority")==v;}

    /** Read hotbar priority preference. */
    synchronized boolean hotbarPriorityEnabled(){return option("hotbar_priority");}
    /** Update hotbar priority preference. */
    synchronized boolean setHotbarPriority(boolean v){return setOption("hotbar_priority",v)&&option("hotbar_priority")==v;}

    /** Read death prevention status preference. */
    synchronized boolean deathPreventionStatusEnabled(){return option("death_prevention_status");}
    /** Update death prevention status preference. */
    synchronized boolean setDeathPreventionStatus(boolean v){return setOption("death_prevention_status",v)&&option("death_prevention_status")==v;}

    /** Read totem animation preference. */
    synchronized boolean totemAnimationEnabled(){return option("totem_animation");}
    /** Update totem animation preference. */
    synchronized boolean setTotemAnimation(boolean v){return setOption("totem_animation",v)&&option("totem_animation")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class PotionAssistantSystem extends FeatureModule {
    private static final String[] DEFAULTS={"strength","swiftness","fire_resistance","regeneration","instant_health","slow_falling","night_vision","poison_warning","weakness_warning","active_effect_timer","splash_potion_count","drink_progress","potion_hotbar","throw_hint","effect_priority"};
    PotionAssistantSystem(){super("potion_assist",DEFAULTS);}
    @Override synchronized String summary(){return "potion assist: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read strength preference. */
    synchronized boolean strengthEnabled(){return option("strength");}
    /** Update strength preference. */
    synchronized boolean setStrength(boolean v){return setOption("strength",v)&&option("strength")==v;}

    /** Read swiftness preference. */
    synchronized boolean swiftnessEnabled(){return option("swiftness");}
    /** Update swiftness preference. */
    synchronized boolean setSwiftness(boolean v){return setOption("swiftness",v)&&option("swiftness")==v;}

    /** Read fire resistance preference. */
    synchronized boolean fireResistanceEnabled(){return option("fire_resistance");}
    /** Update fire resistance preference. */
    synchronized boolean setFireResistance(boolean v){return setOption("fire_resistance",v)&&option("fire_resistance")==v;}

    /** Read regeneration preference. */
    synchronized boolean regenerationEnabled(){return option("regeneration");}
    /** Update regeneration preference. */
    synchronized boolean setRegeneration(boolean v){return setOption("regeneration",v)&&option("regeneration")==v;}

    /** Read instant health preference. */
    synchronized boolean instantHealthEnabled(){return option("instant_health");}
    /** Update instant health preference. */
    synchronized boolean setInstantHealth(boolean v){return setOption("instant_health",v)&&option("instant_health")==v;}

    /** Read slow falling preference. */
    synchronized boolean slowFallingEnabled(){return option("slow_falling");}
    /** Update slow falling preference. */
    synchronized boolean setSlowFalling(boolean v){return setOption("slow_falling",v)&&option("slow_falling")==v;}

    /** Read night vision preference. */
    synchronized boolean nightVisionEnabled(){return option("night_vision");}
    /** Update night vision preference. */
    synchronized boolean setNightVision(boolean v){return setOption("night_vision",v)&&option("night_vision")==v;}

    /** Read poison warning preference. */
    synchronized boolean poisonWarningEnabled(){return option("poison_warning");}
    /** Update poison warning preference. */
    synchronized boolean setPoisonWarning(boolean v){return setOption("poison_warning",v)&&option("poison_warning")==v;}

    /** Read weakness warning preference. */
    synchronized boolean weaknessWarningEnabled(){return option("weakness_warning");}
    /** Update weakness warning preference. */
    synchronized boolean setWeaknessWarning(boolean v){return setOption("weakness_warning",v)&&option("weakness_warning")==v;}

    /** Read active effect timer preference. */
    synchronized boolean activeEffectTimerEnabled(){return option("active_effect_timer");}
    /** Update active effect timer preference. */
    synchronized boolean setActiveEffectTimer(boolean v){return setOption("active_effect_timer",v)&&option("active_effect_timer")==v;}

    /** Read splash potion count preference. */
    synchronized boolean splashPotionCountEnabled(){return option("splash_potion_count");}
    /** Update splash potion count preference. */
    synchronized boolean setSplashPotionCount(boolean v){return setOption("splash_potion_count",v)&&option("splash_potion_count")==v;}

    /** Read drink progress preference. */
    synchronized boolean drinkProgressEnabled(){return option("drink_progress");}
    /** Update drink progress preference. */
    synchronized boolean setDrinkProgress(boolean v){return setOption("drink_progress",v)&&option("drink_progress")==v;}

    /** Read potion hotbar preference. */
    synchronized boolean potionHotbarEnabled(){return option("potion_hotbar");}
    /** Update potion hotbar preference. */
    synchronized boolean setPotionHotbar(boolean v){return setOption("potion_hotbar",v)&&option("potion_hotbar")==v;}

    /** Read throw hint preference. */
    synchronized boolean throwHintEnabled(){return option("throw_hint");}
    /** Update throw hint preference. */
    synchronized boolean setThrowHint(boolean v){return setOption("throw_hint",v)&&option("throw_hint")==v;}

    /** Read effect priority preference. */
    synchronized boolean effectPriorityEnabled(){return option("effect_priority");}
    /** Update effect priority preference. */
    synchronized boolean setEffectPriority(boolean v){return setOption("effect_priority",v)&&option("effect_priority")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class InventoryAssistantSystem extends FeatureModule {
    private static final String[] DEFAULTS={"hotbar_layout","food_slot","pearls_slot","blocks_slot","water_bucket_slot","crystal_slot","obsidian_slot","arrow_slot","throwables_slot","sort_profile","low_food_warning","low_block_warning","empty_slot_warning","quick_move_hint","inventory_snapshot"};
    InventoryAssistantSystem(){super("inventory_assist",DEFAULTS);}
    @Override synchronized String summary(){return "inventory assist: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read hotbar layout preference. */
    synchronized boolean hotbarLayoutEnabled(){return option("hotbar_layout");}
    /** Update hotbar layout preference. */
    synchronized boolean setHotbarLayout(boolean v){return setOption("hotbar_layout",v)&&option("hotbar_layout")==v;}

    /** Read food slot preference. */
    synchronized boolean foodSlotEnabled(){return option("food_slot");}
    /** Update food slot preference. */
    synchronized boolean setFoodSlot(boolean v){return setOption("food_slot",v)&&option("food_slot")==v;}

    /** Read pearls slot preference. */
    synchronized boolean pearlsSlotEnabled(){return option("pearls_slot");}
    /** Update pearls slot preference. */
    synchronized boolean setPearlsSlot(boolean v){return setOption("pearls_slot",v)&&option("pearls_slot")==v;}

    /** Read blocks slot preference. */
    synchronized boolean blocksSlotEnabled(){return option("blocks_slot");}
    /** Update blocks slot preference. */
    synchronized boolean setBlocksSlot(boolean v){return setOption("blocks_slot",v)&&option("blocks_slot")==v;}

    /** Read water bucket slot preference. */
    synchronized boolean waterBucketSlotEnabled(){return option("water_bucket_slot");}
    /** Update water bucket slot preference. */
    synchronized boolean setWaterBucketSlot(boolean v){return setOption("water_bucket_slot",v)&&option("water_bucket_slot")==v;}

    /** Read crystal slot preference. */
    synchronized boolean crystalSlotEnabled(){return option("crystal_slot");}
    /** Update crystal slot preference. */
    synchronized boolean setCrystalSlot(boolean v){return setOption("crystal_slot",v)&&option("crystal_slot")==v;}

    /** Read obsidian slot preference. */
    synchronized boolean obsidianSlotEnabled(){return option("obsidian_slot");}
    /** Update obsidian slot preference. */
    synchronized boolean setObsidianSlot(boolean v){return setOption("obsidian_slot",v)&&option("obsidian_slot")==v;}

    /** Read arrow slot preference. */
    synchronized boolean arrowSlotEnabled(){return option("arrow_slot");}
    /** Update arrow slot preference. */
    synchronized boolean setArrowSlot(boolean v){return setOption("arrow_slot",v)&&option("arrow_slot")==v;}

    /** Read throwables slot preference. */
    synchronized boolean throwablesSlotEnabled(){return option("throwables_slot");}
    /** Update throwables slot preference. */
    synchronized boolean setThrowablesSlot(boolean v){return setOption("throwables_slot",v)&&option("throwables_slot")==v;}

    /** Read sort profile preference. */
    synchronized boolean sortProfileEnabled(){return option("sort_profile");}
    /** Update sort profile preference. */
    synchronized boolean setSortProfile(boolean v){return setOption("sort_profile",v)&&option("sort_profile")==v;}

    /** Read low food warning preference. */
    synchronized boolean lowFoodWarningEnabled(){return option("low_food_warning");}
    /** Update low food warning preference. */
    synchronized boolean setLowFoodWarning(boolean v){return setOption("low_food_warning",v)&&option("low_food_warning")==v;}

    /** Read low block warning preference. */
    synchronized boolean lowBlockWarningEnabled(){return option("low_block_warning");}
    /** Update low block warning preference. */
    synchronized boolean setLowBlockWarning(boolean v){return setOption("low_block_warning",v)&&option("low_block_warning")==v;}

    /** Read empty slot warning preference. */
    synchronized boolean emptySlotWarningEnabled(){return option("empty_slot_warning");}
    /** Update empty slot warning preference. */
    synchronized boolean setEmptySlotWarning(boolean v){return setOption("empty_slot_warning",v)&&option("empty_slot_warning")==v;}

    /** Read quick move hint preference. */
    synchronized boolean quickMoveHintEnabled(){return option("quick_move_hint");}
    /** Update quick move hint preference. */
    synchronized boolean setQuickMoveHint(boolean v){return setOption("quick_move_hint",v)&&option("quick_move_hint")==v;}

    /** Read inventory snapshot preference. */
    synchronized boolean inventorySnapshotEnabled(){return option("inventory_snapshot");}
    /** Update inventory snapshot preference. */
    synchronized boolean setInventorySnapshot(boolean v){return setOption("inventory_snapshot",v)&&option("inventory_snapshot")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class TargetInfoSystem extends FeatureModule {
    private static final String[] DEFAULTS={"name","health","armor","held_item","distance","angle","line_of_sight","friend_marker","enemy_marker","team_color","entity_type","weapon_enchantments","absorption","totem_pop_count","combat_history"};
    TargetInfoSystem(){super("target_info",DEFAULTS);}
    @Override synchronized String summary(){return "target info: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read name preference. */
    synchronized boolean nameEnabled(){return option("name");}
    /** Update name preference. */
    synchronized boolean setName(boolean v){return setOption("name",v)&&option("name")==v;}

    /** Read health preference. */
    synchronized boolean healthEnabled(){return option("health");}
    /** Update health preference. */
    synchronized boolean setHealth(boolean v){return setOption("health",v)&&option("health")==v;}

    /** Read armor preference. */
    synchronized boolean armorEnabled(){return option("armor");}
    /** Update armor preference. */
    synchronized boolean setArmor(boolean v){return setOption("armor",v)&&option("armor")==v;}

    /** Read held item preference. */
    synchronized boolean heldItemEnabled(){return option("held_item");}
    /** Update held item preference. */
    synchronized boolean setHeldItem(boolean v){return setOption("held_item",v)&&option("held_item")==v;}

    /** Read distance preference. */
    synchronized boolean distanceEnabled(){return option("distance");}
    /** Update distance preference. */
    synchronized boolean setDistance(boolean v){return setOption("distance",v)&&option("distance")==v;}

    /** Read angle preference. */
    synchronized boolean angleEnabled(){return option("angle");}
    /** Update angle preference. */
    synchronized boolean setAngle(boolean v){return setOption("angle",v)&&option("angle")==v;}

    /** Read line of sight preference. */
    synchronized boolean lineOfSightEnabled(){return option("line_of_sight");}
    /** Update line of sight preference. */
    synchronized boolean setLineOfSight(boolean v){return setOption("line_of_sight",v)&&option("line_of_sight")==v;}

    /** Read friend marker preference. */
    synchronized boolean friendMarkerEnabled(){return option("friend_marker");}
    /** Update friend marker preference. */
    synchronized boolean setFriendMarker(boolean v){return setOption("friend_marker",v)&&option("friend_marker")==v;}

    /** Read enemy marker preference. */
    synchronized boolean enemyMarkerEnabled(){return option("enemy_marker");}
    /** Update enemy marker preference. */
    synchronized boolean setEnemyMarker(boolean v){return setOption("enemy_marker",v)&&option("enemy_marker")==v;}

    /** Read team color preference. */
    synchronized boolean teamColorEnabled(){return option("team_color");}
    /** Update team color preference. */
    synchronized boolean setTeamColor(boolean v){return setOption("team_color",v)&&option("team_color")==v;}

    /** Read entity type preference. */
    synchronized boolean entityTypeEnabled(){return option("entity_type");}
    /** Update entity type preference. */
    synchronized boolean setEntityType(boolean v){return setOption("entity_type",v)&&option("entity_type")==v;}

    /** Read weapon enchantments preference. */
    synchronized boolean weaponEnchantmentsEnabled(){return option("weapon_enchantments");}
    /** Update weapon enchantments preference. */
    synchronized boolean setWeaponEnchantments(boolean v){return setOption("weapon_enchantments",v)&&option("weapon_enchantments")==v;}

    /** Read absorption preference. */
    synchronized boolean absorptionEnabled(){return option("absorption");}
    /** Update absorption preference. */
    synchronized boolean setAbsorption(boolean v){return setOption("absorption",v)&&option("absorption")==v;}

    /** Read totem pop count preference. */
    synchronized boolean totemPopCountEnabled(){return option("totem_pop_count");}
    /** Update totem pop count preference. */
    synchronized boolean setTotemPopCount(boolean v){return setOption("totem_pop_count",v)&&option("totem_pop_count")==v;}

    /** Read combat history preference. */
    synchronized boolean combatHistoryEnabled(){return option("combat_history");}
    /** Update combat history preference. */
    synchronized boolean setCombatHistory(boolean v){return setOption("combat_history",v)&&option("combat_history")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class ComboTrackerSystem extends FeatureModule {
    private static final String[] DEFAULTS={"hit_count","miss_count","streak","hit_rate","damage_total","damage_average","critical_hits","shield_breaks","totem_pops","combo_timer","reset_on_world_change","session_summary","personal_best","streak_animation","combat_start"};
    ComboTrackerSystem(){super("combo_tracker",DEFAULTS);}
    @Override synchronized String summary(){return "combo tracker: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read hit count preference. */
    synchronized boolean hitCountEnabled(){return option("hit_count");}
    /** Update hit count preference. */
    synchronized boolean setHitCount(boolean v){return setOption("hit_count",v)&&option("hit_count")==v;}

    /** Read miss count preference. */
    synchronized boolean missCountEnabled(){return option("miss_count");}
    /** Update miss count preference. */
    synchronized boolean setMissCount(boolean v){return setOption("miss_count",v)&&option("miss_count")==v;}

    /** Read streak preference. */
    synchronized boolean streakEnabled(){return option("streak");}
    /** Update streak preference. */
    synchronized boolean setStreak(boolean v){return setOption("streak",v)&&option("streak")==v;}

    /** Read hit rate preference. */
    synchronized boolean hitRateEnabled(){return option("hit_rate");}
    /** Update hit rate preference. */
    synchronized boolean setHitRate(boolean v){return setOption("hit_rate",v)&&option("hit_rate")==v;}

    /** Read damage total preference. */
    synchronized boolean damageTotalEnabled(){return option("damage_total");}
    /** Update damage total preference. */
    synchronized boolean setDamageTotal(boolean v){return setOption("damage_total",v)&&option("damage_total")==v;}

    /** Read damage average preference. */
    synchronized boolean damageAverageEnabled(){return option("damage_average");}
    /** Update damage average preference. */
    synchronized boolean setDamageAverage(boolean v){return setOption("damage_average",v)&&option("damage_average")==v;}

    /** Read critical hits preference. */
    synchronized boolean criticalHitsEnabled(){return option("critical_hits");}
    /** Update critical hits preference. */
    synchronized boolean setCriticalHits(boolean v){return setOption("critical_hits",v)&&option("critical_hits")==v;}

    /** Read shield breaks preference. */
    synchronized boolean shieldBreaksEnabled(){return option("shield_breaks");}
    /** Update shield breaks preference. */
    synchronized boolean setShieldBreaks(boolean v){return setOption("shield_breaks",v)&&option("shield_breaks")==v;}

    /** Read totem pops preference. */
    synchronized boolean totemPopsEnabled(){return option("totem_pops");}
    /** Update totem pops preference. */
    synchronized boolean setTotemPops(boolean v){return setOption("totem_pops",v)&&option("totem_pops")==v;}

    /** Read combo timer preference. */
    synchronized boolean comboTimerEnabled(){return option("combo_timer");}
    /** Update combo timer preference. */
    synchronized boolean setComboTimer(boolean v){return setOption("combo_timer",v)&&option("combo_timer")==v;}

    /** Read reset on world change preference. */
    synchronized boolean resetOnWorldChangeEnabled(){return option("reset_on_world_change");}
    /** Update reset on world change preference. */
    synchronized boolean setResetOnWorldChange(boolean v){return setOption("reset_on_world_change",v)&&option("reset_on_world_change")==v;}

    /** Read session summary preference. */
    synchronized boolean sessionSummaryEnabled(){return option("session_summary");}
    /** Update session summary preference. */
    synchronized boolean setSessionSummary(boolean v){return setOption("session_summary",v)&&option("session_summary")==v;}

    /** Read personal best preference. */
    synchronized boolean personalBestEnabled(){return option("personal_best");}
    /** Update personal best preference. */
    synchronized boolean setPersonalBest(boolean v){return setOption("personal_best",v)&&option("personal_best")==v;}

    /** Read streak animation preference. */
    synchronized boolean streakAnimationEnabled(){return option("streak_animation");}
    /** Update streak animation preference. */
    synchronized boolean setStreakAnimation(boolean v){return setOption("streak_animation",v)&&option("streak_animation")==v;}

    /** Read combat start preference. */
    synchronized boolean combatStartEnabled(){return option("combat_start");}
    /** Update combat start preference. */
    synchronized boolean setCombatStart(boolean v){return setOption("combat_start",v)&&option("combat_start")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class CooldownSystem extends FeatureModule {
    private static final String[] DEFAULTS={"attack","shield","ender_pearl","chorus","food","totem","crystal","anchor","potion","bow","crossbow","wind_charge","water_bucket","firework","custom_a","custom_b"};
    CooldownSystem(){super("cooldowns",DEFAULTS);}
    @Override synchronized String summary(){return "cooldowns: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read attack preference. */
    synchronized boolean attackEnabled(){return option("attack");}
    /** Update attack preference. */
    synchronized boolean setAttack(boolean v){return setOption("attack",v)&&option("attack")==v;}

    /** Read shield preference. */
    synchronized boolean shieldEnabled(){return option("shield");}
    /** Update shield preference. */
    synchronized boolean setShield(boolean v){return setOption("shield",v)&&option("shield")==v;}

    /** Read ender pearl preference. */
    synchronized boolean enderPearlEnabled(){return option("ender_pearl");}
    /** Update ender pearl preference. */
    synchronized boolean setEnderPearl(boolean v){return setOption("ender_pearl",v)&&option("ender_pearl")==v;}

    /** Read chorus preference. */
    synchronized boolean chorusEnabled(){return option("chorus");}
    /** Update chorus preference. */
    synchronized boolean setChorus(boolean v){return setOption("chorus",v)&&option("chorus")==v;}

    /** Read food preference. */
    synchronized boolean foodEnabled(){return option("food");}
    /** Update food preference. */
    synchronized boolean setFood(boolean v){return setOption("food",v)&&option("food")==v;}

    /** Read totem preference. */
    synchronized boolean totemEnabled(){return option("totem");}
    /** Update totem preference. */
    synchronized boolean setTotem(boolean v){return setOption("totem",v)&&option("totem")==v;}

    /** Read crystal preference. */
    synchronized boolean crystalEnabled(){return option("crystal");}
    /** Update crystal preference. */
    synchronized boolean setCrystal(boolean v){return setOption("crystal",v)&&option("crystal")==v;}

    /** Read anchor preference. */
    synchronized boolean anchorEnabled(){return option("anchor");}
    /** Update anchor preference. */
    synchronized boolean setAnchor(boolean v){return setOption("anchor",v)&&option("anchor")==v;}

    /** Read potion preference. */
    synchronized boolean potionEnabled(){return option("potion");}
    /** Update potion preference. */
    synchronized boolean setPotion(boolean v){return setOption("potion",v)&&option("potion")==v;}

    /** Read bow preference. */
    synchronized boolean bowEnabled(){return option("bow");}
    /** Update bow preference. */
    synchronized boolean setBow(boolean v){return setOption("bow",v)&&option("bow")==v;}

    /** Read crossbow preference. */
    synchronized boolean crossbowEnabled(){return option("crossbow");}
    /** Update crossbow preference. */
    synchronized boolean setCrossbow(boolean v){return setOption("crossbow",v)&&option("crossbow")==v;}

    /** Read wind charge preference. */
    synchronized boolean windChargeEnabled(){return option("wind_charge");}
    /** Update wind charge preference. */
    synchronized boolean setWindCharge(boolean v){return setOption("wind_charge",v)&&option("wind_charge")==v;}

    /** Read water bucket preference. */
    synchronized boolean waterBucketEnabled(){return option("water_bucket");}
    /** Update water bucket preference. */
    synchronized boolean setWaterBucket(boolean v){return setOption("water_bucket",v)&&option("water_bucket")==v;}

    /** Read firework preference. */
    synchronized boolean fireworkEnabled(){return option("firework");}
    /** Update firework preference. */
    synchronized boolean setFirework(boolean v){return setOption("firework",v)&&option("firework")==v;}

    /** Read custom a preference. */
    synchronized boolean customAEnabled(){return option("custom_a");}
    /** Update custom a preference. */
    synchronized boolean setCustomA(boolean v){return setOption("custom_a",v)&&option("custom_a")==v;}

    /** Read custom b preference. */
    synchronized boolean customBEnabled(){return option("custom_b");}
    /** Update custom b preference. */
    synchronized boolean setCustomB(boolean v){return setOption("custom_b",v)&&option("custom_b")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class EffectsSystem extends FeatureModule {
    private static final String[] DEFAULTS={"hit_spark","crit_spark","shield_flash","totem_burst","low_health_pulse","combo_glow","loadout_swoosh","button_click","warning_flash","heal_pulse","block_break","damage_fade","kill_confirm","pearl_trail","subtle_particles"};
    EffectsSystem(){super("effects",DEFAULTS);}
    @Override synchronized String summary(){return "effects: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read hit spark preference. */
    synchronized boolean hitSparkEnabled(){return option("hit_spark");}
    /** Update hit spark preference. */
    synchronized boolean setHitSpark(boolean v){return setOption("hit_spark",v)&&option("hit_spark")==v;}

    /** Read crit spark preference. */
    synchronized boolean critSparkEnabled(){return option("crit_spark");}
    /** Update crit spark preference. */
    synchronized boolean setCritSpark(boolean v){return setOption("crit_spark",v)&&option("crit_spark")==v;}

    /** Read shield flash preference. */
    synchronized boolean shieldFlashEnabled(){return option("shield_flash");}
    /** Update shield flash preference. */
    synchronized boolean setShieldFlash(boolean v){return setOption("shield_flash",v)&&option("shield_flash")==v;}

    /** Read totem burst preference. */
    synchronized boolean totemBurstEnabled(){return option("totem_burst");}
    /** Update totem burst preference. */
    synchronized boolean setTotemBurst(boolean v){return setOption("totem_burst",v)&&option("totem_burst")==v;}

    /** Read low health pulse preference. */
    synchronized boolean lowHealthPulseEnabled(){return option("low_health_pulse");}
    /** Update low health pulse preference. */
    synchronized boolean setLowHealthPulse(boolean v){return setOption("low_health_pulse",v)&&option("low_health_pulse")==v;}

    /** Read combo glow preference. */
    synchronized boolean comboGlowEnabled(){return option("combo_glow");}
    /** Update combo glow preference. */
    synchronized boolean setComboGlow(boolean v){return setOption("combo_glow",v)&&option("combo_glow")==v;}

    /** Read loadout swoosh preference. */
    synchronized boolean loadoutSwooshEnabled(){return option("loadout_swoosh");}
    /** Update loadout swoosh preference. */
    synchronized boolean setLoadoutSwoosh(boolean v){return setOption("loadout_swoosh",v)&&option("loadout_swoosh")==v;}

    /** Read button click preference. */
    synchronized boolean buttonClickEnabled(){return option("button_click");}
    /** Update button click preference. */
    synchronized boolean setButtonClick(boolean v){return setOption("button_click",v)&&option("button_click")==v;}

    /** Read warning flash preference. */
    synchronized boolean warningFlashEnabled(){return option("warning_flash");}
    /** Update warning flash preference. */
    synchronized boolean setWarningFlash(boolean v){return setOption("warning_flash",v)&&option("warning_flash")==v;}

    /** Read heal pulse preference. */
    synchronized boolean healPulseEnabled(){return option("heal_pulse");}
    /** Update heal pulse preference. */
    synchronized boolean setHealPulse(boolean v){return setOption("heal_pulse",v)&&option("heal_pulse")==v;}

    /** Read block break preference. */
    synchronized boolean blockBreakEnabled(){return option("block_break");}
    /** Update block break preference. */
    synchronized boolean setBlockBreak(boolean v){return setOption("block_break",v)&&option("block_break")==v;}

    /** Read damage fade preference. */
    synchronized boolean damageFadeEnabled(){return option("damage_fade");}
    /** Update damage fade preference. */
    synchronized boolean setDamageFade(boolean v){return setOption("damage_fade",v)&&option("damage_fade")==v;}

    /** Read kill confirm preference. */
    synchronized boolean killConfirmEnabled(){return option("kill_confirm");}
    /** Update kill confirm preference. */
    synchronized boolean setKillConfirm(boolean v){return setOption("kill_confirm",v)&&option("kill_confirm")==v;}

    /** Read pearl trail preference. */
    synchronized boolean pearlTrailEnabled(){return option("pearl_trail");}
    /** Update pearl trail preference. */
    synchronized boolean setPearlTrail(boolean v){return setOption("pearl_trail",v)&&option("pearl_trail")==v;}

    /** Read subtle particles preference. */
    synchronized boolean subtleParticlesEnabled(){return option("subtle_particles");}
    /** Update subtle particles preference. */
    synchronized boolean setSubtleParticles(boolean v){return setOption("subtle_particles",v)&&option("subtle_particles")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class SettingsSystem extends FeatureModule {
    private static final String[] DEFAULTS={"hud_enabled","crosshair_enabled","animations_enabled","particles_enabled","sounds_enabled","compact_mode","left_handed","high_contrast","reduce_motion","show_target","show_cooldowns","show_coordinates","show_durability","show_combat_stats","auto_save","server_safe"};
    SettingsSystem(){super("settings",DEFAULTS);}
    @Override synchronized String summary(){return "settings: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read hud enabled preference. */
    synchronized boolean hudEnabledEnabled(){return option("hud_enabled");}
    /** Update hud enabled preference. */
    synchronized boolean setHudEnabled(boolean v){return setOption("hud_enabled",v)&&option("hud_enabled")==v;}

    /** Read crosshair enabled preference. */
    synchronized boolean crosshairEnabledEnabled(){return option("crosshair_enabled");}
    /** Update crosshair enabled preference. */
    synchronized boolean setCrosshairEnabled(boolean v){return setOption("crosshair_enabled",v)&&option("crosshair_enabled")==v;}

    /** Read animations enabled preference. */
    synchronized boolean animationsEnabledEnabled(){return option("animations_enabled");}
    /** Update animations enabled preference. */
    synchronized boolean setAnimationsEnabled(boolean v){return setOption("animations_enabled",v)&&option("animations_enabled")==v;}

    /** Read particles enabled preference. */
    synchronized boolean particlesEnabledEnabled(){return option("particles_enabled");}
    /** Update particles enabled preference. */
    synchronized boolean setParticlesEnabled(boolean v){return setOption("particles_enabled",v)&&option("particles_enabled")==v;}

    /** Read sounds enabled preference. */
    synchronized boolean soundsEnabledEnabled(){return option("sounds_enabled");}
    /** Update sounds enabled preference. */
    synchronized boolean setSoundsEnabled(boolean v){return setOption("sounds_enabled",v)&&option("sounds_enabled")==v;}

    /** Read compact mode preference. */
    synchronized boolean compactModeEnabled(){return option("compact_mode");}
    /** Update compact mode preference. */
    synchronized boolean setCompactMode(boolean v){return setOption("compact_mode",v)&&option("compact_mode")==v;}

    /** Read left handed preference. */
    synchronized boolean leftHandedEnabled(){return option("left_handed");}
    /** Update left handed preference. */
    synchronized boolean setLeftHanded(boolean v){return setOption("left_handed",v)&&option("left_handed")==v;}

    /** Read high contrast preference. */
    synchronized boolean highContrastEnabled(){return option("high_contrast");}
    /** Update high contrast preference. */
    synchronized boolean setHighContrast(boolean v){return setOption("high_contrast",v)&&option("high_contrast")==v;}

    /** Read reduce motion preference. */
    synchronized boolean reduceMotionEnabled(){return option("reduce_motion");}
    /** Update reduce motion preference. */
    synchronized boolean setReduceMotion(boolean v){return setOption("reduce_motion",v)&&option("reduce_motion")==v;}

    /** Read show target preference. */
    synchronized boolean showTargetEnabled(){return option("show_target");}
    /** Update show target preference. */
    synchronized boolean setShowTarget(boolean v){return setOption("show_target",v)&&option("show_target")==v;}

    /** Read show cooldowns preference. */
    synchronized boolean showCooldownsEnabled(){return option("show_cooldowns");}
    /** Update show cooldowns preference. */
    synchronized boolean setShowCooldowns(boolean v){return setOption("show_cooldowns",v)&&option("show_cooldowns")==v;}

    /** Read show coordinates preference. */
    synchronized boolean showCoordinatesEnabled(){return option("show_coordinates");}
    /** Update show coordinates preference. */
    synchronized boolean setShowCoordinates(boolean v){return setOption("show_coordinates",v)&&option("show_coordinates")==v;}

    /** Read show durability preference. */
    synchronized boolean showDurabilityEnabled(){return option("show_durability");}
    /** Update show durability preference. */
    synchronized boolean setShowDurability(boolean v){return setOption("show_durability",v)&&option("show_durability")==v;}

    /** Read show combat stats preference. */
    synchronized boolean showCombatStatsEnabled(){return option("show_combat_stats");}
    /** Update show combat stats preference. */
    synchronized boolean setShowCombatStats(boolean v){return setOption("show_combat_stats",v)&&option("show_combat_stats")==v;}

    /** Read auto save preference. */
    synchronized boolean autoSaveEnabled(){return option("auto_save");}
    /** Update auto save preference. */
    synchronized boolean setAutoSave(boolean v){return setOption("auto_save",v)&&option("auto_save")==v;}

    /** Read server safe preference. */
    synchronized boolean serverSafeEnabled(){return option("server_safe");}
    /** Update server safe preference. */
    synchronized boolean setServerSafe(boolean v){return setOption("server_safe",v)&&option("server_safe")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class HudLayoutSystem extends FeatureModule {
    private static final String[] DEFAULTS={"anchor_top_left","anchor_top_right","anchor_bottom_left","anchor_bottom_right","anchor_center","scale","opacity","spacing","margin","drag_mode","snap_to_grid","hide_in_menus","hide_when_chat","compact","reset_layout"};
    HudLayoutSystem(){super("hud_layout",DEFAULTS);}
    @Override synchronized String summary(){return "hud layout: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read anchor top left preference. */
    synchronized boolean anchorTopLeftEnabled(){return option("anchor_top_left");}
    /** Update anchor top left preference. */
    synchronized boolean setAnchorTopLeft(boolean v){return setOption("anchor_top_left",v)&&option("anchor_top_left")==v;}

    /** Read anchor top right preference. */
    synchronized boolean anchorTopRightEnabled(){return option("anchor_top_right");}
    /** Update anchor top right preference. */
    synchronized boolean setAnchorTopRight(boolean v){return setOption("anchor_top_right",v)&&option("anchor_top_right")==v;}

    /** Read anchor bottom left preference. */
    synchronized boolean anchorBottomLeftEnabled(){return option("anchor_bottom_left");}
    /** Update anchor bottom left preference. */
    synchronized boolean setAnchorBottomLeft(boolean v){return setOption("anchor_bottom_left",v)&&option("anchor_bottom_left")==v;}

    /** Read anchor bottom right preference. */
    synchronized boolean anchorBottomRightEnabled(){return option("anchor_bottom_right");}
    /** Update anchor bottom right preference. */
    synchronized boolean setAnchorBottomRight(boolean v){return setOption("anchor_bottom_right",v)&&option("anchor_bottom_right")==v;}

    /** Read anchor center preference. */
    synchronized boolean anchorCenterEnabled(){return option("anchor_center");}
    /** Update anchor center preference. */
    synchronized boolean setAnchorCenter(boolean v){return setOption("anchor_center",v)&&option("anchor_center")==v;}

    /** Read scale preference. */
    synchronized boolean scaleEnabled(){return option("scale");}
    /** Update scale preference. */
    synchronized boolean setScale(boolean v){return setOption("scale",v)&&option("scale")==v;}

    /** Read opacity preference. */
    synchronized boolean opacityEnabled(){return option("opacity");}
    /** Update opacity preference. */
    synchronized boolean setOpacity(boolean v){return setOption("opacity",v)&&option("opacity")==v;}

    /** Read spacing preference. */
    synchronized boolean spacingEnabled(){return option("spacing");}
    /** Update spacing preference. */
    synchronized boolean setSpacing(boolean v){return setOption("spacing",v)&&option("spacing")==v;}

    /** Read margin preference. */
    synchronized boolean marginEnabled(){return option("margin");}
    /** Update margin preference. */
    synchronized boolean setMargin(boolean v){return setOption("margin",v)&&option("margin")==v;}

    /** Read drag mode preference. */
    synchronized boolean dragModeEnabled(){return option("drag_mode");}
    /** Update drag mode preference. */
    synchronized boolean setDragMode(boolean v){return setOption("drag_mode",v)&&option("drag_mode")==v;}

    /** Read snap to grid preference. */
    synchronized boolean snapToGridEnabled(){return option("snap_to_grid");}
    /** Update snap to grid preference. */
    synchronized boolean setSnapToGrid(boolean v){return setOption("snap_to_grid",v)&&option("snap_to_grid")==v;}

    /** Read hide in menus preference. */
    synchronized boolean hideInMenusEnabled(){return option("hide_in_menus");}
    /** Update hide in menus preference. */
    synchronized boolean setHideInMenus(boolean v){return setOption("hide_in_menus",v)&&option("hide_in_menus")==v;}

    /** Read hide when chat preference. */
    synchronized boolean hideWhenChatEnabled(){return option("hide_when_chat");}
    /** Update hide when chat preference. */
    synchronized boolean setHideWhenChat(boolean v){return setOption("hide_when_chat",v)&&option("hide_when_chat")==v;}

    /** Read compact preference. */
    synchronized boolean compactEnabled(){return option("compact");}
    /** Update compact preference. */
    synchronized boolean setCompact(boolean v){return setOption("compact",v)&&option("compact")==v;}

    /** Read reset layout preference. */
    synchronized boolean resetLayoutEnabled(){return option("reset_layout");}
    /** Update reset layout preference. */
    synchronized boolean setResetLayout(boolean v){return setOption("reset_layout",v)&&option("reset_layout")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class SoundCueSystem extends FeatureModule {
    private static final String[] DEFAULTS={"hit","critical","shield_raise","shield_break","totem_pop","low_health","combo_start","combo_end","loadout_swap","warning","pearl_ready","potion_ready","attack_ready","kill_confirm","menu_open","menu_close"};
    SoundCueSystem(){super("sound_cues",DEFAULTS);}
    @Override synchronized String summary(){return "sound cues: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read hit preference. */
    synchronized boolean hitEnabled(){return option("hit");}
    /** Update hit preference. */
    synchronized boolean setHit(boolean v){return setOption("hit",v)&&option("hit")==v;}

    /** Read critical preference. */
    synchronized boolean criticalEnabled(){return option("critical");}
    /** Update critical preference. */
    synchronized boolean setCritical(boolean v){return setOption("critical",v)&&option("critical")==v;}

    /** Read shield raise preference. */
    synchronized boolean shieldRaiseEnabled(){return option("shield_raise");}
    /** Update shield raise preference. */
    synchronized boolean setShieldRaise(boolean v){return setOption("shield_raise",v)&&option("shield_raise")==v;}

    /** Read shield break preference. */
    synchronized boolean shieldBreakEnabled(){return option("shield_break");}
    /** Update shield break preference. */
    synchronized boolean setShieldBreak(boolean v){return setOption("shield_break",v)&&option("shield_break")==v;}

    /** Read totem pop preference. */
    synchronized boolean totemPopEnabled(){return option("totem_pop");}
    /** Update totem pop preference. */
    synchronized boolean setTotemPop(boolean v){return setOption("totem_pop",v)&&option("totem_pop")==v;}

    /** Read low health preference. */
    synchronized boolean lowHealthEnabled(){return option("low_health");}
    /** Update low health preference. */
    synchronized boolean setLowHealth(boolean v){return setOption("low_health",v)&&option("low_health")==v;}

    /** Read combo start preference. */
    synchronized boolean comboStartEnabled(){return option("combo_start");}
    /** Update combo start preference. */
    synchronized boolean setComboStart(boolean v){return setOption("combo_start",v)&&option("combo_start")==v;}

    /** Read combo end preference. */
    synchronized boolean comboEndEnabled(){return option("combo_end");}
    /** Update combo end preference. */
    synchronized boolean setComboEnd(boolean v){return setOption("combo_end",v)&&option("combo_end")==v;}

    /** Read loadout swap preference. */
    synchronized boolean loadoutSwapEnabled(){return option("loadout_swap");}
    /** Update loadout swap preference. */
    synchronized boolean setLoadoutSwap(boolean v){return setOption("loadout_swap",v)&&option("loadout_swap")==v;}

    /** Read warning preference. */
    synchronized boolean warningEnabled(){return option("warning");}
    /** Update warning preference. */
    synchronized boolean setWarning(boolean v){return setOption("warning",v)&&option("warning")==v;}

    /** Read pearl ready preference. */
    synchronized boolean pearlReadyEnabled(){return option("pearl_ready");}
    /** Update pearl ready preference. */
    synchronized boolean setPearlReady(boolean v){return setOption("pearl_ready",v)&&option("pearl_ready")==v;}

    /** Read potion ready preference. */
    synchronized boolean potionReadyEnabled(){return option("potion_ready");}
    /** Update potion ready preference. */
    synchronized boolean setPotionReady(boolean v){return setOption("potion_ready",v)&&option("potion_ready")==v;}

    /** Read attack ready preference. */
    synchronized boolean attackReadyEnabled(){return option("attack_ready");}
    /** Update attack ready preference. */
    synchronized boolean setAttackReady(boolean v){return setOption("attack_ready",v)&&option("attack_ready")==v;}

    /** Read kill confirm preference. */
    synchronized boolean killConfirmEnabled(){return option("kill_confirm");}
    /** Update kill confirm preference. */
    synchronized boolean setKillConfirm(boolean v){return setOption("kill_confirm",v)&&option("kill_confirm")==v;}

    /** Read menu open preference. */
    synchronized boolean menuOpenEnabled(){return option("menu_open");}
    /** Update menu open preference. */
    synchronized boolean setMenuOpen(boolean v){return setOption("menu_open",v)&&option("menu_open")==v;}

    /** Read menu close preference. */
    synchronized boolean menuCloseEnabled(){return option("menu_close");}
    /** Update menu close preference. */
    synchronized boolean setMenuClose(boolean v){return setOption("menu_close",v)&&option("menu_close")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class PerformanceSystem extends FeatureModule {
    private static final String[] DEFAULTS={"particle_budget","animation_budget","hud_refresh_rate","cache_enabled","low_end_mode","skip_hidden_widgets","throttle_target_scan","limit_combat_log","disable_shake","disable_blur","texture_cache","frame_time_sample","debug_overlay","metrics","auto_quality"};
    PerformanceSystem(){super("performance",DEFAULTS);}
    @Override synchronized String summary(){return "performance: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read particle budget preference. */
    synchronized boolean particleBudgetEnabled(){return option("particle_budget");}
    /** Update particle budget preference. */
    synchronized boolean setParticleBudget(boolean v){return setOption("particle_budget",v)&&option("particle_budget")==v;}

    /** Read animation budget preference. */
    synchronized boolean animationBudgetEnabled(){return option("animation_budget");}
    /** Update animation budget preference. */
    synchronized boolean setAnimationBudget(boolean v){return setOption("animation_budget",v)&&option("animation_budget")==v;}

    /** Read hud refresh rate preference. */
    synchronized boolean hudRefreshRateEnabled(){return option("hud_refresh_rate");}
    /** Update hud refresh rate preference. */
    synchronized boolean setHudRefreshRate(boolean v){return setOption("hud_refresh_rate",v)&&option("hud_refresh_rate")==v;}

    /** Read cache enabled preference. */
    synchronized boolean cacheEnabledEnabled(){return option("cache_enabled");}
    /** Update cache enabled preference. */
    synchronized boolean setCacheEnabled(boolean v){return setOption("cache_enabled",v)&&option("cache_enabled")==v;}

    /** Read low end mode preference. */
    synchronized boolean lowEndModeEnabled(){return option("low_end_mode");}
    /** Update low end mode preference. */
    synchronized boolean setLowEndMode(boolean v){return setOption("low_end_mode",v)&&option("low_end_mode")==v;}

    /** Read skip hidden widgets preference. */
    synchronized boolean skipHiddenWidgetsEnabled(){return option("skip_hidden_widgets");}
    /** Update skip hidden widgets preference. */
    synchronized boolean setSkipHiddenWidgets(boolean v){return setOption("skip_hidden_widgets",v)&&option("skip_hidden_widgets")==v;}

    /** Read throttle target scan preference. */
    synchronized boolean throttleTargetScanEnabled(){return option("throttle_target_scan");}
    /** Update throttle target scan preference. */
    synchronized boolean setThrottleTargetScan(boolean v){return setOption("throttle_target_scan",v)&&option("throttle_target_scan")==v;}

    /** Read limit combat log preference. */
    synchronized boolean limitCombatLogEnabled(){return option("limit_combat_log");}
    /** Update limit combat log preference. */
    synchronized boolean setLimitCombatLog(boolean v){return setOption("limit_combat_log",v)&&option("limit_combat_log")==v;}

    /** Read disable shake preference. */
    synchronized boolean disableShakeEnabled(){return option("disable_shake");}
    /** Update disable shake preference. */
    synchronized boolean setDisableShake(boolean v){return setOption("disable_shake",v)&&option("disable_shake")==v;}

    /** Read disable blur preference. */
    synchronized boolean disableBlurEnabled(){return option("disable_blur");}
    /** Update disable blur preference. */
    synchronized boolean setDisableBlur(boolean v){return setOption("disable_blur",v)&&option("disable_blur")==v;}

    /** Read texture cache preference. */
    synchronized boolean textureCacheEnabled(){return option("texture_cache");}
    /** Update texture cache preference. */
    synchronized boolean setTextureCache(boolean v){return setOption("texture_cache",v)&&option("texture_cache")==v;}

    /** Read frame time sample preference. */
    synchronized boolean frameTimeSampleEnabled(){return option("frame_time_sample");}
    /** Update frame time sample preference. */
    synchronized boolean setFrameTimeSample(boolean v){return setOption("frame_time_sample",v)&&option("frame_time_sample")==v;}

    /** Read debug overlay preference. */
    synchronized boolean debugOverlayEnabled(){return option("debug_overlay");}
    /** Update debug overlay preference. */
    synchronized boolean setDebugOverlay(boolean v){return setOption("debug_overlay",v)&&option("debug_overlay")==v;}

    /** Read metrics preference. */
    synchronized boolean metricsEnabled(){return option("metrics");}
    /** Update metrics preference. */
    synchronized boolean setMetrics(boolean v){return setOption("metrics",v)&&option("metrics")==v;}

    /** Read auto quality preference. */
    synchronized boolean autoQualityEnabled(){return option("auto_quality");}
    /** Update auto quality preference. */
    synchronized boolean setAutoQuality(boolean v){return setOption("auto_quality",v)&&option("auto_quality")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class CombatLogSystem extends FeatureModule {
    private static final String[] DEFAULTS={"record_hits","record_damage","record_crits","record_totem_pops","record_shield_breaks","record_loadout_swaps","record_potions","record_projectiles","session_start","session_end","export_summary","clear_history","history_limit","group_events","privacy_mode"};
    CombatLogSystem(){super("combat_log",DEFAULTS);}
    @Override synchronized String summary(){return "combat log: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read record hits preference. */
    synchronized boolean recordHitsEnabled(){return option("record_hits");}
    /** Update record hits preference. */
    synchronized boolean setRecordHits(boolean v){return setOption("record_hits",v)&&option("record_hits")==v;}

    /** Read record damage preference. */
    synchronized boolean recordDamageEnabled(){return option("record_damage");}
    /** Update record damage preference. */
    synchronized boolean setRecordDamage(boolean v){return setOption("record_damage",v)&&option("record_damage")==v;}

    /** Read record crits preference. */
    synchronized boolean recordCritsEnabled(){return option("record_crits");}
    /** Update record crits preference. */
    synchronized boolean setRecordCrits(boolean v){return setOption("record_crits",v)&&option("record_crits")==v;}

    /** Read record totem pops preference. */
    synchronized boolean recordTotemPopsEnabled(){return option("record_totem_pops");}
    /** Update record totem pops preference. */
    synchronized boolean setRecordTotemPops(boolean v){return setOption("record_totem_pops",v)&&option("record_totem_pops")==v;}

    /** Read record shield breaks preference. */
    synchronized boolean recordShieldBreaksEnabled(){return option("record_shield_breaks");}
    /** Update record shield breaks preference. */
    synchronized boolean setRecordShieldBreaks(boolean v){return setOption("record_shield_breaks",v)&&option("record_shield_breaks")==v;}

    /** Read record loadout swaps preference. */
    synchronized boolean recordLoadoutSwapsEnabled(){return option("record_loadout_swaps");}
    /** Update record loadout swaps preference. */
    synchronized boolean setRecordLoadoutSwaps(boolean v){return setOption("record_loadout_swaps",v)&&option("record_loadout_swaps")==v;}

    /** Read record potions preference. */
    synchronized boolean recordPotionsEnabled(){return option("record_potions");}
    /** Update record potions preference. */
    synchronized boolean setRecordPotions(boolean v){return setOption("record_potions",v)&&option("record_potions")==v;}

    /** Read record projectiles preference. */
    synchronized boolean recordProjectilesEnabled(){return option("record_projectiles");}
    /** Update record projectiles preference. */
    synchronized boolean setRecordProjectiles(boolean v){return setOption("record_projectiles",v)&&option("record_projectiles")==v;}

    /** Read session start preference. */
    synchronized boolean sessionStartEnabled(){return option("session_start");}
    /** Update session start preference. */
    synchronized boolean setSessionStart(boolean v){return setOption("session_start",v)&&option("session_start")==v;}

    /** Read session end preference. */
    synchronized boolean sessionEndEnabled(){return option("session_end");}
    /** Update session end preference. */
    synchronized boolean setSessionEnd(boolean v){return setOption("session_end",v)&&option("session_end")==v;}

    /** Read export summary preference. */
    synchronized boolean exportSummaryEnabled(){return option("export_summary");}
    /** Update export summary preference. */
    synchronized boolean setExportSummary(boolean v){return setOption("export_summary",v)&&option("export_summary")==v;}

    /** Read clear history preference. */
    synchronized boolean clearHistoryEnabled(){return option("clear_history");}
    /** Update clear history preference. */
    synchronized boolean setClearHistory(boolean v){return setOption("clear_history",v)&&option("clear_history")==v;}

    /** Read history limit preference. */
    synchronized boolean historyLimitEnabled(){return option("history_limit");}
    /** Update history limit preference. */
    synchronized boolean setHistoryLimit(boolean v){return setOption("history_limit",v)&&option("history_limit")==v;}

    /** Read group events preference. */
    synchronized boolean groupEventsEnabled(){return option("group_events");}
    /** Update group events preference. */
    synchronized boolean setGroupEvents(boolean v){return setOption("group_events",v)&&option("group_events")==v;}

    /** Read privacy mode preference. */
    synchronized boolean privacyModeEnabled(){return option("privacy_mode");}
    /** Update privacy mode preference. */
    synchronized boolean setPrivacyMode(boolean v){return setOption("privacy_mode",v)&&option("privacy_mode")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class ProfileSystem extends FeatureModule {
    private static final String[] DEFAULTS={"default","competitive","casual","minimal","high_visibility","streamer","low_end","crystal","shield","custom_a","custom_b","import_profile","export_profile","duplicate_profile","reset_profile","profile_lock"};
    ProfileSystem(){super("profiles",DEFAULTS);}
    @Override synchronized String summary(){return "profiles: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read default preference. */
    synchronized boolean defaultEnabled(){return option("default");}
    /** Update default preference. */
    synchronized boolean setDefault(boolean v){return setOption("default",v)&&option("default")==v;}

    /** Read competitive preference. */
    synchronized boolean competitiveEnabled(){return option("competitive");}
    /** Update competitive preference. */
    synchronized boolean setCompetitive(boolean v){return setOption("competitive",v)&&option("competitive")==v;}

    /** Read casual preference. */
    synchronized boolean casualEnabled(){return option("casual");}
    /** Update casual preference. */
    synchronized boolean setCasual(boolean v){return setOption("casual",v)&&option("casual")==v;}

    /** Read minimal preference. */
    synchronized boolean minimalEnabled(){return option("minimal");}
    /** Update minimal preference. */
    synchronized boolean setMinimal(boolean v){return setOption("minimal",v)&&option("minimal")==v;}

    /** Read high visibility preference. */
    synchronized boolean highVisibilityEnabled(){return option("high_visibility");}
    /** Update high visibility preference. */
    synchronized boolean setHighVisibility(boolean v){return setOption("high_visibility",v)&&option("high_visibility")==v;}

    /** Read streamer preference. */
    synchronized boolean streamerEnabled(){return option("streamer");}
    /** Update streamer preference. */
    synchronized boolean setStreamer(boolean v){return setOption("streamer",v)&&option("streamer")==v;}

    /** Read low end preference. */
    synchronized boolean lowEndEnabled(){return option("low_end");}
    /** Update low end preference. */
    synchronized boolean setLowEnd(boolean v){return setOption("low_end",v)&&option("low_end")==v;}

    /** Read crystal preference. */
    synchronized boolean crystalEnabled(){return option("crystal");}
    /** Update crystal preference. */
    synchronized boolean setCrystal(boolean v){return setOption("crystal",v)&&option("crystal")==v;}

    /** Read shield preference. */
    synchronized boolean shieldEnabled(){return option("shield");}
    /** Update shield preference. */
    synchronized boolean setShield(boolean v){return setOption("shield",v)&&option("shield")==v;}

    /** Read custom a preference. */
    synchronized boolean customAEnabled(){return option("custom_a");}
    /** Update custom a preference. */
    synchronized boolean setCustomA(boolean v){return setOption("custom_a",v)&&option("custom_a")==v;}

    /** Read custom b preference. */
    synchronized boolean customBEnabled(){return option("custom_b");}
    /** Update custom b preference. */
    synchronized boolean setCustomB(boolean v){return setOption("custom_b",v)&&option("custom_b")==v;}

    /** Read import profile preference. */
    synchronized boolean importProfileEnabled(){return option("import_profile");}
    /** Update import profile preference. */
    synchronized boolean setImportProfile(boolean v){return setOption("import_profile",v)&&option("import_profile")==v;}

    /** Read export profile preference. */
    synchronized boolean exportProfileEnabled(){return option("export_profile");}
    /** Update export profile preference. */
    synchronized boolean setExportProfile(boolean v){return setOption("export_profile",v)&&option("export_profile")==v;}

    /** Read duplicate profile preference. */
    synchronized boolean duplicateProfileEnabled(){return option("duplicate_profile");}
    /** Update duplicate profile preference. */
    synchronized boolean setDuplicateProfile(boolean v){return setOption("duplicate_profile",v)&&option("duplicate_profile")==v;}

    /** Read reset profile preference. */
    synchronized boolean resetProfileEnabled(){return option("reset_profile");}
    /** Update reset profile preference. */
    synchronized boolean setResetProfile(boolean v){return setOption("reset_profile",v)&&option("reset_profile")==v;}

    /** Read profile lock preference. */
    synchronized boolean profileLockEnabled(){return option("profile_lock");}
    /** Update profile lock preference. */
    synchronized boolean setProfileLock(boolean v){return setOption("profile_lock",v)&&option("profile_lock")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class WeaponDurabilitySystem extends FeatureModule {
    private static final String[] DEFAULTS={"sword","axe","bow","crossbow","trident","mace","shield","elytra","helmet","chestplate","leggings","boots","warning_25","warning_10","break_alert","repair_hint"};
    WeaponDurabilitySystem(){super("weapon_durability",DEFAULTS);}
    @Override synchronized String summary(){return "weapon durability: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read sword preference. */
    synchronized boolean swordEnabled(){return option("sword");}
    /** Update sword preference. */
    synchronized boolean setSword(boolean v){return setOption("sword",v)&&option("sword")==v;}

    /** Read axe preference. */
    synchronized boolean axeEnabled(){return option("axe");}
    /** Update axe preference. */
    synchronized boolean setAxe(boolean v){return setOption("axe",v)&&option("axe")==v;}

    /** Read bow preference. */
    synchronized boolean bowEnabled(){return option("bow");}
    /** Update bow preference. */
    synchronized boolean setBow(boolean v){return setOption("bow",v)&&option("bow")==v;}

    /** Read crossbow preference. */
    synchronized boolean crossbowEnabled(){return option("crossbow");}
    /** Update crossbow preference. */
    synchronized boolean setCrossbow(boolean v){return setOption("crossbow",v)&&option("crossbow")==v;}

    /** Read trident preference. */
    synchronized boolean tridentEnabled(){return option("trident");}
    /** Update trident preference. */
    synchronized boolean setTrident(boolean v){return setOption("trident",v)&&option("trident")==v;}

    /** Read mace preference. */
    synchronized boolean maceEnabled(){return option("mace");}
    /** Update mace preference. */
    synchronized boolean setMace(boolean v){return setOption("mace",v)&&option("mace")==v;}

    /** Read shield preference. */
    synchronized boolean shieldEnabled(){return option("shield");}
    /** Update shield preference. */
    synchronized boolean setShield(boolean v){return setOption("shield",v)&&option("shield")==v;}

    /** Read elytra preference. */
    synchronized boolean elytraEnabled(){return option("elytra");}
    /** Update elytra preference. */
    synchronized boolean setElytra(boolean v){return setOption("elytra",v)&&option("elytra")==v;}

    /** Read helmet preference. */
    synchronized boolean helmetEnabled(){return option("helmet");}
    /** Update helmet preference. */
    synchronized boolean setHelmet(boolean v){return setOption("helmet",v)&&option("helmet")==v;}

    /** Read chestplate preference. */
    synchronized boolean chestplateEnabled(){return option("chestplate");}
    /** Update chestplate preference. */
    synchronized boolean setChestplate(boolean v){return setOption("chestplate",v)&&option("chestplate")==v;}

    /** Read leggings preference. */
    synchronized boolean leggingsEnabled(){return option("leggings");}
    /** Update leggings preference. */
    synchronized boolean setLeggings(boolean v){return setOption("leggings",v)&&option("leggings")==v;}

    /** Read boots preference. */
    synchronized boolean bootsEnabled(){return option("boots");}
    /** Update boots preference. */
    synchronized boolean setBoots(boolean v){return setOption("boots",v)&&option("boots")==v;}

    /** Read warning 25 preference. */
    synchronized boolean warning25Enabled(){return option("warning_25");}
    /** Update warning 25 preference. */
    synchronized boolean setWarning25(boolean v){return setOption("warning_25",v)&&option("warning_25")==v;}

    /** Read warning 10 preference. */
    synchronized boolean warning10Enabled(){return option("warning_10");}
    /** Update warning 10 preference. */
    synchronized boolean setWarning10(boolean v){return setOption("warning_10",v)&&option("warning_10")==v;}

    /** Read break alert preference. */
    synchronized boolean breakAlertEnabled(){return option("break_alert");}
    /** Update break alert preference. */
    synchronized boolean setBreakAlert(boolean v){return setOption("break_alert",v)&&option("break_alert")==v;}

    /** Read repair hint preference. */
    synchronized boolean repairHintEnabled(){return option("repair_hint");}
    /** Update repair hint preference. */
    synchronized boolean setRepairHint(boolean v){return setOption("repair_hint",v)&&option("repair_hint")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class ProjectileAssistantSystem extends FeatureModule {
    private static final String[] DEFAULTS={"arrow_ready","arrow_charge","trident_charge","pearl_ready","snowball_count","egg_count","wind_charge_count","trajectory_hint","projectile_warning","firework_count","water_bucket_hint","aim_sensitivity","throwable_hotbar","projectile_cooldown","hit_confirmation"};
    ProjectileAssistantSystem(){super("projectile_assist",DEFAULTS);}
    @Override synchronized String summary(){return "projectile assist: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read arrow ready preference. */
    synchronized boolean arrowReadyEnabled(){return option("arrow_ready");}
    /** Update arrow ready preference. */
    synchronized boolean setArrowReady(boolean v){return setOption("arrow_ready",v)&&option("arrow_ready")==v;}

    /** Read arrow charge preference. */
    synchronized boolean arrowChargeEnabled(){return option("arrow_charge");}
    /** Update arrow charge preference. */
    synchronized boolean setArrowCharge(boolean v){return setOption("arrow_charge",v)&&option("arrow_charge")==v;}

    /** Read trident charge preference. */
    synchronized boolean tridentChargeEnabled(){return option("trident_charge");}
    /** Update trident charge preference. */
    synchronized boolean setTridentCharge(boolean v){return setOption("trident_charge",v)&&option("trident_charge")==v;}

    /** Read pearl ready preference. */
    synchronized boolean pearlReadyEnabled(){return option("pearl_ready");}
    /** Update pearl ready preference. */
    synchronized boolean setPearlReady(boolean v){return setOption("pearl_ready",v)&&option("pearl_ready")==v;}

    /** Read snowball count preference. */
    synchronized boolean snowballCountEnabled(){return option("snowball_count");}
    /** Update snowball count preference. */
    synchronized boolean setSnowballCount(boolean v){return setOption("snowball_count",v)&&option("snowball_count")==v;}

    /** Read egg count preference. */
    synchronized boolean eggCountEnabled(){return option("egg_count");}
    /** Update egg count preference. */
    synchronized boolean setEggCount(boolean v){return setOption("egg_count",v)&&option("egg_count")==v;}

    /** Read wind charge count preference. */
    synchronized boolean windChargeCountEnabled(){return option("wind_charge_count");}
    /** Update wind charge count preference. */
    synchronized boolean setWindChargeCount(boolean v){return setOption("wind_charge_count",v)&&option("wind_charge_count")==v;}

    /** Read trajectory hint preference. */
    synchronized boolean trajectoryHintEnabled(){return option("trajectory_hint");}
    /** Update trajectory hint preference. */
    synchronized boolean setTrajectoryHint(boolean v){return setOption("trajectory_hint",v)&&option("trajectory_hint")==v;}

    /** Read projectile warning preference. */
    synchronized boolean projectileWarningEnabled(){return option("projectile_warning");}
    /** Update projectile warning preference. */
    synchronized boolean setProjectileWarning(boolean v){return setOption("projectile_warning",v)&&option("projectile_warning")==v;}

    /** Read firework count preference. */
    synchronized boolean fireworkCountEnabled(){return option("firework_count");}
    /** Update firework count preference. */
    synchronized boolean setFireworkCount(boolean v){return setOption("firework_count",v)&&option("firework_count")==v;}

    /** Read water bucket hint preference. */
    synchronized boolean waterBucketHintEnabled(){return option("water_bucket_hint");}
    /** Update water bucket hint preference. */
    synchronized boolean setWaterBucketHint(boolean v){return setOption("water_bucket_hint",v)&&option("water_bucket_hint")==v;}

    /** Read aim sensitivity preference. */
    synchronized boolean aimSensitivityEnabled(){return option("aim_sensitivity");}
    /** Update aim sensitivity preference. */
    synchronized boolean setAimSensitivity(boolean v){return setOption("aim_sensitivity",v)&&option("aim_sensitivity")==v;}

    /** Read throwable hotbar preference. */
    synchronized boolean throwableHotbarEnabled(){return option("throwable_hotbar");}
    /** Update throwable hotbar preference. */
    synchronized boolean setThrowableHotbar(boolean v){return setOption("throwable_hotbar",v)&&option("throwable_hotbar")==v;}

    /** Read projectile cooldown preference. */
    synchronized boolean projectileCooldownEnabled(){return option("projectile_cooldown");}
    /** Update projectile cooldown preference. */
    synchronized boolean setProjectileCooldown(boolean v){return setOption("projectile_cooldown",v)&&option("projectile_cooldown")==v;}

    /** Read hit confirmation preference. */
    synchronized boolean hitConfirmationEnabled(){return option("hit_confirmation");}
    /** Update hit confirmation preference. */
    synchronized boolean setHitConfirmation(boolean v){return setOption("hit_confirmation",v)&&option("hit_confirmation")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class MovementAssistantSystem extends FeatureModule {
    private static final String[] DEFAULTS={"sprint_state","jump_state","fall_distance","water_state","elytra_state","velocity_hint","knockback_direction","landing_warning","ledge_warning","strafe_hint","sprint_reset","climb_state","slow_fall","grounded_state","damage_window"};
    MovementAssistantSystem(){super("movement_assist",DEFAULTS);}
    @Override synchronized String summary(){return "movement assist: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read sprint state preference. */
    synchronized boolean sprintStateEnabled(){return option("sprint_state");}
    /** Update sprint state preference. */
    synchronized boolean setSprintState(boolean v){return setOption("sprint_state",v)&&option("sprint_state")==v;}

    /** Read jump state preference. */
    synchronized boolean jumpStateEnabled(){return option("jump_state");}
    /** Update jump state preference. */
    synchronized boolean setJumpState(boolean v){return setOption("jump_state",v)&&option("jump_state")==v;}

    /** Read fall distance preference. */
    synchronized boolean fallDistanceEnabled(){return option("fall_distance");}
    /** Update fall distance preference. */
    synchronized boolean setFallDistance(boolean v){return setOption("fall_distance",v)&&option("fall_distance")==v;}

    /** Read water state preference. */
    synchronized boolean waterStateEnabled(){return option("water_state");}
    /** Update water state preference. */
    synchronized boolean setWaterState(boolean v){return setOption("water_state",v)&&option("water_state")==v;}

    /** Read elytra state preference. */
    synchronized boolean elytraStateEnabled(){return option("elytra_state");}
    /** Update elytra state preference. */
    synchronized boolean setElytraState(boolean v){return setOption("elytra_state",v)&&option("elytra_state")==v;}

    /** Read velocity hint preference. */
    synchronized boolean velocityHintEnabled(){return option("velocity_hint");}
    /** Update velocity hint preference. */
    synchronized boolean setVelocityHint(boolean v){return setOption("velocity_hint",v)&&option("velocity_hint")==v;}

    /** Read knockback direction preference. */
    synchronized boolean knockbackDirectionEnabled(){return option("knockback_direction");}
    /** Update knockback direction preference. */
    synchronized boolean setKnockbackDirection(boolean v){return setOption("knockback_direction",v)&&option("knockback_direction")==v;}

    /** Read landing warning preference. */
    synchronized boolean landingWarningEnabled(){return option("landing_warning");}
    /** Update landing warning preference. */
    synchronized boolean setLandingWarning(boolean v){return setOption("landing_warning",v)&&option("landing_warning")==v;}

    /** Read ledge warning preference. */
    synchronized boolean ledgeWarningEnabled(){return option("ledge_warning");}
    /** Update ledge warning preference. */
    synchronized boolean setLedgeWarning(boolean v){return setOption("ledge_warning",v)&&option("ledge_warning")==v;}

    /** Read strafe hint preference. */
    synchronized boolean strafeHintEnabled(){return option("strafe_hint");}
    /** Update strafe hint preference. */
    synchronized boolean setStrafeHint(boolean v){return setOption("strafe_hint",v)&&option("strafe_hint")==v;}

    /** Read sprint reset preference. */
    synchronized boolean sprintResetEnabled(){return option("sprint_reset");}
    /** Update sprint reset preference. */
    synchronized boolean setSprintReset(boolean v){return setOption("sprint_reset",v)&&option("sprint_reset")==v;}

    /** Read climb state preference. */
    synchronized boolean climbStateEnabled(){return option("climb_state");}
    /** Update climb state preference. */
    synchronized boolean setClimbState(boolean v){return setOption("climb_state",v)&&option("climb_state")==v;}

    /** Read slow fall preference. */
    synchronized boolean slowFallEnabled(){return option("slow_fall");}
    /** Update slow fall preference. */
    synchronized boolean setSlowFall(boolean v){return setOption("slow_fall",v)&&option("slow_fall")==v;}

    /** Read grounded state preference. */
    synchronized boolean groundedStateEnabled(){return option("grounded_state");}
    /** Update grounded state preference. */
    synchronized boolean setGroundedState(boolean v){return setOption("grounded_state",v)&&option("grounded_state")==v;}

    /** Read damage window preference. */
    synchronized boolean damageWindowEnabled(){return option("damage_window");}
    /** Update damage window preference. */
    synchronized boolean setDamageWindow(boolean v){return setOption("damage_window",v)&&option("damage_window")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class ResourceTrackerSystem extends FeatureModule {
    private static final String[] DEFAULTS={"pearls","crystals","obsidian","anchors","glowstone","arrows","food","blocks","water_buckets","totems","potions","wind_charges","fireworks","repair_materials","low_resource_alert"};
    ResourceTrackerSystem(){super("resource_tracker",DEFAULTS);}
    @Override synchronized String summary(){return "resource tracker: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read pearls preference. */
    synchronized boolean pearlsEnabled(){return option("pearls");}
    /** Update pearls preference. */
    synchronized boolean setPearls(boolean v){return setOption("pearls",v)&&option("pearls")==v;}

    /** Read crystals preference. */
    synchronized boolean crystalsEnabled(){return option("crystals");}
    /** Update crystals preference. */
    synchronized boolean setCrystals(boolean v){return setOption("crystals",v)&&option("crystals")==v;}

    /** Read obsidian preference. */
    synchronized boolean obsidianEnabled(){return option("obsidian");}
    /** Update obsidian preference. */
    synchronized boolean setObsidian(boolean v){return setOption("obsidian",v)&&option("obsidian")==v;}

    /** Read anchors preference. */
    synchronized boolean anchorsEnabled(){return option("anchors");}
    /** Update anchors preference. */
    synchronized boolean setAnchors(boolean v){return setOption("anchors",v)&&option("anchors")==v;}

    /** Read glowstone preference. */
    synchronized boolean glowstoneEnabled(){return option("glowstone");}
    /** Update glowstone preference. */
    synchronized boolean setGlowstone(boolean v){return setOption("glowstone",v)&&option("glowstone")==v;}

    /** Read arrows preference. */
    synchronized boolean arrowsEnabled(){return option("arrows");}
    /** Update arrows preference. */
    synchronized boolean setArrows(boolean v){return setOption("arrows",v)&&option("arrows")==v;}

    /** Read food preference. */
    synchronized boolean foodEnabled(){return option("food");}
    /** Update food preference. */
    synchronized boolean setFood(boolean v){return setOption("food",v)&&option("food")==v;}

    /** Read blocks preference. */
    synchronized boolean blocksEnabled(){return option("blocks");}
    /** Update blocks preference. */
    synchronized boolean setBlocks(boolean v){return setOption("blocks",v)&&option("blocks")==v;}

    /** Read water buckets preference. */
    synchronized boolean waterBucketsEnabled(){return option("water_buckets");}
    /** Update water buckets preference. */
    synchronized boolean setWaterBuckets(boolean v){return setOption("water_buckets",v)&&option("water_buckets")==v;}

    /** Read totems preference. */
    synchronized boolean totemsEnabled(){return option("totems");}
    /** Update totems preference. */
    synchronized boolean setTotems(boolean v){return setOption("totems",v)&&option("totems")==v;}

    /** Read potions preference. */
    synchronized boolean potionsEnabled(){return option("potions");}
    /** Update potions preference. */
    synchronized boolean setPotions(boolean v){return setOption("potions",v)&&option("potions")==v;}

    /** Read wind charges preference. */
    synchronized boolean windChargesEnabled(){return option("wind_charges");}
    /** Update wind charges preference. */
    synchronized boolean setWindCharges(boolean v){return setOption("wind_charges",v)&&option("wind_charges")==v;}

    /** Read fireworks preference. */
    synchronized boolean fireworksEnabled(){return option("fireworks");}
    /** Update fireworks preference. */
    synchronized boolean setFireworks(boolean v){return setOption("fireworks",v)&&option("fireworks")==v;}

    /** Read repair materials preference. */
    synchronized boolean repairMaterialsEnabled(){return option("repair_materials");}
    /** Update repair materials preference. */
    synchronized boolean setRepairMaterials(boolean v){return setOption("repair_materials",v)&&option("repair_materials")==v;}

    /** Read low resource alert preference. */
    synchronized boolean lowResourceAlertEnabled(){return option("low_resource_alert");}
    /** Update low resource alert preference. */
    synchronized boolean setLowResourceAlert(boolean v){return setOption("low_resource_alert",v)&&option("low_resource_alert")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class FriendMarkerSystem extends FeatureModule {
    private static final String[] DEFAULTS={"friend_list","team_color","ally_crosshair","ally_nameplate","ally_target_exclusion","friend_import","friend_export","friend_add","friend_remove","team_sync","privacy_mode","offline_friends","friend_count","marker_opacity","marker_size"};
    FriendMarkerSystem(){super("friend_marker",DEFAULTS);}
    @Override synchronized String summary(){return "friend marker: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read friend list preference. */
    synchronized boolean friendListEnabled(){return option("friend_list");}
    /** Update friend list preference. */
    synchronized boolean setFriendList(boolean v){return setOption("friend_list",v)&&option("friend_list")==v;}

    /** Read team color preference. */
    synchronized boolean teamColorEnabled(){return option("team_color");}
    /** Update team color preference. */
    synchronized boolean setTeamColor(boolean v){return setOption("team_color",v)&&option("team_color")==v;}

    /** Read ally crosshair preference. */
    synchronized boolean allyCrosshairEnabled(){return option("ally_crosshair");}
    /** Update ally crosshair preference. */
    synchronized boolean setAllyCrosshair(boolean v){return setOption("ally_crosshair",v)&&option("ally_crosshair")==v;}

    /** Read ally nameplate preference. */
    synchronized boolean allyNameplateEnabled(){return option("ally_nameplate");}
    /** Update ally nameplate preference. */
    synchronized boolean setAllyNameplate(boolean v){return setOption("ally_nameplate",v)&&option("ally_nameplate")==v;}

    /** Read ally target exclusion preference. */
    synchronized boolean allyTargetExclusionEnabled(){return option("ally_target_exclusion");}
    /** Update ally target exclusion preference. */
    synchronized boolean setAllyTargetExclusion(boolean v){return setOption("ally_target_exclusion",v)&&option("ally_target_exclusion")==v;}

    /** Read friend import preference. */
    synchronized boolean friendImportEnabled(){return option("friend_import");}
    /** Update friend import preference. */
    synchronized boolean setFriendImport(boolean v){return setOption("friend_import",v)&&option("friend_import")==v;}

    /** Read friend export preference. */
    synchronized boolean friendExportEnabled(){return option("friend_export");}
    /** Update friend export preference. */
    synchronized boolean setFriendExport(boolean v){return setOption("friend_export",v)&&option("friend_export")==v;}

    /** Read friend add preference. */
    synchronized boolean friendAddEnabled(){return option("friend_add");}
    /** Update friend add preference. */
    synchronized boolean setFriendAdd(boolean v){return setOption("friend_add",v)&&option("friend_add")==v;}

    /** Read friend remove preference. */
    synchronized boolean friendRemoveEnabled(){return option("friend_remove");}
    /** Update friend remove preference. */
    synchronized boolean setFriendRemove(boolean v){return setOption("friend_remove",v)&&option("friend_remove")==v;}

    /** Read team sync preference. */
    synchronized boolean teamSyncEnabled(){return option("team_sync");}
    /** Update team sync preference. */
    synchronized boolean setTeamSync(boolean v){return setOption("team_sync",v)&&option("team_sync")==v;}

    /** Read privacy mode preference. */
    synchronized boolean privacyModeEnabled(){return option("privacy_mode");}
    /** Update privacy mode preference. */
    synchronized boolean setPrivacyMode(boolean v){return setOption("privacy_mode",v)&&option("privacy_mode")==v;}

    /** Read offline friends preference. */
    synchronized boolean offlineFriendsEnabled(){return option("offline_friends");}
    /** Update offline friends preference. */
    synchronized boolean setOfflineFriends(boolean v){return setOption("offline_friends",v)&&option("offline_friends")==v;}

    /** Read friend count preference. */
    synchronized boolean friendCountEnabled(){return option("friend_count");}
    /** Update friend count preference. */
    synchronized boolean setFriendCount(boolean v){return setOption("friend_count",v)&&option("friend_count")==v;}

    /** Read marker opacity preference. */
    synchronized boolean markerOpacityEnabled(){return option("marker_opacity");}
    /** Update marker opacity preference. */
    synchronized boolean setMarkerOpacity(boolean v){return setOption("marker_opacity",v)&&option("marker_opacity")==v;}

    /** Read marker size preference. */
    synchronized boolean markerSizeEnabled(){return option("marker_size");}
    /** Update marker size preference. */
    synchronized boolean setMarkerSize(boolean v){return setOption("marker_size",v)&&option("marker_size")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class SessionStatsSystem extends FeatureModule {
    private static final String[] DEFAULTS={"kills","deaths","assists","hits","misses","damage_dealt","damage_taken","healing","totem_pops","pearls_used","arrows_hit","session_duration","win_streak","loss_streak","export_stats"};
    SessionStatsSystem(){super("session_stats",DEFAULTS);}
    @Override synchronized String summary(){return "session stats: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read kills preference. */
    synchronized boolean killsEnabled(){return option("kills");}
    /** Update kills preference. */
    synchronized boolean setKills(boolean v){return setOption("kills",v)&&option("kills")==v;}

    /** Read deaths preference. */
    synchronized boolean deathsEnabled(){return option("deaths");}
    /** Update deaths preference. */
    synchronized boolean setDeaths(boolean v){return setOption("deaths",v)&&option("deaths")==v;}

    /** Read assists preference. */
    synchronized boolean assistsEnabled(){return option("assists");}
    /** Update assists preference. */
    synchronized boolean setAssists(boolean v){return setOption("assists",v)&&option("assists")==v;}

    /** Read hits preference. */
    synchronized boolean hitsEnabled(){return option("hits");}
    /** Update hits preference. */
    synchronized boolean setHits(boolean v){return setOption("hits",v)&&option("hits")==v;}

    /** Read misses preference. */
    synchronized boolean missesEnabled(){return option("misses");}
    /** Update misses preference. */
    synchronized boolean setMisses(boolean v){return setOption("misses",v)&&option("misses")==v;}

    /** Read damage dealt preference. */
    synchronized boolean damageDealtEnabled(){return option("damage_dealt");}
    /** Update damage dealt preference. */
    synchronized boolean setDamageDealt(boolean v){return setOption("damage_dealt",v)&&option("damage_dealt")==v;}

    /** Read damage taken preference. */
    synchronized boolean damageTakenEnabled(){return option("damage_taken");}
    /** Update damage taken preference. */
    synchronized boolean setDamageTaken(boolean v){return setOption("damage_taken",v)&&option("damage_taken")==v;}

    /** Read healing preference. */
    synchronized boolean healingEnabled(){return option("healing");}
    /** Update healing preference. */
    synchronized boolean setHealing(boolean v){return setOption("healing",v)&&option("healing")==v;}

    /** Read totem pops preference. */
    synchronized boolean totemPopsEnabled(){return option("totem_pops");}
    /** Update totem pops preference. */
    synchronized boolean setTotemPops(boolean v){return setOption("totem_pops",v)&&option("totem_pops")==v;}

    /** Read pearls used preference. */
    synchronized boolean pearlsUsedEnabled(){return option("pearls_used");}
    /** Update pearls used preference. */
    synchronized boolean setPearlsUsed(boolean v){return setOption("pearls_used",v)&&option("pearls_used")==v;}

    /** Read arrows hit preference. */
    synchronized boolean arrowsHitEnabled(){return option("arrows_hit");}
    /** Update arrows hit preference. */
    synchronized boolean setArrowsHit(boolean v){return setOption("arrows_hit",v)&&option("arrows_hit")==v;}

    /** Read session duration preference. */
    synchronized boolean sessionDurationEnabled(){return option("session_duration");}
    /** Update session duration preference. */
    synchronized boolean setSessionDuration(boolean v){return setOption("session_duration",v)&&option("session_duration")==v;}

    /** Read win streak preference. */
    synchronized boolean winStreakEnabled(){return option("win_streak");}
    /** Update win streak preference. */
    synchronized boolean setWinStreak(boolean v){return setOption("win_streak",v)&&option("win_streak")==v;}

    /** Read loss streak preference. */
    synchronized boolean lossStreakEnabled(){return option("loss_streak");}
    /** Update loss streak preference. */
    synchronized boolean setLossStreak(boolean v){return setOption("loss_streak",v)&&option("loss_streak")==v;}

    /** Read export stats preference. */
    synchronized boolean exportStatsEnabled(){return option("export_stats");}
    /** Update export stats preference. */
    synchronized boolean setExportStats(boolean v){return setOption("export_stats",v)&&option("export_stats")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class AccessibilitySystem extends FeatureModule {
    private static final String[] DEFAULTS={"high_contrast","large_text","colorblind_palette","reduced_motion","disable_shake","subtitles","screen_reader_labels","keyboard_navigation","hold_to_toggle","sticky_keys","flash_limit","opacity_floor","scale_floor","reduce_particles","focus_outline"};
    AccessibilitySystem(){super("accessibility",DEFAULTS);}
    @Override synchronized String summary(){return "accessibility: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read high contrast preference. */
    synchronized boolean highContrastEnabled(){return option("high_contrast");}
    /** Update high contrast preference. */
    synchronized boolean setHighContrast(boolean v){return setOption("high_contrast",v)&&option("high_contrast")==v;}

    /** Read large text preference. */
    synchronized boolean largeTextEnabled(){return option("large_text");}
    /** Update large text preference. */
    synchronized boolean setLargeText(boolean v){return setOption("large_text",v)&&option("large_text")==v;}

    /** Read colorblind palette preference. */
    synchronized boolean colorblindPaletteEnabled(){return option("colorblind_palette");}
    /** Update colorblind palette preference. */
    synchronized boolean setColorblindPalette(boolean v){return setOption("colorblind_palette",v)&&option("colorblind_palette")==v;}

    /** Read reduced motion preference. */
    synchronized boolean reducedMotionEnabled(){return option("reduced_motion");}
    /** Update reduced motion preference. */
    synchronized boolean setReducedMotion(boolean v){return setOption("reduced_motion",v)&&option("reduced_motion")==v;}

    /** Read disable shake preference. */
    synchronized boolean disableShakeEnabled(){return option("disable_shake");}
    /** Update disable shake preference. */
    synchronized boolean setDisableShake(boolean v){return setOption("disable_shake",v)&&option("disable_shake")==v;}

    /** Read subtitles preference. */
    synchronized boolean subtitlesEnabled(){return option("subtitles");}
    /** Update subtitles preference. */
    synchronized boolean setSubtitles(boolean v){return setOption("subtitles",v)&&option("subtitles")==v;}

    /** Read screen reader labels preference. */
    synchronized boolean screenReaderLabelsEnabled(){return option("screen_reader_labels");}
    /** Update screen reader labels preference. */
    synchronized boolean setScreenReaderLabels(boolean v){return setOption("screen_reader_labels",v)&&option("screen_reader_labels")==v;}

    /** Read keyboard navigation preference. */
    synchronized boolean keyboardNavigationEnabled(){return option("keyboard_navigation");}
    /** Update keyboard navigation preference. */
    synchronized boolean setKeyboardNavigation(boolean v){return setOption("keyboard_navigation",v)&&option("keyboard_navigation")==v;}

    /** Read hold to toggle preference. */
    synchronized boolean holdToToggleEnabled(){return option("hold_to_toggle");}
    /** Update hold to toggle preference. */
    synchronized boolean setHoldToToggle(boolean v){return setOption("hold_to_toggle",v)&&option("hold_to_toggle")==v;}

    /** Read sticky keys preference. */
    synchronized boolean stickyKeysEnabled(){return option("sticky_keys");}
    /** Update sticky keys preference. */
    synchronized boolean setStickyKeys(boolean v){return setOption("sticky_keys",v)&&option("sticky_keys")==v;}

    /** Read flash limit preference. */
    synchronized boolean flashLimitEnabled(){return option("flash_limit");}
    /** Update flash limit preference. */
    synchronized boolean setFlashLimit(boolean v){return setOption("flash_limit",v)&&option("flash_limit")==v;}

    /** Read opacity floor preference. */
    synchronized boolean opacityFloorEnabled(){return option("opacity_floor");}
    /** Update opacity floor preference. */
    synchronized boolean setOpacityFloor(boolean v){return setOption("opacity_floor",v)&&option("opacity_floor")==v;}

    /** Read scale floor preference. */
    synchronized boolean scaleFloorEnabled(){return option("scale_floor");}
    /** Update scale floor preference. */
    synchronized boolean setScaleFloor(boolean v){return setOption("scale_floor",v)&&option("scale_floor")==v;}

    /** Read reduce particles preference. */
    synchronized boolean reduceParticlesEnabled(){return option("reduce_particles");}
    /** Update reduce particles preference. */
    synchronized boolean setReduceParticles(boolean v){return setOption("reduce_particles",v)&&option("reduce_particles")==v;}

    /** Read focus outline preference. */
    synchronized boolean focusOutlineEnabled(){return option("focus_outline");}
    /** Update focus outline preference. */
    synchronized boolean setFocusOutline(boolean v){return setOption("focus_outline",v)&&option("focus_outline")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class LayoutPresetSystem extends FeatureModule {
    private static final String[] DEFAULTS={"classic","compact","wide","streamer","minimal","competitive","accessibility","top_left","top_right","bottom_left","bottom_right","restore_defaults","save_preset","delete_preset","duplicate_preset","active_preset"};
    LayoutPresetSystem(){super("layout_presets",DEFAULTS);}
    @Override synchronized String summary(){return "layout presets: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read classic preference. */
    synchronized boolean classicEnabled(){return option("classic");}
    /** Update classic preference. */
    synchronized boolean setClassic(boolean v){return setOption("classic",v)&&option("classic")==v;}

    /** Read compact preference. */
    synchronized boolean compactEnabled(){return option("compact");}
    /** Update compact preference. */
    synchronized boolean setCompact(boolean v){return setOption("compact",v)&&option("compact")==v;}

    /** Read wide preference. */
    synchronized boolean wideEnabled(){return option("wide");}
    /** Update wide preference. */
    synchronized boolean setWide(boolean v){return setOption("wide",v)&&option("wide")==v;}

    /** Read streamer preference. */
    synchronized boolean streamerEnabled(){return option("streamer");}
    /** Update streamer preference. */
    synchronized boolean setStreamer(boolean v){return setOption("streamer",v)&&option("streamer")==v;}

    /** Read minimal preference. */
    synchronized boolean minimalEnabled(){return option("minimal");}
    /** Update minimal preference. */
    synchronized boolean setMinimal(boolean v){return setOption("minimal",v)&&option("minimal")==v;}

    /** Read competitive preference. */
    synchronized boolean competitiveEnabled(){return option("competitive");}
    /** Update competitive preference. */
    synchronized boolean setCompetitive(boolean v){return setOption("competitive",v)&&option("competitive")==v;}

    /** Read accessibility preference. */
    synchronized boolean accessibilityEnabled(){return option("accessibility");}
    /** Update accessibility preference. */
    synchronized boolean setAccessibility(boolean v){return setOption("accessibility",v)&&option("accessibility")==v;}

    /** Read top left preference. */
    synchronized boolean topLeftEnabled(){return option("top_left");}
    /** Update top left preference. */
    synchronized boolean setTopLeft(boolean v){return setOption("top_left",v)&&option("top_left")==v;}

    /** Read top right preference. */
    synchronized boolean topRightEnabled(){return option("top_right");}
    /** Update top right preference. */
    synchronized boolean setTopRight(boolean v){return setOption("top_right",v)&&option("top_right")==v;}

    /** Read bottom left preference. */
    synchronized boolean bottomLeftEnabled(){return option("bottom_left");}
    /** Update bottom left preference. */
    synchronized boolean setBottomLeft(boolean v){return setOption("bottom_left",v)&&option("bottom_left")==v;}

    /** Read bottom right preference. */
    synchronized boolean bottomRightEnabled(){return option("bottom_right");}
    /** Update bottom right preference. */
    synchronized boolean setBottomRight(boolean v){return setOption("bottom_right",v)&&option("bottom_right")==v;}

    /** Read restore defaults preference. */
    synchronized boolean restoreDefaultsEnabled(){return option("restore_defaults");}
    /** Update restore defaults preference. */
    synchronized boolean setRestoreDefaults(boolean v){return setOption("restore_defaults",v)&&option("restore_defaults")==v;}

    /** Read save preset preference. */
    synchronized boolean savePresetEnabled(){return option("save_preset");}
    /** Update save preset preference. */
    synchronized boolean setSavePreset(boolean v){return setOption("save_preset",v)&&option("save_preset")==v;}

    /** Read delete preset preference. */
    synchronized boolean deletePresetEnabled(){return option("delete_preset");}
    /** Update delete preset preference. */
    synchronized boolean setDeletePreset(boolean v){return setOption("delete_preset",v)&&option("delete_preset")==v;}

    /** Read duplicate preset preference. */
    synchronized boolean duplicatePresetEnabled(){return option("duplicate_preset");}
    /** Update duplicate preset preference. */
    synchronized boolean setDuplicatePreset(boolean v){return setOption("duplicate_preset",v)&&option("duplicate_preset")==v;}

    /** Read active preset preference. */
    synchronized boolean activePresetEnabled(){return option("active_preset");}
    /** Update active preset preference. */
    synchronized boolean setActivePreset(boolean v){return setOption("active_preset",v)&&option("active_preset")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class WarningSystem extends FeatureModule {
    private static final String[] DEFAULTS={"low_health","low_armor","low_durability","low_food","low_totems","low_pearls","low_crystals","shield_broken","enemy_nearby","projectile_incoming","fire","fall_damage","void_edge","negative_effect","inventory_full"};
    WarningSystem(){super("warnings",DEFAULTS);}
    @Override synchronized String summary(){return "warnings: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read low health preference. */
    synchronized boolean lowHealthEnabled(){return option("low_health");}
    /** Update low health preference. */
    synchronized boolean setLowHealth(boolean v){return setOption("low_health",v)&&option("low_health")==v;}

    /** Read low armor preference. */
    synchronized boolean lowArmorEnabled(){return option("low_armor");}
    /** Update low armor preference. */
    synchronized boolean setLowArmor(boolean v){return setOption("low_armor",v)&&option("low_armor")==v;}

    /** Read low durability preference. */
    synchronized boolean lowDurabilityEnabled(){return option("low_durability");}
    /** Update low durability preference. */
    synchronized boolean setLowDurability(boolean v){return setOption("low_durability",v)&&option("low_durability")==v;}

    /** Read low food preference. */
    synchronized boolean lowFoodEnabled(){return option("low_food");}
    /** Update low food preference. */
    synchronized boolean setLowFood(boolean v){return setOption("low_food",v)&&option("low_food")==v;}

    /** Read low totems preference. */
    synchronized boolean lowTotemsEnabled(){return option("low_totems");}
    /** Update low totems preference. */
    synchronized boolean setLowTotems(boolean v){return setOption("low_totems",v)&&option("low_totems")==v;}

    /** Read low pearls preference. */
    synchronized boolean lowPearlsEnabled(){return option("low_pearls");}
    /** Update low pearls preference. */
    synchronized boolean setLowPearls(boolean v){return setOption("low_pearls",v)&&option("low_pearls")==v;}

    /** Read low crystals preference. */
    synchronized boolean lowCrystalsEnabled(){return option("low_crystals");}
    /** Update low crystals preference. */
    synchronized boolean setLowCrystals(boolean v){return setOption("low_crystals",v)&&option("low_crystals")==v;}

    /** Read shield broken preference. */
    synchronized boolean shieldBrokenEnabled(){return option("shield_broken");}
    /** Update shield broken preference. */
    synchronized boolean setShieldBroken(boolean v){return setOption("shield_broken",v)&&option("shield_broken")==v;}

    /** Read enemy nearby preference. */
    synchronized boolean enemyNearbyEnabled(){return option("enemy_nearby");}
    /** Update enemy nearby preference. */
    synchronized boolean setEnemyNearby(boolean v){return setOption("enemy_nearby",v)&&option("enemy_nearby")==v;}

    /** Read projectile incoming preference. */
    synchronized boolean projectileIncomingEnabled(){return option("projectile_incoming");}
    /** Update projectile incoming preference. */
    synchronized boolean setProjectileIncoming(boolean v){return setOption("projectile_incoming",v)&&option("projectile_incoming")==v;}

    /** Read fire preference. */
    synchronized boolean fireEnabled(){return option("fire");}
    /** Update fire preference. */
    synchronized boolean setFire(boolean v){return setOption("fire",v)&&option("fire")==v;}

    /** Read fall damage preference. */
    synchronized boolean fallDamageEnabled(){return option("fall_damage");}
    /** Update fall damage preference. */
    synchronized boolean setFallDamage(boolean v){return setOption("fall_damage",v)&&option("fall_damage")==v;}

    /** Read void edge preference. */
    synchronized boolean voidEdgeEnabled(){return option("void_edge");}
    /** Update void edge preference. */
    synchronized boolean setVoidEdge(boolean v){return setOption("void_edge",v)&&option("void_edge")==v;}

    /** Read negative effect preference. */
    synchronized boolean negativeEffectEnabled(){return option("negative_effect");}
    /** Update negative effect preference. */
    synchronized boolean setNegativeEffect(boolean v){return setOption("negative_effect",v)&&option("negative_effect")==v;}

    /** Read inventory full preference. */
    synchronized boolean inventoryFullEnabled(){return option("inventory_full");}
    /** Update inventory full preference. */
    synchronized boolean setInventoryFull(boolean v){return setOption("inventory_full",v)&&option("inventory_full")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class TrainingSystem extends FeatureModule {
    private static final String[] DEFAULTS={"attack_timing","shield_timing","crit_timing","hotbar_drill","pearl_practice","aim_practice","combo_practice","strafe_practice","inventory_drill","totem_drill","session_timer","accuracy_score","reaction_time","practice_notes","training_history"};
    TrainingSystem(){super("training",DEFAULTS);}
    @Override synchronized String summary(){return "training: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read attack timing preference. */
    synchronized boolean attackTimingEnabled(){return option("attack_timing");}
    /** Update attack timing preference. */
    synchronized boolean setAttackTiming(boolean v){return setOption("attack_timing",v)&&option("attack_timing")==v;}

    /** Read shield timing preference. */
    synchronized boolean shieldTimingEnabled(){return option("shield_timing");}
    /** Update shield timing preference. */
    synchronized boolean setShieldTiming(boolean v){return setOption("shield_timing",v)&&option("shield_timing")==v;}

    /** Read crit timing preference. */
    synchronized boolean critTimingEnabled(){return option("crit_timing");}
    /** Update crit timing preference. */
    synchronized boolean setCritTiming(boolean v){return setOption("crit_timing",v)&&option("crit_timing")==v;}

    /** Read hotbar drill preference. */
    synchronized boolean hotbarDrillEnabled(){return option("hotbar_drill");}
    /** Update hotbar drill preference. */
    synchronized boolean setHotbarDrill(boolean v){return setOption("hotbar_drill",v)&&option("hotbar_drill")==v;}

    /** Read pearl practice preference. */
    synchronized boolean pearlPracticeEnabled(){return option("pearl_practice");}
    /** Update pearl practice preference. */
    synchronized boolean setPearlPractice(boolean v){return setOption("pearl_practice",v)&&option("pearl_practice")==v;}

    /** Read aim practice preference. */
    synchronized boolean aimPracticeEnabled(){return option("aim_practice");}
    /** Update aim practice preference. */
    synchronized boolean setAimPractice(boolean v){return setOption("aim_practice",v)&&option("aim_practice")==v;}

    /** Read combo practice preference. */
    synchronized boolean comboPracticeEnabled(){return option("combo_practice");}
    /** Update combo practice preference. */
    synchronized boolean setComboPractice(boolean v){return setOption("combo_practice",v)&&option("combo_practice")==v;}

    /** Read strafe practice preference. */
    synchronized boolean strafePracticeEnabled(){return option("strafe_practice");}
    /** Update strafe practice preference. */
    synchronized boolean setStrafePractice(boolean v){return setOption("strafe_practice",v)&&option("strafe_practice")==v;}

    /** Read inventory drill preference. */
    synchronized boolean inventoryDrillEnabled(){return option("inventory_drill");}
    /** Update inventory drill preference. */
    synchronized boolean setInventoryDrill(boolean v){return setOption("inventory_drill",v)&&option("inventory_drill")==v;}

    /** Read totem drill preference. */
    synchronized boolean totemDrillEnabled(){return option("totem_drill");}
    /** Update totem drill preference. */
    synchronized boolean setTotemDrill(boolean v){return setOption("totem_drill",v)&&option("totem_drill")==v;}

    /** Read session timer preference. */
    synchronized boolean sessionTimerEnabled(){return option("session_timer");}
    /** Update session timer preference. */
    synchronized boolean setSessionTimer(boolean v){return setOption("session_timer",v)&&option("session_timer")==v;}

    /** Read accuracy score preference. */
    synchronized boolean accuracyScoreEnabled(){return option("accuracy_score");}
    /** Update accuracy score preference. */
    synchronized boolean setAccuracyScore(boolean v){return setOption("accuracy_score",v)&&option("accuracy_score")==v;}

    /** Read reaction time preference. */
    synchronized boolean reactionTimeEnabled(){return option("reaction_time");}
    /** Update reaction time preference. */
    synchronized boolean setReactionTime(boolean v){return setOption("reaction_time",v)&&option("reaction_time")==v;}

    /** Read practice notes preference. */
    synchronized boolean practiceNotesEnabled(){return option("practice_notes");}
    /** Update practice notes preference. */
    synchronized boolean setPracticeNotes(boolean v){return setOption("practice_notes",v)&&option("practice_notes")==v;}

    /** Read training history preference. */
    synchronized boolean trainingHistoryEnabled(){return option("training_history");}
    /** Update training history preference. */
    synchronized boolean setTrainingHistory(boolean v){return setOption("training_history",v)&&option("training_history")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class PrivacySystem extends FeatureModule {
    private static final String[] DEFAULTS={"hide_coordinates","hide_target_names","hide_session_stats","streamer_mode","hide_server_address","hide_chat_overlay","mask_player_names","disable_exports","local_only_data","clear_local_history","private_profile","hide_latency","hide_fps","hide_combat_log","privacy_banner"};
    PrivacySystem(){super("privacy",DEFAULTS);}
    @Override synchronized String summary(){return "privacy: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read hide coordinates preference. */
    synchronized boolean hideCoordinatesEnabled(){return option("hide_coordinates");}
    /** Update hide coordinates preference. */
    synchronized boolean setHideCoordinates(boolean v){return setOption("hide_coordinates",v)&&option("hide_coordinates")==v;}

    /** Read hide target names preference. */
    synchronized boolean hideTargetNamesEnabled(){return option("hide_target_names");}
    /** Update hide target names preference. */
    synchronized boolean setHideTargetNames(boolean v){return setOption("hide_target_names",v)&&option("hide_target_names")==v;}

    /** Read hide session stats preference. */
    synchronized boolean hideSessionStatsEnabled(){return option("hide_session_stats");}
    /** Update hide session stats preference. */
    synchronized boolean setHideSessionStats(boolean v){return setOption("hide_session_stats",v)&&option("hide_session_stats")==v;}

    /** Read streamer mode preference. */
    synchronized boolean streamerModeEnabled(){return option("streamer_mode");}
    /** Update streamer mode preference. */
    synchronized boolean setStreamerMode(boolean v){return setOption("streamer_mode",v)&&option("streamer_mode")==v;}

    /** Read hide server address preference. */
    synchronized boolean hideServerAddressEnabled(){return option("hide_server_address");}
    /** Update hide server address preference. */
    synchronized boolean setHideServerAddress(boolean v){return setOption("hide_server_address",v)&&option("hide_server_address")==v;}

    /** Read hide chat overlay preference. */
    synchronized boolean hideChatOverlayEnabled(){return option("hide_chat_overlay");}
    /** Update hide chat overlay preference. */
    synchronized boolean setHideChatOverlay(boolean v){return setOption("hide_chat_overlay",v)&&option("hide_chat_overlay")==v;}

    /** Read mask player names preference. */
    synchronized boolean maskPlayerNamesEnabled(){return option("mask_player_names");}
    /** Update mask player names preference. */
    synchronized boolean setMaskPlayerNames(boolean v){return setOption("mask_player_names",v)&&option("mask_player_names")==v;}

    /** Read disable exports preference. */
    synchronized boolean disableExportsEnabled(){return option("disable_exports");}
    /** Update disable exports preference. */
    synchronized boolean setDisableExports(boolean v){return setOption("disable_exports",v)&&option("disable_exports")==v;}

    /** Read local only data preference. */
    synchronized boolean localOnlyDataEnabled(){return option("local_only_data");}
    /** Update local only data preference. */
    synchronized boolean setLocalOnlyData(boolean v){return setOption("local_only_data",v)&&option("local_only_data")==v;}

    /** Read clear local history preference. */
    synchronized boolean clearLocalHistoryEnabled(){return option("clear_local_history");}
    /** Update clear local history preference. */
    synchronized boolean setClearLocalHistory(boolean v){return setOption("clear_local_history",v)&&option("clear_local_history")==v;}

    /** Read private profile preference. */
    synchronized boolean privateProfileEnabled(){return option("private_profile");}
    /** Update private profile preference. */
    synchronized boolean setPrivateProfile(boolean v){return setOption("private_profile",v)&&option("private_profile")==v;}

    /** Read hide latency preference. */
    synchronized boolean hideLatencyEnabled(){return option("hide_latency");}
    /** Update hide latency preference. */
    synchronized boolean setHideLatency(boolean v){return setOption("hide_latency",v)&&option("hide_latency")==v;}

    /** Read hide fps preference. */
    synchronized boolean hideFpsEnabled(){return option("hide_fps");}
    /** Update hide fps preference. */
    synchronized boolean setHideFps(boolean v){return setOption("hide_fps",v)&&option("hide_fps")==v;}

    /** Read hide combat log preference. */
    synchronized boolean hideCombatLogEnabled(){return option("hide_combat_log");}
    /** Update hide combat log preference. */
    synchronized boolean setHideCombatLog(boolean v){return setOption("hide_combat_log",v)&&option("hide_combat_log")==v;}

    /** Read privacy banner preference. */
    synchronized boolean privacyBannerEnabled(){return option("privacy_banner");}
    /** Update privacy banner preference. */
    synchronized boolean setPrivacyBanner(boolean v){return setOption("privacy_banner",v)&&option("privacy_banner")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class NotificationSystem extends FeatureModule {
    private static final String[] DEFAULTS={"loadout_changed","low_health","totem_used","shield_broken","weapon_low_durability","pearl_ready","potion_ready","combat_started","combat_ended","friend_joined","friend_left","profile_changed","settings_saved","settings_error","warning_queue"};
    NotificationSystem(){super("notifications",DEFAULTS);}
    @Override synchronized String summary(){return "notifications: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read loadout changed preference. */
    synchronized boolean loadoutChangedEnabled(){return option("loadout_changed");}
    /** Update loadout changed preference. */
    synchronized boolean setLoadoutChanged(boolean v){return setOption("loadout_changed",v)&&option("loadout_changed")==v;}

    /** Read low health preference. */
    synchronized boolean lowHealthEnabled(){return option("low_health");}
    /** Update low health preference. */
    synchronized boolean setLowHealth(boolean v){return setOption("low_health",v)&&option("low_health")==v;}

    /** Read totem used preference. */
    synchronized boolean totemUsedEnabled(){return option("totem_used");}
    /** Update totem used preference. */
    synchronized boolean setTotemUsed(boolean v){return setOption("totem_used",v)&&option("totem_used")==v;}

    /** Read shield broken preference. */
    synchronized boolean shieldBrokenEnabled(){return option("shield_broken");}
    /** Update shield broken preference. */
    synchronized boolean setShieldBroken(boolean v){return setOption("shield_broken",v)&&option("shield_broken")==v;}

    /** Read weapon low durability preference. */
    synchronized boolean weaponLowDurabilityEnabled(){return option("weapon_low_durability");}
    /** Update weapon low durability preference. */
    synchronized boolean setWeaponLowDurability(boolean v){return setOption("weapon_low_durability",v)&&option("weapon_low_durability")==v;}

    /** Read pearl ready preference. */
    synchronized boolean pearlReadyEnabled(){return option("pearl_ready");}
    /** Update pearl ready preference. */
    synchronized boolean setPearlReady(boolean v){return setOption("pearl_ready",v)&&option("pearl_ready")==v;}

    /** Read potion ready preference. */
    synchronized boolean potionReadyEnabled(){return option("potion_ready");}
    /** Update potion ready preference. */
    synchronized boolean setPotionReady(boolean v){return setOption("potion_ready",v)&&option("potion_ready")==v;}

    /** Read combat started preference. */
    synchronized boolean combatStartedEnabled(){return option("combat_started");}
    /** Update combat started preference. */
    synchronized boolean setCombatStarted(boolean v){return setOption("combat_started",v)&&option("combat_started")==v;}

    /** Read combat ended preference. */
    synchronized boolean combatEndedEnabled(){return option("combat_ended");}
    /** Update combat ended preference. */
    synchronized boolean setCombatEnded(boolean v){return setOption("combat_ended",v)&&option("combat_ended")==v;}

    /** Read friend joined preference. */
    synchronized boolean friendJoinedEnabled(){return option("friend_joined");}
    /** Update friend joined preference. */
    synchronized boolean setFriendJoined(boolean v){return setOption("friend_joined",v)&&option("friend_joined")==v;}

    /** Read friend left preference. */
    synchronized boolean friendLeftEnabled(){return option("friend_left");}
    /** Update friend left preference. */
    synchronized boolean setFriendLeft(boolean v){return setOption("friend_left",v)&&option("friend_left")==v;}

    /** Read profile changed preference. */
    synchronized boolean profileChangedEnabled(){return option("profile_changed");}
    /** Update profile changed preference. */
    synchronized boolean setProfileChanged(boolean v){return setOption("profile_changed",v)&&option("profile_changed")==v;}

    /** Read settings saved preference. */
    synchronized boolean settingsSavedEnabled(){return option("settings_saved");}
    /** Update settings saved preference. */
    synchronized boolean setSettingsSaved(boolean v){return setOption("settings_saved",v)&&option("settings_saved")==v;}

    /** Read settings error preference. */
    synchronized boolean settingsErrorEnabled(){return option("settings_error");}
    /** Update settings error preference. */
    synchronized boolean setSettingsError(boolean v){return setOption("settings_error",v)&&option("settings_error")==v;}

    /** Read warning queue preference. */
    synchronized boolean warningQueueEnabled(){return option("warning_queue");}
    /** Update warning queue preference. */
    synchronized boolean setWarningQueue(boolean v){return setOption("warning_queue",v)&&option("warning_queue")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

final class QuickActionSystem extends FeatureModule {
    private static final String[] DEFAULTS={"swap_loadout","toggle_hud","toggle_crosshair","mark_friend","mark_enemy","open_settings","reset_hud","show_stats","clear_log","save_profile","restore_profile","cycle_crosshair","hide_overlays","panic_hide","help"};
    QuickActionSystem(){super("quick_actions",DEFAULTS);}
    @Override synchronized String summary(){return "quick actions: "+enabledOptionCount()+"/"+optionCount()+" options; "+(isEnabled()?"enabled":"disabled");}
    /** Read swap loadout preference. */
    synchronized boolean swapLoadoutEnabled(){return option("swap_loadout");}
    /** Update swap loadout preference. */
    synchronized boolean setSwapLoadout(boolean v){return setOption("swap_loadout",v)&&option("swap_loadout")==v;}

    /** Read toggle hud preference. */
    synchronized boolean toggleHudEnabled(){return option("toggle_hud");}
    /** Update toggle hud preference. */
    synchronized boolean setToggleHud(boolean v){return setOption("toggle_hud",v)&&option("toggle_hud")==v;}

    /** Read toggle crosshair preference. */
    synchronized boolean toggleCrosshairEnabled(){return option("toggle_crosshair");}
    /** Update toggle crosshair preference. */
    synchronized boolean setToggleCrosshair(boolean v){return setOption("toggle_crosshair",v)&&option("toggle_crosshair")==v;}

    /** Read mark friend preference. */
    synchronized boolean markFriendEnabled(){return option("mark_friend");}
    /** Update mark friend preference. */
    synchronized boolean setMarkFriend(boolean v){return setOption("mark_friend",v)&&option("mark_friend")==v;}

    /** Read mark enemy preference. */
    synchronized boolean markEnemyEnabled(){return option("mark_enemy");}
    /** Update mark enemy preference. */
    synchronized boolean setMarkEnemy(boolean v){return setOption("mark_enemy",v)&&option("mark_enemy")==v;}

    /** Read open settings preference. */
    synchronized boolean openSettingsEnabled(){return option("open_settings");}
    /** Update open settings preference. */
    synchronized boolean setOpenSettings(boolean v){return setOption("open_settings",v)&&option("open_settings")==v;}

    /** Read reset hud preference. */
    synchronized boolean resetHudEnabled(){return option("reset_hud");}
    /** Update reset hud preference. */
    synchronized boolean setResetHud(boolean v){return setOption("reset_hud",v)&&option("reset_hud")==v;}

    /** Read show stats preference. */
    synchronized boolean showStatsEnabled(){return option("show_stats");}
    /** Update show stats preference. */
    synchronized boolean setShowStats(boolean v){return setOption("show_stats",v)&&option("show_stats")==v;}

    /** Read clear log preference. */
    synchronized boolean clearLogEnabled(){return option("clear_log");}
    /** Update clear log preference. */
    synchronized boolean setClearLog(boolean v){return setOption("clear_log",v)&&option("clear_log")==v;}

    /** Read save profile preference. */
    synchronized boolean saveProfileEnabled(){return option("save_profile");}
    /** Update save profile preference. */
    synchronized boolean setSaveProfile(boolean v){return setOption("save_profile",v)&&option("save_profile")==v;}

    /** Read restore profile preference. */
    synchronized boolean restoreProfileEnabled(){return option("restore_profile");}
    /** Update restore profile preference. */
    synchronized boolean setRestoreProfile(boolean v){return setOption("restore_profile",v)&&option("restore_profile")==v;}

    /** Read cycle crosshair preference. */
    synchronized boolean cycleCrosshairEnabled(){return option("cycle_crosshair");}
    /** Update cycle crosshair preference. */
    synchronized boolean setCycleCrosshair(boolean v){return setOption("cycle_crosshair",v)&&option("cycle_crosshair")==v;}

    /** Read hide overlays preference. */
    synchronized boolean hideOverlaysEnabled(){return option("hide_overlays");}
    /** Update hide overlays preference. */
    synchronized boolean setHideOverlays(boolean v){return setOption("hide_overlays",v)&&option("hide_overlays")==v;}

    /** Read panic hide preference. */
    synchronized boolean panicHideEnabled(){return option("panic_hide");}
    /** Update panic hide preference. */
    synchronized boolean setPanicHide(boolean v){return setOption("panic_hide",v)&&option("panic_hide")==v;}

    /** Read help preference. */
    synchronized boolean helpEnabled(){return option("help");}
    /** Update help preference. */
    synchronized boolean setHelp(boolean v){return setOption("help",v)&&option("help")==v;}
    synchronized int enabledOptionCount(){return (int)options().values().stream().filter(Boolean::booleanValue).count();}
    synchronized int disabledOptionCount(){return optionCount()-enabledOptionCount();}
    synchronized boolean isReady(){return isEnabled()&&enabledOptionCount()>0;}
    synchronized boolean toggle(String k){boolean v=!option(k);return setOption(k,v)&&option(k)==v;}
    synchronized List<String> enabledOptions(){return options().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();}
    synchronized List<String> disabledOptions(){return options().entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();}
    synchronized boolean hasOption(String k){return options().containsKey(normalize(k));}
    synchronized String optionStatus(String k){return hasOption(k)?(option(k)?"enabled":"disabled"):"unknown";}
    synchronized void enableAll(){for(String k:options().keySet())setOption(k,true);}
    synchronized void disableAll(){for(String k:options().keySet())setOption(k,false);}
    synchronized Map<String,Boolean> enabledSnapshot(){Map<String,Boolean> m=new java.util.LinkedHashMap<>();options().forEach((k,v)->{if(v)m.put(k,v);});return Map.copyOf(m);}
    synchronized String diagnostics(){return id()+"[enabled="+isEnabled()+", active="+enabledOptionCount()+"/"+optionCount()+", revision="+revision()+"]";}
    synchronized boolean accepts(Map<String,Boolean> proposed){if(proposed==null||proposed.size()>DEFAULTS.length)return false;for(var e:proposed.entrySet())if(e.getKey()==null||e.getValue()==null||!hasOption(e.getKey()))return false;return true;}
    synchronized boolean apply(Map<String,Boolean> proposed){if(!accepts(proposed))return false;proposed.forEach(this::setOption);return true;}
    synchronized String exportText(){StringBuilder b=new StringBuilder(id()).append('\n').append("enabled=").append(isEnabled()).append('\n');options().forEach((k,v)->b.append(k).append('=').append(v).append('\n'));return b.toString();}
    synchronized int importText(String input){if(input==null||input.length()>16384)return 0;int n=0;for(String line:input.split("\\R")){int p=line.indexOf('=');if(p<=0)continue;String k=line.substring(0,p).trim(),v=line.substring(p+1).trim();if("enabled".equals(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setEnabled(Boolean.parseBoolean(v));n++;}else if(hasOption(k)&&(v.equalsIgnoreCase("true")||v.equalsIgnoreCase("false"))){setOption(k,Boolean.parseBoolean(v));n++;}}return n;}
}

