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
        
        // 加载配置
        config = AirExtraConfig.load();
        
        // 检查是否启用
        modEnabled = config.isModEnabled();
        
        if (!modEnabled) {
            LOGGER.info("AirExtra is disabled by config");
            return;
        }
        
        LOGGER.info("AirExtra Client initialized");
        
        // 注册事件监听器
        registerEventListeners();
        
        // 注册生命周期事件
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            LOGGER.info("Minecraft client started, scheduling device detection...");
            
            // 延迟设备检测，确保游戏完全启动
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // 延迟 2 秒
                    performDeviceDetection(client);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "AirExtra-Device-Detector").start();
        });
    }
    
    private void registerEventListeners() {
        // 注册 tick 监听器
        TickListener.register();
    }
    
    private void performDeviceDetection(net.minecraft.client.MinecraftClient client) {
        if (!modEnabled || hasCheckedDevice) return;
        
        hasCheckedDevice = true;
        
        // 检查设备
        boolean isAppleDevice = DeviceDetector.isAppleDevice();
        boolean isIOSDevice = DeviceDetector.isIOSDevice();
        
        LOGGER.info("Device detection completed: Apple={}, iOS={}", isAppleDevice, isIOSDevice);
        
        // 启动监控
        startMonitoring(client);
    }
    
    private void startMonitoring(net.minecraft.client.MinecraftClient client) {
        // 启动 UDP 监听
        UDPListener.start(client, config);
        
        // 启动内存监控
        MemoryMonitor.start(client, config);
        
        // 启动性能监控
        PerformanceMonitor.startMonitoring(client, config);
        
        // 启动屏幕方向监控
        ScreenOrientationDetector.startMonitoring(client, config);
        
        // 检查内存分配
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
