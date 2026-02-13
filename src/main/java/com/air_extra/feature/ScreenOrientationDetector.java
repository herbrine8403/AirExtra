package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ScreenOrientationDetector {
    
    private static Boolean isPortraitMode = null;
    private static boolean hasShownWarning = false;
    private static long lastWarningTime = 0;
    private static final long WARNING_COOLDOWN = 60000;
    private static boolean firstCheck = true;
    
    public static void startMonitoring(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) {
            AirExtraClient.LOGGER.info("Portrait check disabled by config");
            return;
        }
        
        AirExtraClient.LOGGER.info("Screen orientation monitor started");
        
        hasShownWarning = false;
        lastWarningTime = 0;
        firstCheck = true;
        isPortraitMode = null;
        
        checkOrientation(client, config);
    }
    
    public static void checkOrientation(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) return;
        
        try {
            long window = client.getWindow().getHandle();
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetWindowSize(window, width, height);
            
            boolean currentIsPortrait = height[0] > width[0];
            boolean wasPortrait = isPortraitMode != null && isPortraitMode;
            
            AirExtraClient.LOGGER.info("Screen size: {}x{}, Portrait: {}, wasPortrait: {}, firstCheck: {}, player: {}", 
                width[0], height[0], currentIsPortrait, wasPortrait, firstCheck, client.player != null);
            
            // 只在状态变化或首次检测时处理
            boolean stateChanged = (isPortraitMode == null) || (isPortraitMode != currentIsPortrait);
            
            if (stateChanged) {
                AirExtraClient.LOGGER.info("Portrait state changed: {} -> {}", isPortraitMode, currentIsPortrait);
            }
            
            isPortraitMode = currentIsPortrait;
            
            // 竖屏模式下显示警告
            if (isPortraitMode && client.player != null) {
                long currentTime = System.currentTimeMillis();
                
                // 状态变化（从横屏到竖屏）或首次检测到竖屏时重置警告
                if (stateChanged || firstCheck) {
                    hasShownWarning = false;
                    AirExtraClient.LOGGER.info("Reset warning flag due to state change or first check");
                }
                
                // 显示警告
                if (!hasShownWarning || (currentTime - lastWarningTime) > WARNING_COOLDOWN) {
                    AirExtraClient.LOGGER.info("Showing portrait warning toast");
                    ToastHelper.showWarningToast(client, config.portraitWarningText);
                    hasShownWarning = true;
                    lastWarningTime = currentTime;
                }
            } else if (!isPortraitMode && stateChanged) {
                // 切换到横屏时重置警告状态
                hasShownWarning = false;
                AirExtraClient.LOGGER.info("Switched to landscape, reset warning flag");
            }
            
            firstCheck = false;
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Screen orientation check failed: {}", e.getMessage());
        }
    }
    
    public static boolean isPortraitMode() {
        return isPortraitMode != null && isPortraitMode;
    }
    
    public static int[] getScreenSize(MinecraftClient client) {
        try {
            long window = client.getWindow().getHandle();
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetWindowSize(window, width, height);
            return new int[]{width[0], height[0]};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }
}
