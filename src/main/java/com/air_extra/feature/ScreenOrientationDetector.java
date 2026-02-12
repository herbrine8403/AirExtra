package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ScreenOrientationDetector {
    
    private static Boolean isPortraitMode = null;
    
    public static void checkOrientation(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePortraitCheck()) return;
        
        try {
            long window = client.getWindow().getHandle();
            int width = GLFW.glfwGetWindowSize(window)[0];
            int height = GLFW.glfwGetWindowSize(window)[1];
            
            isPortraitMode = height > width;
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.info("Screen Size: {}x{}, Portrait Mode: {}", width, height, isPortraitMode);
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
            int[] size = GLFW.glfwGetWindowSize(window);
            return new int[]{size[0], size[1]};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }
}
