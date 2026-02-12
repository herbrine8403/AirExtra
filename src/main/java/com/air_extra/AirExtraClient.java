package com.air_extra;

import com.air_extra.config.AirExtraConfig;
import com.air_extra.feature.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirExtraClient implements ClientModInitializer {
    public static final String MOD_ID = "air_extra";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AirExtraConfig config;
    private static boolean isFirstLaunch = false;
    private static boolean modEnabled = true;
    private static boolean hasCheckedDevice = false;
    private static int tickCounter = 0;
    private static int memoryCheckInterval = 200;
    private static UDPListener udpListener;
    
    @Override
    public void onInitializeClient() {
        config = AirExtraConfig.load();
        
        if (!config.hasBeenInitialized()) {
            isFirstLaunch = true;
            checkDeviceAndEnableMod();
            config.setInitialized(true);
            config.save();
        } else {
            modEnabled = config.isModEnabled();
        }
        
        if (!modEnabled) {
            LOGGER.info("AirExtra is disabled by config.");
            return;
        }
        
        LOGGER.info("AirExtra initialized for iOS device optimization!");
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!modEnabled || client.world == null) return;
            
            tickCounter++;
            
            if (tickCounter % memoryCheckInterval == 0) {
                MemoryMonitor.checkMemory(client, config);
            }
            
            if (!hasCheckedDevice) {
                hasCheckedDevice = true;
                performInitialChecks(client);
            }
        });
    }
    
    private void checkDeviceAndEnableMod() {
        boolean isAppleDevice = DeviceDetector.isAppleDevice();
        boolean isIOSDevice = DeviceDetector.isIOSDevice();
        
        if (isAppleDevice || isIOSDevice) {
            modEnabled = true;
            LOGGER.info("Apple/iOS device detected. AirExtra enabled automatically.");
        } else {
            modEnabled = config.isModEnabled();
            LOGGER.info("Non-Apple device detected. AirExtra status: {}", modEnabled);
        }
    }
    
    private void performInitialChecks(net.minecraft.client.MinecraftClient client) {
        if (config.isEnableRendererCheck()) {
            RendererDetector.checkRenderer(client, config);
        }
        
        if (config.isEnablePortraitCheck()) {
            ScreenOrientationDetector.checkOrientation(client, config);
        }
        
        if (config.isEnableMemoryAllocationCheck()) {
            MemoryMonitor.checkMemoryAllocation(client, config);
        }
        
        if (config.isEnableUDPListener()) {
            startUDPListener();
        }
        
        if (config.isEnableTouchOptimization()) {
            TouchOptimizer.initialize(client, config);
        }
        
        if (config.isEnablePerformanceMonitor()) {
            PerformanceMonitor.startMonitoring(client, config);
        }
        
        if (config.isEnableMemoryWarning()) {
            MemoryMonitor.startMemoryWarning(client, config);
        }
    }
    
    private void startUDPListener() {
        udpListener = new UDPListener(config);
        udpListener.start();
    }
    
    public static AirExtraConfig getConfig() {
        return config;
    }
    
    public static boolean isModEnabled() {
        return modEnabled;
    }
    
    public static void setModEnabled(boolean enabled) {
        modEnabled = enabled;
        config.setModEnabled(enabled);
        config.save();
    }
    
    public static void resetTickCounter() {
        tickCounter = 0;
    }
}
