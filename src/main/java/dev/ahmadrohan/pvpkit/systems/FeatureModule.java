package dev.ahmadrohan.pvpkit.systems;
import java.util.Collections;import java.util.LinkedHashMap;import java.util.Locale;import java.util.Map;import java.util.Objects;
/** Shared validated option state; intentionally has no Minecraft dependency. */
public abstract class FeatureModule {
 private final String id;private final Map<String,Boolean> options=new LinkedHashMap<>();private boolean enabled=true;private long revision,lastChangedAt;
 protected FeatureModule(String id,String[] defaults){this.id=Objects.requireNonNull(id);for(String k:defaults)options.put(k,true);}
 public final String id(){return id;}public final synchronized boolean isEnabled(){return enabled;}public final synchronized long revision(){return revision;}public final synchronized long lastChangedAt(){return lastChangedAt;}public final synchronized int optionCount(){return options.size();}
 public final synchronized void setEnabled(boolean v){if(enabled!=v){enabled=v;changed();}}public final synchronized boolean option(String k){return options.getOrDefault(normalize(k),false);}
 public final synchronized boolean setOption(String k,boolean v){String key=normalize(k);if(!options.containsKey(key))return false;if(options.get(key)!=v){options.put(key,v);changed();}return true;}
 public final synchronized Map<String,Boolean> options(){return Collections.unmodifiableMap(new LinkedHashMap<>(options));}public final synchronized void resetToDefaults(){enabled=true;options.replaceAll((k,v)->true);changed();}
 protected final synchronized void changed(){revision++;lastChangedAt=System.nanoTime();}protected final String normalize(String s){return Objects.requireNonNull(s,"option").trim().toLowerCase(Locale.ROOT);}public abstract String summary();
}
