package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ScreenOrientationDetector {
    
    private static boolean hasChecked = false;  // 本次游戏会话是否已检测过
    
    public static void startMonitoring(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) {
            AirExtraClient.LOGGER.info("Portrait check disabled by config");
            return;
        }
        
        AirExtraClient.LOGGER.info("Screen orientation monitor started");
        hasChecked = false;
    }
    
    public static void checkOrientation(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck() || hasChecked) return;
        
        // 只在有 player 时检测
        if (client.player == null) return;
        
        hasChecked = true;
        
        try {
            long window = client.getWindow().getHandle();
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetWindowSize(window, width, height);
            
            boolean isPortrait = height[0] > width[0];
            
            AirExtraClient.LOGGER.info("Portrait check: {}x{}, isPortrait: {}", width[0], height[0], isPortrait);
            
            if (isPortrait) {
                AirExtraClient.LOGGER.info("Showing portrait warning toast for 5 seconds");
                ToastHelper.showTimedWarningToast(client, config.portraitWarningText, 5);
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Screen orientation check failed: {}", e.getMessage());
        }
    }
    
    public static boolean isPortraitMode(MinecraftClient client) {
        try {
            long window = client.getWindow().getHandle();
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetWindowSize(window, width, height);
            return height[0] > width[0];
        } catch (Exception e) {
            return false;
        }
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
