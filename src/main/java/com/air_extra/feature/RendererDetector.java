package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;

public class RendererDetector {
    
    private static boolean hasMobileGlues = false;
    private static String rendererInfo = "";
    
    public static void checkRenderer(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableRendererCheck()) return;
        
        try {
            String vendor = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR);
            String renderer = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_RENDERER);
            String version = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VERSION);
            
            rendererInfo = String.format("Vendor: %s, Renderer: %s, Version: %s", 
                vendor, renderer, version);
            
            String combinedInfo = (vendor + " " + renderer + " " + version).toLowerCase();
            hasMobileGlues = combinedInfo.contains("mobileglues");
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.info("Renderer Info: {}", rendererInfo);
                AirExtraClient.LOGGER.info("Has MobileGlues: {}", hasMobileGlues);
            }
            
            if (!hasMobileGlues) {
                ToastHelper.showWarningToast(client, config.rendererWarningText);
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Failed to check renderer: {}", e.getMessage());
        }
    }
    
    public static boolean hasMobileGlues() {
        return hasMobileGlues;
    }
    
    public static String getRendererInfo() {
        return rendererInfo;
    }
}
