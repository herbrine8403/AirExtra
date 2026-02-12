package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;

public class MemoryMonitor {
    
    private static int tickCounter = 0;
    private static int memoryCheckInterval = 200;
    
    public static void start(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableMemoryWarning()) return;
        
        AirExtraClient.LOGGER.info("Memory monitor started with threshold: {}MB", config.getMemoryWarningThreshold());
    }
    
    public static void check(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableMemoryWarning()) return;
        
        tickCounter++;
        
        if (tickCounter % memoryCheckInterval == 0) {
            checkMemoryStatus(client, config);
        }
    }
    
    private static void checkMemoryStatus(MinecraftClient client, AirExtraConfig config) {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long availableMemory = maxMemory - usedMemory;
            
            long availableMB = availableMemory / (1024 * 1024);
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.debug("Memory: Max={}MB, Used={}MB, Available={}MB", 
                    maxMemory / (1024 * 1024), usedMemory / (1024 * 1024), availableMB);
            }
            
            if (availableMB < config.getMemoryWarningThreshold()) {
                String warning = String.format(config.memoryWarningText, availableMB);
                ToastHelper.showWarningToast(client, warning);
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Memory check failed: {}", e.getMessage());
        }
    }
    
    public static void checkMemoryAllocation(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableMemoryAllocationCheck()) return;
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long maxMemoryMB = maxMemory / (1024 * 1024);
        
        if (maxMemoryMB < config.getMinMemoryAllocation()) {
            String warning = String.format(config.memoryAllocationWarningText, maxMemoryMB);
            ToastHelper.showWarningToast(client, warning);
        }
    }
}
