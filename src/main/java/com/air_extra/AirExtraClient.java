package com.air_extra;

import com.air_extra.config.AirExtraConfig;
import com.air_extra.event.TickListener;
import com.air_extra.feature.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirExtraClient implements ClientModInitializer {
    public static final String MOD_ID = "air_extra";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AirExtraConfig config;
    private static boolean modEnabled = true;
    private static boolean hasCheckedDevice = false;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing AirExtra Client...");
        
        config = AirExtraConfig.load();
        modEnabled = config.isModEnabled();
        
        if (!modEnabled) {
            LOGGER.info("AirExtra is disabled by config");
            return;
        }
        
        LOGGER.info("AirExtra Client initialized");
        
        TickListener.register();
        RendererDetector.register();
        
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            LOGGER.info("Minecraft client started, scheduling device detection...");
            
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    performDeviceDetection(client);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "AirExtra-Device-Detector").start();
        });
    }
    
    private void performDeviceDetection(net.minecraft.client.MinecraftClient client) {
        if (!modEnabled || hasCheckedDevice) return;
        
        hasCheckedDevice = true;
        
        boolean isAppleDevice = DeviceDetector.isAppleDevice();
        boolean isIOSDevice = DeviceDetector.isIOSDevice();
        
        LOGGER.info("Device detection completed: Apple={}, iOS={}", isAppleDevice, isIOSDevice);
        
        startMonitoring(client);
    }
    
    private void startMonitoring(net.minecraft.client.MinecraftClient client) {
        UDPListener.start(client, config);
        MemoryMonitor.start(client, config);
        PerformanceMonitor.startMonitoring(client, config);
        ScreenOrientationDetector.startMonitoring(client, config);
        MemoryMonitor.checkMemoryAllocation(client, config);
        
        LOGGER.info("All monitoring systems started");
    }
    
    public static AirExtraConfig getConfig() {
        return config;
    }
    
    public static boolean isModEnabled() {
        return modEnabled;
    }
}
