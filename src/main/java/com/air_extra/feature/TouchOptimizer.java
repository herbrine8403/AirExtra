package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class TouchOptimizer {
    
    private static boolean optimized = false;
    
    public static void initialize(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableTouchOptimization()) return;
        
        if (DeviceDetector.isIOSDevice() || DeviceDetector.isAppleDevice()) {
            applyTouchOptimizations(client, config);
        }
    }
    
    private static void applyTouchOptimizations(MinecraftClient client, AirExtraConfig config) {
        if (optimized) return;
        
        try {
            GameOptions options = client.options;
            
            if (config.enableChunkOptimization) {
                options.getSimulationDistance().setValue(Math.min(options.getSimulationDistance().getValue(), 6));
            }
            
            if (config.enableRenderDistanceOptimization) {
                int currentRenderDistance = options.getViewDistance().getValue();
                if (currentRenderDistance > config.maxRenderDistance) {
                    options.getViewDistance().setValue(config.maxRenderDistance);
                }
            }
            
            options.getCloudRenderMode().setValue(net.minecraft.client.option.CloudRenderMode.OFF);
            options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.MINIMAL);
            options.getFpsLimit().setValue(config.targetFPS);
            
            optimized = true;
            
            AirExtraClient.LOGGER.info("Touch optimizations applied for iOS device");
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.info("Render Distance: {}, Simulation Distance: {}", 
                    options.getViewDistance().getValue(), options.getSimulationDistance().getValue());
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Failed to apply touch optimizations: {}", e.getMessage());
        }
    }
    
    public static boolean isOptimized() {
        return optimized;
    }
    
    public static void reset() {
        optimized = false;
    }
}
