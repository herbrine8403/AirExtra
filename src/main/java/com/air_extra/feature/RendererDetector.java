package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
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
        
        AirExtraClient.LOGGER.info("[RendererDetector] Registering renderer detector...");
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (checked) return;
            
            if (client.world != null && client.player != null) {
                checkDelay++;
                
                if (checkDelay >= CHECK_DELAY_TICKS) {
                    AirExtraClient.LOGGER.debug("[RendererDetector] Check delay reached ({} ticks), attempting detection", checkDelay);
                    checkRenderer(client, AirExtraClient.getConfig());
                }
            } else {
                if (checkDelay > 0) {
                    AirExtraClient.LOGGER.debug("[RendererDetector] Waiting for player to join world (current delay: {})", checkDelay);
                }
                checkDelay = 0;
            }
        });
        
        AirExtraClient.LOGGER.info("[RendererDetector] Renderer detector registered successfully");
    }
    
    public static void reset() {
        AirExtraClient.LOGGER.info("[RendererDetector] Resetting detector state");
        checked = false;
        checkDelay = 0;
        hasMobileGlues = false;
        rendererInfo = "";
    }
    
    private static void checkRenderer(MinecraftClient client, AirExtraConfig config) {
        if (checked) {
            AirExtraClient.LOGGER.debug("[RendererDetector] Already checked, skipping");
            return;
        }
        
        if (config == null) {
            AirExtraClient.LOGGER.warn("[RendererDetector] Config is null, skipping check");
            return;
        }
        
        if (!config.isEnableRendererCheck()) {
            AirExtraClient.LOGGER.info("[RendererDetector] Renderer check disabled in config");
            checked = true;
            return;
        }
        
        AirExtraClient.LOGGER.info("[RendererDetector] Starting renderer detection...");
        
        // 检查 OpenGL 上下文是否可用
        long window = GLFW.glfwGetCurrentContext();
        AirExtraClient.LOGGER.debug("[RendererDetector] Current GLFW window context: {}", window);
        
        if (window == 0) {
            AirExtraClient.LOGGER.warn("[RendererDetector] No OpenGL context available (window=0), postponing check");
            // 不标记为已检查，下次再尝试
            return;
        }
        
        checked = true;
        
        try {
            AirExtraClient.LOGGER.debug("[RendererDetector] Querying OpenGL strings...");
            
            String vendor = safeGetString(GL11.GL_VENDOR);
            String renderer = safeGetString(GL11.GL_RENDERER);
            String version = safeGetString(GL11.GL_VERSION);
            
            AirExtraClient.LOGGER.info("[RendererDetector] Raw GL strings - Vendor: '{}', Renderer: '{}', Version: '{}'", 
                vendor, renderer, version);
            
            rendererInfo = String.format("Vendor: %s, Renderer: %s, Version: %s", 
                vendor, renderer, version);
            
            String combinedInfo = (vendor + " " + renderer + " " + version).toLowerCase();
            AirExtraClient.LOGGER.debug("[RendererDetector] Combined info for matching: '{}'", combinedInfo);
            
            hasMobileGlues = combinedInfo.contains("mobileglues");
            
            AirExtraClient.LOGGER.info("[RendererDetector] Detection completed - Has MobileGlues: {}", hasMobileGlues);
            AirExtraClient.LOGGER.info("[RendererDetector] Final Renderer Info: {}", rendererInfo);
            
            if (!hasMobileGlues) {
                AirExtraClient.LOGGER.warn("[RendererDetector] MobileGlues not detected, showing warning toast");
                ToastHelper.showTimedWarningToast(client, config.rendererWarningText, 5);
            } else {
                AirExtraClient.LOGGER.info("[RendererDetector] MobileGlues detected, no warning needed");
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.error("[RendererDetector] Failed to check renderer", e);
            rendererInfo = "Unknown (error: " + e.getMessage() + ")";
        }
    }
    
    private static String safeGetString(int pname) {
        String paramName;
        switch (pname) {
            case GL11.GL_VENDOR:
                paramName = "GL_VENDOR";
                break;
            case GL11.GL_RENDERER:
                paramName = "GL_RENDERER";
                break;
            case GL11.GL_VERSION:
                paramName = "GL_VERSION";
                break;
            default:
                paramName = "UNKNOWN(" + pname + ")";
        }
        
        try {
            String result = GL11.glGetString(pname);
            if (result == null || result.isEmpty()) {
                AirExtraClient.LOGGER.warn("[RendererDetector] GL string for {} returned null or empty", paramName);
                return "Unknown";
            }
            AirExtraClient.LOGGER.debug("[RendererDetector] {} = '{}'", paramName, result);
            return result;
        } catch (Exception e) {
            AirExtraClient.LOGGER.error("[RendererDetector] Failed to get GL string for {}: {}", paramName, e.getMessage());
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
