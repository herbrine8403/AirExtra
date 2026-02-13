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
    private static boolean hadPlayer = false;  // 跟踪之前是否有 player
    
    public static void startMonitoring(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) {
            AirExtraClient.LOGGER.info("Portrait check disabled by config");
            return;
        }
        
        AirExtraClient.LOGGER.info("Screen orientation monitor started");
        
        hasShownWarning = false;
        lastWarningTime = 0;
        hadPlayer = false;
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
            boolean hasPlayer = client.player != null;
            
            AirExtraClient.LOGGER.info("Screen size: {}x{}, Portrait: {}, wasPortrait: {}, hasPlayer: {}, hadPlayer: {}, hasShownWarning: {}", 
                width[0], height[0], currentIsPortrait, wasPortrait, hasPlayer, hadPlayer, hasShownWarning);
            
            // 状态变化检测
            boolean orientationChanged = (isPortraitMode == null) || (isPortraitMode != currentIsPortrait);
            boolean playerJustJoined = hasPlayer && !hadPlayer;  // player 刚进入世界
            
            if (orientationChanged) {
                AirExtraClient.LOGGER.info("Portrait state changed: {} -> {}", isPortraitMode, currentIsPortrait);
            }
            
            if (playerJustJoined) {
                AirExtraClient.LOGGER.info("Player just joined the world");
            }
            
            isPortraitMode = currentIsPortrait;
            hadPlayer = hasPlayer;
            
            // 竖屏模式下显示警告
            if (isPortraitMode && hasPlayer) {
                long currentTime = System.currentTimeMillis();
                
                // 方向变化、player 刚进入世界时重置警告
                if (orientationChanged || playerJustJoined) {
                    hasShownWarning = false;
                    AirExtraClient.LOGGER.info("Reset warning flag (orientationChanged={}, playerJustJoined={})", 
                        orientationChanged, playerJustJoined);
                }
                
                // 显示警告
                if (!hasShownWarning || (currentTime - lastWarningTime) > WARNING_COOLDOWN) {
                    AirExtraClient.LOGGER.info("Showing portrait warning toast");
                    ToastHelper.showWarningToast(client, config.portraitWarningText);
                    hasShownWarning = true;
                    lastWarningTime = currentTime;
                }
            } else if (!isPortraitMode && orientationChanged) {
                // 切换到横屏时重置警告状态
                hasShownWarning = false;
                AirExtraClient.LOGGER.info("Switched to landscape, reset warning flag");
            }
            
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
