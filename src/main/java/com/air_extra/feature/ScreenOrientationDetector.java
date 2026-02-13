package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ScreenOrientationDetector {
    
    private static Boolean isPortraitMode = null;
    private static boolean hasShownWarning = false;  // 防止重复显示警告
    private static long lastWarningTime = 0;  // 上次警告时间
    private static final long WARNING_COOLDOWN = 60000;  // 警告冷却时间 (60秒)
    
    public static void startMonitoring(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) return;
        
        AirExtraClient.LOGGER.info("Screen orientation monitor started");
        
        // 重置状态
        hasShownWarning = false;
        lastWarningTime = 0;
        
        // 立即检查一次
        checkOrientation(client, config);
    }
    
    public static void checkOrientation(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) return;
        
        try {
            long window = client.getWindow().getHandle();
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetWindowSize(window, width, height);
            
            boolean wasPortrait = isPortraitMode != null && isPortraitMode;
            isPortraitMode = height[0] > width[0];
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.debug("Screen size: {}x{}, Portrait: {}", width[0], height[0], isPortraitMode);
            }
            
            // 检测到从横屏切换到竖屏，或首次检测到竖屏
            if (isPortraitMode && client.player != null) {
                long currentTime = System.currentTimeMillis();
                
                // 如果从横屏切换到竖屏，重置警告状态
                if (!wasPortrait) {
                    hasShownWarning = false;
                }
                
                // 检查是否可以显示警告（未显示过或已过冷却时间）
                if (!hasShownWarning || (currentTime - lastWarningTime) > WARNING_COOLDOWN) {
                    ToastHelper.showWarningToast(client, config.portraitWarningText);
                    hasShownWarning = true;
                    lastWarningTime = currentTime;
                }
            } else if (!isPortraitMode) {
                // 切换到横屏时重置警告状态
                hasShownWarning = false;
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
