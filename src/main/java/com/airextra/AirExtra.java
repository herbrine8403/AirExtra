package com.airextra;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AirExtra implements ModInitializer {
    public static final String MOD_ID = "airextra";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AirExtraConfig config;
    private boolean modEnabled = true;
    private boolean hasShownStartupWarning = false;
    private boolean hasShownRendererWarning = false;
    private boolean hasShownTouchControllerWarning = false;
    private boolean hasShownOrientationWarning = false;
    private boolean hasShownMemoryWarning = false;
    private boolean hasShownLowMemoryWarning = false;
    
    private static final int UDP_PORT = 12450;
    private static final int UDP_TIMEOUT_MS = 10 * 60 * 1000; // 10 minutes
    private static final long LOW_MEMORY_THRESHOLD = 300 * 1024 * 1024; // 300MB
    
    @Override
    public void onInitialize() {
        LOGGER.info("AirExtra mod initializing...");
        
        // Load config
        config = new AirExtraConfig();
        config.load();
        
        // Check if mod should be enabled
        if (!config.isModEnabled()) {
            modEnabled = false;
            LOGGER.info("AirExtra mod is disabled by config");
            return;
        }
        
        // Check if we're on iOS/macOS
        if (!isIOSDevice()) {
            LOGGER.info("Not an iOS/macOS device, disabling AirExtra");
            modEnabled = false;
            return;
        }
        
        // Initialize features
        AirExtraFeatures.initializeFeatures();
        AirExtraFeatures.registerFeatureEvents();
        
        // Start listening for TouchController messages
        startTouchControllerListener();
        
        // Start memory monitoring
        startMemoryMonitoring();
        
        // Register tick event for continuous checks
        ClientTickEvents.START_CLIENT_TICK.register(this::onClientTick);
        
        LOGGER.info("AirExtra mod initialized successfully");
    }
    
    private void onClientTick(MinecraftClient client) {
        if (!modEnabled || hasShownStartupWarning) return;
        
        // Check memory every few ticks
        if (System.currentTimeMillis() % 20000 < 2000) { // Check every 10 seconds
            checkMemoryUsage(client);
        }
        
        // Check renderer every few ticks
        if (System.currentTimeMillis() % 30000 < 3000) { // Check every 15 seconds
            checkRenderer(client);
        }
        
        // Check orientation every few ticks
        if (System.currentTimeMillis() % 15000 < 1500) { // Check every 7.5 seconds
            checkOrientation(client);
        }
        
        // Check memory allocation
        checkMemoryAllocation(client);
        
        hasShownStartupWarning = true;
    }
    
    private boolean isIOSDevice() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        String osVersion = System.getProperty("os.version", "").toLowerCase();
        String osArch = System.getProperty("os.arch", "").toLowerCase();
        
        return (osName.contains("ios") || 
                osName.contains("mac os x") || 
                osName.contains("ipados")) ||
               (osArch.contains("apple") || 
                osName.contains("iphone") || 
                osName.contains("ipad"));
    }
    
    private void startTouchControllerListener() {
        if (!config.isTouchControllerCheckEnabled()) return;
        
        CompletableFuture.runAsync(() -> {
            try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
                socket.setSoTimeout(UDP_TIMEOUT_MS);
                
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                
                LOGGER.info("Starting TouchController listener on UDP port " + UDP_PORT);
                
                while (modEnabled && !socket.isClosed()) {
                    try {
                        socket.receive(packet);
                        
                        String receivedData = new String(packet.getData(), 0, packet.getLength());
                        LOGGER.info("Received message from TouchController: " + receivedData);
                        
                        // Check if TouchController mod is loaded
                        if (!isTouchControllerLoaded()) {
                            if (!hasShownTouchControllerWarning) {
                                showWarning("TouchController未安装或已被禁用");
                                hasShownTouchControllerWarning = true;
                            }
                        }
                        
                        // Stop listening after successful reception
                        break;
                        
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            LOGGER.info("TouchController listener timeout, stopping");
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error starting TouchController listener", e);
            }
        });
    }
    
    private boolean isTouchControllerLoaded() {
        try {
            // Try to access TouchController mod class
            Class.forName("com.touchcontroller.TouchController");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private void checkMemoryUsage(MinecraftClient client) {
        if (!config.isMemoryWarningEnabled()) return;
        
        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long remainingMemory = runtime.maxMemory() - usedMemory;
            
            if (remainingMemory < LOW_MEMORY_THRESHOLD && !hasShownLowMemoryWarning) {
                showWarning("游戏剩余内存不足300MB，请及时保存并关闭不必要的应用");
                hasShownLowMemoryWarning = true;
            }
        } catch (Exception e) {
            LOGGER.error("Error checking memory usage", e);
        }
    }
    
    private void checkRenderer(MinecraftClient client) {
        if (!config.isRendererWarningEnabled()) return;
        
        try {
            String rendererInfo = getClientRendererInfo();
            if (!rendererInfo.contains("mobileglues") && !hasShownRendererWarning) {
                showWarning("检测到您未使用MobileGlues渲染器，可能会导致游戏崩溃，建议切换为MobileGlues渲染器进行游戏。");
                hasShownRendererWarning = true;
            }
        } catch (Exception e) {
            LOGGER.error("Error checking renderer", e);
        }
    }
    
    private String getClientRendererInfo() {
        try {
            // This is a simplified check - in reality, we'd need to access more specific renderer info
            String vendor = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR);
            String renderer = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_RENDERER);
            return (vendor + " " + renderer).toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }
    
    private void checkOrientation(MinecraftClient client) {
        if (!config.isOrientationWarningEnabled()) return;
        
        try {
            if (client.getWindow() != null) {
                int width = client.getWindow().getFramebufferWidth();
                int height = client.getWindow().getFramebufferHeight();
                
                if (height > width && !hasShownOrientationWarning) {
                    showWarning("检测到竖屏模式，建议切换到横屏以获得更好的游戏体验");
                    hasShownOrientationWarning = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error checking orientation", e);
        }
    }
    
    private void checkMemoryAllocation(MinecraftClient client) {
        if (!config.isMemoryAllocationWarningEnabled()) return;
        
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            
            if (maxMemory < 1024 * 1024 * 1024 && !hasShownMemoryWarning) {
                showWarning("游戏分配的内存小于1024MB，建议在启动器设置中增加分配内存");
                hasShownMemoryWarning = true;
            }
        } catch (Exception e) {
            LOGGER.error("Error checking memory allocation", e);
        }
    }
    
    private void showWarning(String message) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                client.player.networkHandler.sendChatCommand("tellraw @s {\"text\":\"" + message + "\",\"color\":\"gold\"}");
            }
        } catch (Exception e) {
            LOGGER.error("Error showing warning", e);
        }
    }
    
    public static AirExtraConfig getConfig() {
        return config;
    }
    
    public boolean isModEnabled() {
        return modEnabled;
    }
}