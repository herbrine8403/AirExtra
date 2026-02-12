package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

import java.util.concurrent.atomic.AtomicBoolean;

public class PerformanceMonitor {
    
    private static final AtomicBoolean monitoring = new AtomicBoolean(false);
    private static Thread monitorThread;
    private static int currentFPS = 0;
    private static int lowFPSCount = 0;
    private static final int LOW_FPS_THRESHOLD = 3;
    
    public static void startMonitoring(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePerformanceMonitor() || monitoring.get()) return;
        
        monitoring.set(true);
        
        monitorThread = new Thread(() -> {
            while (monitoring.get()) {
                try {
                    Thread.sleep(5000);
                    
                    if (client.player != null && client.world != null) {
                        currentFPS = client.getCurrentFps();
                        
                        if (AirExtraClient.getConfig().enableDebugLogging) {
                            AirExtraClient.LOGGER.debug("Current FPS: {}", currentFPS);
                        }
                        
                        if (currentFPS < 20 && currentFPS > 0) {
                            lowFPSCount++;
                            
                            if (lowFPSCount >= LOW_FPS_THRESHOLD) {
                                applyPerformanceOptimizations(client, config);
                                lowFPSCount = 0;
                            }
                        } else {
                            lowFPSCount = Math.max(0, lowFPSCount - 1);
                        }
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "AirExtra-Performance-Monitor");
        
        monitorThread.setDaemon(true);
        monitorThread.start();
        
        AirExtraClient.LOGGER.info("Performance monitoring started");
    }
    
    private static void applyPerformanceOptimizations(MinecraftClient client, AirExtraConfig config) {
        if (!config.enableAutoFPSLimit) return;
        
        client.execute(() -> {
            try {
                GameOptions options = client.options;
                
                int currentRenderDistance = options.getViewDistance().getValue();
                if (currentRenderDistance > 4) {
                    options.getViewDistance().setValue(currentRenderDistance - 2);
                }
                
                options.getCloudRenderMode().setValue(net.minecraft.client.option.CloudRenderMode.OFF);
                options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.MINIMAL);
                
                String message = String.format(config.lowFPSText, currentFPS);
                ToastHelper.showInfoToast(client, "AirExtra", message.replace("§e", ""));
                
                AirExtraClient.LOGGER.info("Applied performance optimizations due to low FPS");
                
            } catch (Exception e) {
                AirExtraClient.LOGGER.warn("Failed to apply performance optimizations: {}", e.getMessage());
            }
        });
    }
    
    public static void stopMonitoring() {
        monitoring.set(false);
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        AirExtraClient.LOGGER.info("Performance monitoring stopped");
    }
    
    public static int getCurrentFPS() {
        return currentFPS;
    }
    
    public static boolean isMonitoring() {
        return monitoring.get();
    }
}
