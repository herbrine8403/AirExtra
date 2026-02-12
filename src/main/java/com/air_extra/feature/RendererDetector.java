package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;

public class RendererDetector {
    
    private static boolean hasMobileGlues = false;
    private static String rendererInfo = "";
    private static boolean checked = false;
    
    public static void checkRenderer(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnableRendererCheck() || checked) return;
        
        checked = true;
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        try {
            String vendor = safeGetString(GL11.GL_VENDOR);
            String renderer = safeGetString(GL11.GL_RENDERER);
            String version = safeGetString(GL11.GL_VERSION);
            
            rendererInfo = String.format("Vendor: %s, Renderer: %s, Version: %s", 
                vendor, renderer, version);
            
            String combinedInfo = (vendor + " " + renderer + " " + version).toLowerCase();
            hasMobileGlues = combinedInfo.contains("mobileglues");
            
            AirExtraClient.LOGGER.info("Renderer detection completed");
            AirExtraClient.LOGGER.info("Renderer Info: {}", rendererInfo);
            AirExtraClient.LOGGER.info("Has MobileGlues: {}", hasMobileGlues);
            
            if (!hasMobileGlues) {
                client.execute(() -> {
                    ToastHelper.showWarningToast(client, config.rendererWarningText);
                });
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Failed to check renderer: {}", e.getMessage());
            rendererInfo = "Unknown (error: " + e.getMessage() + ")";
        }
    }
    
    private static String safeGetString(int pname) {
        try {
            String result = GL11.glGetString(pname);
            return result != null ? result : "Unknown";
        } catch (Exception e) {
            AirExtraClient.LOGGER.debug("Failed to get GL string for {}: {}", pname, e.getMessage());
            return "Unknown";
        }
    }
    
    public static boolean hasMobileGlues() {
        return hasMobileGlues;
    }
    
    public static String getRendererInfo() {
        return rendererInfo;
    }
    
    public static boolean isChecked() {
        return checked;
    }
}
