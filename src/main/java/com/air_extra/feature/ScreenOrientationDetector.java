package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ScreenOrientationDetector {
    
    private static Boolean isPortraitMode = null;
    
    public static void startMonitoring(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) return;
        
        AirExtraClient.LOGGER.info("Screen orientation monitor started");
        
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
            
            isPortraitMode = height[0] > width[0];
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.debug("Screen size: {}x{}, Portrait: {}", width[0], height[0], isPortraitMode);
            }
            
            if (isPortraitMode && client.player != null) {
                ToastHelper.showWarningToast(client, config.portraitWarningText);
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
