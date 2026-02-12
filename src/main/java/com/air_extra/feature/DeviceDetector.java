package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class DeviceDetector {
    
    private static Boolean isAppleDevice = null;
    private static Boolean isIOSDevice = null;
    private static String gpuInfo = "";
    private static String osInfo = "";
    
    public static boolean isAppleDevice() {
        if (isAppleDevice != null) {
            return isAppleDevice;
        }
        
        try {
            String gpuRenderer = getGPURenderer();
            String osName = System.getProperty("os.name", "");
            String osVersion = System.getProperty("os.version", "");
            
            gpuInfo = gpuRenderer;
            osInfo = osName + " " + osVersion;
            
            isAppleDevice = gpuRenderer.toLowerCase().contains("apple") ||
                           osName.toLowerCase().contains("mac") ||
                           osName.toLowerCase().contains("darwin");
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.info("GPU Renderer: {}", gpuRenderer);
                AirExtraClient.LOGGER.info("OS Info: {}", osInfo);
                AirExtraClient.LOGGER.info("Is Apple Device: {}", isAppleDevice);
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Failed to detect Apple device: {}", e.getMessage());
            isAppleDevice = false;
        }
        
        return isAppleDevice;
    }
    
    public static boolean isIOSDevice() {
        if (isIOSDevice != null) {
            return isIOSDevice;
        }
        
        try {
            String osName = System.getProperty("os.name", "");
            String osVersion = System.getProperty("os.version", "");
            String gpuRenderer = getGPURenderer();
            
            String combinedInfo = (osName + " " + osVersion + " " + gpuRenderer).toLowerCase();
            
            isIOSDevice = combinedInfo.contains("ios") ||
                         combinedInfo.contains("ipados") ||
                         combinedInfo.contains("iphone") ||
                         combinedInfo.contains("ipad");
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.info("Is iOS Device: {}", isIOSDevice);
            }
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Failed to detect iOS device: {}", e.getMessage());
            isIOSDevice = false;
        }
        
        return isIOSDevice;
    }
    
    public static String getGPURenderer() {
        try {
            // 检查 OpenGL 上下文是否初始化
            long window = GLFW.glfwGetCurrentContext();
            if (window == 0) {
                AirExtraClient.LOGGER.debug("OpenGL context not initialized yet, skipping GPU detection");
                return "OpenGL context not initialized";
            }
            
            // 检查 OpenGL 是否可用
            boolean glInitialized = false;
            try {
                // 尝试获取 OpenGL 版本来验证上下文
                String glVersion = GL11.glGetString(GL11.GL_VERSION);
                if (glVersion != null) {
                    glInitialized = true;
                }
            } catch (Exception e) {
                AirExtraClient.LOGGER.debug("OpenGL not available yet: {}", e.getMessage());
            }
            
            if (!glInitialized) {
                return "OpenGL not available";
            }
            
            // 安全获取 GPU 信息
            String vendor = "";
            String renderer = "";
            
            try {
                vendor = GL11.glGetString(GL11.GL_VENDOR);
            } catch (Exception e) {
                AirExtraClient.LOGGER.debug("Failed to get GPU vendor: {}", e.getMessage());
            }
            
            try {
                renderer = GL11.glGetString(GL11.GL_RENDERER);
            } catch (Exception e) {
                AirExtraClient.LOGGER.debug("Failed to get GPU renderer: {}", e.getMessage());
            }
            
            String gpuInfo = (vendor != null ? vendor : "") + " " + (renderer != null ? renderer : "").trim();
            
            if (AirExtraClient.getConfig().enableDebugLogging) {
                AirExtraClient.LOGGER.debug("GPU Info: {}", gpuInfo);
            }
            
            return gpuInfo;
            
        } catch (Exception e) {
            AirExtraClient.LOGGER.warn("Failed to detect GPU: {}", e.getMessage());
            return "Unknown GPU (error)";
        }
    }
    
    public static String getGPUInfo() {
        return gpuInfo;
    }
    
    public static String getOSInfo() {
        return osInfo;
    }
    
    public static void refresh() {
        isAppleDevice = null;
        isIOSDevice = null;
        gpuInfo = "";
        osInfo = "";
    }
}
