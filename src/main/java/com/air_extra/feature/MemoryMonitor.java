package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MemoryMonitor {
    
    private static long lastWarningTime = 0;
    private static final long WARNING_COOLDOWN = 60000;
    private static boolean memoryWarningActive = false;
    
    public static void checkMemory(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableMemoryWarning()) return;
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long availableMemory = maxMemory - usedMemory;
        
        long availableMB = availableMemory / (1024 * 1024);
        
        if (AirExtraClient.getConfig().enableDebugLogging) {
            AirExtraClient.LOGGER.debug("Memory Status - Max: {}MB, Used: {}MB, Available: {}MB", 
                maxMemory / (1024 * 1024), usedMemory / (1024 * 1024), availableMB);
        }
        
        if (availableMB < config.getMemoryWarningThreshold()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastWarningTime > WARNING_COOLDOWN) {
                String warningText = String.format(config.memoryWarningText, availableMB);
                showWarning(client, warningText);
                lastWarningTime = currentTime;
                memoryWarningActive = true;
            }
        } else {
            memoryWarningActive = false;
        }
    }
    
    public static void checkMemoryAllocation(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableMemoryAllocationCheck()) return;
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long maxMemoryMB = maxMemory / (1024 * 1024);
        
        if (maxMemoryMB < config.getMinMemoryAllocation()) {
            String warningText = String.format(config.memoryAllocationWarningText, maxMemoryMB);
            showWarning(client, warningText);
        }
        
        if (AirExtraClient.getConfig().enableDebugLogging) {
            AirExtraClient.LOGGER.info("Allocated Memory: {}MB (Min required: {}MB)", 
                maxMemoryMB, config.getMinMemoryAllocation());
        }
    }
    
    public static void startMemoryWarning(MinecraftClient client, AirExtraConfig config) {
        AirExtraClient.LOGGER.info("Memory monitoring started. Warning threshold: {}MB", 
            config.getMemoryWarningThreshold());
    }
    
    public static boolean isMemoryWarningActive() {
        return memoryWarningActive;
    }
    
    public static long getAvailableMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return (maxMemory - usedMemory) / (1024 * 1024);
    }
    
    public static long getTotalMemoryMB() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }
    
    private static void showWarning(MinecraftClient client, String message) {
        if (client.player != null) {
            client.execute(() -> {
                client.inGameHud.getChatHud().addMessage(Text.literal(message));
                showOverlayWarning(client, message);
            });
        }
    }
    
    private static void showOverlayWarning(MinecraftClient client, String message) {
        ToastHelper.showWarningToast(client, message);
    }
}
