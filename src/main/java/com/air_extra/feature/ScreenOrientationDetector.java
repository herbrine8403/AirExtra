package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.nio.IntBuffer;

public class ScreenOrientationDetector {
    
    private static Boolean isPortraitMode = null;
    
    public static void checkOrientation(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) return;
        
        try {
            long window = client.getWindow().getHandle();
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetWindowSize(window, width, height);
            
            isPortraitMode = height[0] > width[0];
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.info("Screen Size: {}x{}, Portrait Mode: {}", width[0], height[0], isPortraitMode);
            }
            
            if (isPortraitMode) {
                ToastHelper.showWarningToast(client, config.portraitWarningText);
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Failed to check screen orientation: {}", e.getMessage());
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
