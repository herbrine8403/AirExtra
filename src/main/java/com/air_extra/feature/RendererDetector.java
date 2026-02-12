package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;

public class RendererDetector {
    
    private static boolean hasMobileGlues = false;
    private static String rendererInfo = "";
    private static boolean checked = false;
    private static boolean registered = false;
    private static int checkDelay = 0;
    private static final int CHECK_DELAY_TICKS = 100;
    
    public static void register() {
        if (registered) return;
        registered = true;
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (checked) return;
            
            if (client.world != null && client.player != null) {
                checkDelay++;
                
                if (checkDelay >= CHECK_DELAY_TICKS) {
                    checkRenderer(client, AirExtraClient.getConfig());
                }
            } else {
                checkDelay = 0;
            }
        });
        
        AirExtraClient.LOGGER.info("Renderer detector registered");
    }
    
    public static void reset() {
        checked = false;
        checkDelay = 0;
        hasMobileGlues = false;
        rendererInfo = "";
    }
    
    private static void checkRenderer(MinecraftClient client, AirExtraConfig config) {
        if (checked || config == null || !config.isEnableRendererCheck()) return;
        
        checked = true;
        
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
                ToastHelper.showTimedWarningToast(client, config.rendererWarningText, 5);
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
