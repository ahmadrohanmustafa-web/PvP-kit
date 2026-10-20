package dev.ahmadrohan.pvpkit;
import dev.ahmadrohan.pvpkit.systems.FeatureRegistry;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/** Fabric entrypoint; feature logic stays independent from version-specific render APIs. */
public final class PvpKitClient implements ClientModInitializer {
 public static final String MOD_ID="pvpkit"; public static final Logger LOGGER=LoggerFactory.getLogger(MOD_ID); private static FeatureRegistry registry;
 @Override public void onInitializeClient(){registry=new FeatureRegistry();registry.initializeDefaults();LOGGER.info("PvP-kit initialized: {} feature systems. Client-side only.",registry.systemCount());}
 public static FeatureRegistry registry(){if(registry==null)throw new IllegalStateException("PvP-kit has not initialized yet");return registry;}
}
