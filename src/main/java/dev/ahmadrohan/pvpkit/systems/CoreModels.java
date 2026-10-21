package dev.ahmadrohan.pvpkit.systems;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** Dependency-free value objects and small managers shared by PvP-kit systems. */
final class CoreModels { private CoreModels() {} }
record CombatEvent(EventType type, Instant timestamp, String detail, double amount) {
 enum EventType { HIT,MISS,CRITICAL,SHIELD_BLOCK,SHIELD_BREAK,TOTEM_POP,HEAL,LOADOUT_SWAP,PEARL_THROW,POTION_USED,WARNING,SESSION_START,SESSION_END }
 CombatEvent { Objects.requireNonNull(type);timestamp=timestamp==null?Instant.now():timestamp;detail=detail==null?"":detail;if(!Double.isFinite(amount)||amount<0)amount=0; }
 static CombatEvent of(EventType type,String detail){return new CombatEvent(type,Instant.now(),detail,0);}
}
final class CombatHistory {
 private final Deque<CombatEvent> events=new ArrayDeque<>();private int limit;
 CombatHistory(int limit){setLimit(limit);}synchronized void setLimit(int n){if(n<10||n>10000)throw new IllegalArgumentException("limit must be 10..10000");limit=n;while(events.size()>limit)events.removeFirst();}
 synchronized void add(CombatEvent e){events.addLast(Objects.requireNonNull(e));while(events.size()>limit)events.removeFirst();}synchronized List<CombatEvent> recent(int n){List<CombatEvent>a=new ArrayList<>(events);int k=Math.max(0,Math.min(n,a.size()));return List.copyOf(a.subList(a.size()-k,a.size()));}synchronized int size(){return events.size();}synchronized void clear(){events.clear();}
}
final class CooldownBook {
 private final Map<String,Long> readyAt=new LinkedHashMap<>();
 synchronized void start(String id,long duration,TimeUnit unit){if(id==null||id.isBlank()||duration<0||unit==null)throw new IllegalArgumentException("invalid cooldown");readyAt.put(id,System.nanoTime()+Math.min(unit.toNanos(duration),TimeUnit.DAYS.toNanos(1)));}
 synchronized long remainingNanos(String id,long now){return Math.max(0,readyAt.getOrDefault(id,0L)-now);}synchronized double remainingSeconds(String id){return remainingNanos(id,System.nanoTime())/1e9;}synchronized boolean isReady(String id){return remainingNanos(id,System.nanoTime())==0;}synchronized void clear(String id){readyAt.remove(id);}synchronized void clearAll(){readyAt.clear();}synchronized Map<String,Long> snapshot(){return Map.copyOf(readyAt);}
}
final class CombatMetrics {
 private long hits,misses,crits,blocks,breaks,pops,started=System.nanoTime();private double dealt,taken,healing;
 synchronized void hit(double n,boolean critical){hits++;dealt+=safe(n);if(critical)crits++;}synchronized void miss(){misses++;}synchronized void shieldBlock(){blocks++;}synchronized void shieldBreak(){breaks++;}synchronized void totemPop(){pops++;}synchronized void tookDamage(double n){taken+=safe(n);}synchronized void healed(double n){healing+=safe(n);}
 synchronized long hits(){return hits;}synchronized long misses(){return misses;}synchronized long crits(){return crits;}synchronized long totemPops(){return pops;}synchronized double dealt(){return dealt;}synchronized double taken(){return taken;}synchronized double healing(){return healing;}synchronized double accuracy(){return hits+misses==0?0:(double)hits/(hits+misses);}synchronized long elapsedMillis(){return(System.nanoTime()-started)/1_000_000;}
 synchronized void reset(){hits=misses=crits=blocks=breaks=pops=0;dealt=taken=healing=0;started=System.nanoTime();}private static double safe(double n){return Double.isFinite(n)&&n>0?n:0;}
}
final class Loadout {
 private final String name;private final Map<Integer,String> slots=new LinkedHashMap<>();private long updated;
 Loadout(String name){if(name==null||name.isBlank())throw new IllegalArgumentException("name required");this.name=name.trim();}String name(){return name;}
 synchronized void assign(int slot,String purpose){if(slot<0||slot>8)throw new IllegalArgumentException("slot must be 0..8");if(purpose==null||purpose.isBlank())slots.remove(slot);else slots.put(slot,purpose.trim().toLowerCase(Locale.ROOT));updated=System.nanoTime();}
 synchronized String purposeAt(int slot){return slots.getOrDefault(slot,"unassigned");}synchronized Map<Integer,String> slots(){return Map.copyOf(slots);}synchronized long updatedAt(){return updated;}synchronized Loadout copy(String newName){Loadout out=new Loadout(newName);slots.forEach(out::assign);return out;}
}
final class AnimationTimeline {
 enum Curve{LINEAR,EASE_IN,EASE_OUT,EASE_IN_OUT,PULSE,OVERSHOOT}record Track(String id,double from,double to,long start,long duration,Curve curve,boolean loop){}
 private final List<Track> tracks=new ArrayList<>();
 synchronized void start(String id,double from,double to,long millis,Curve curve,boolean loop){if(id==null||id.isBlank()||millis<1||millis>120000||curve==null)throw new IllegalArgumentException("invalid track");tracks.removeIf(t->t.id().equals(id));tracks.add(new Track(id,from,to,System.nanoTime(),millis*1_000_000L,curve,loop));}
 synchronized double value(String id,long now){for(Track t:tracks)if(t.id().equals(id)){double x=(double)(now-t.start())/t.duration();x=t.loop()?x-Math.floor(x):Math.max(0,Math.min(1,x));return t.from()+(t.to()-t.from())*ease(t.curve(),x);}return 0;}
 synchronized void prune(long now){Iterator<Track>i=tracks.iterator();while(i.hasNext()){Track t=i.next();if(!t.loop()&&now-t.start()>=t.duration())i.remove();}}synchronized void clear(){tracks.clear();}synchronized int activeCount(){return tracks.size();}
 private double ease(Curve c,double x){return switch(c){case LINEAR->x;case EASE_IN->x*x;case EASE_OUT->1-(1-x)*(1-x);case EASE_IN_OUT->x<.5?2*x*x:1-Math.pow(-2*x+2,2)/2;case PULSE->.5-.5*Math.cos(x*Math.PI*2);case OVERSHOOT->{double k=1.70158;yield 1+(k+1)*Math.pow(x-1,3)+k*Math.pow(x-1,2);}};}
}
final class SettingsStore {
 private final Map<String,Object> values=new LinkedHashMap<>();private long revision;
 synchronized void putBoolean(String k,boolean v){put(k,v);}synchronized void putDouble(String k,double v,double min,double max){if(!Double.isFinite(v)||v<min||v>max)throw new IllegalArgumentException("out of range");put(k,v);}synchronized void putString(String k,String v,int max){Objects.requireNonNull(v);if(v.length()>max)throw new IllegalArgumentException("too long");put(k,v);}
 synchronized boolean getBoolean(String k,boolean d){Object v=values.get(k);return v instanceof Boolean b?b:d;}synchronized double getDouble(String k,double d){Object v=values.get(k);return v instanceof Number n?n.doubleValue():d;}synchronized String getString(String k,String d){Object v=values.get(k);return v instanceof String s?s:d;}synchronized long revision(){return revision;}synchronized Map<String,Object> snapshot(){return Map.copyOf(values);}synchronized void clear(){values.clear();revision++;}
 private void put(String k,Object v){String key=Objects.requireNonNull(k).trim();if(key.isEmpty())throw new IllegalArgumentException("empty key");Object old=values.put(key,v);if(!Objects.equals(old,v))revision++;}
}
final class HudWidgetState {
 private final String id;private double x,y,scale=1,opacity=1;private boolean visible=true,pinned;
 HudWidgetState(String id,double x,double y){if(id==null||id.isBlank())throw new IllegalArgumentException("id required");this.id=id;move(x,y);}synchronized void move(double x,double y){this.x=clamp(x,-1,1);this.y=clamp(y,-1,1);}synchronized void scale(double n){scale=clamp(n,.5,2.5);}synchronized void opacity(double n){opacity=clamp(n,.1,1);}synchronized void visible(boolean v){visible=v;}synchronized void pinned(boolean v){pinned=v;}String id(){return id;}synchronized double x(){return x;}synchronized double y(){return y;}synchronized double scale(){return scale;}synchronized double opacity(){return opacity;}synchronized boolean visible(){return visible;}synchronized boolean pinned(){return pinned;}private static double clamp(double n,double a,double b){return Double.isFinite(n)?Math.max(a,Math.min(b,n)):a;}
}
final class ColorPalette {
 static final int WHITE=0xFFFFFFFF,BLACK=0xFF000000,FRIEND=0xFF49D17D,ENEMY=0xFFFF5B67,WARNING=0xFFFFC247,INFO=0xFF66BFFF,CRITICAL=0xFFFF3D88,SHIELD=0xFF79A9FF;private ColorPalette(){}
 static int withAlpha(int c,double o){int a=(int)Math.round(Math.max(0,Math.min(1,o))*(c>>>24&255));return(c&0xFFFFFF)|(a<<24);}static int blend(int a,int b,double t){t=Math.max(0,Math.min(1,t));return(mix(a>>>24&255,b>>>24&255,t)<<24)|(mix(a>>>16&255,b>>>16&255,t)<<16)|(mix(a>>>8&255,b>>>8&255,t)<<8)|mix(a&255,b&255,t);}private static int mix(int a,int b,double t){return(int)Math.round(a+(b-a)*t);}
}
