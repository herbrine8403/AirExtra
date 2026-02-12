package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import org.lwjgl.glfw.GLFW;

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
            long window = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            if (window != 0) {
                return GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_CONTEXT_VERSION_MAJOR) > 0 
                    ? "GPU: " + GLFW.glfwGetMonitorName(GLFW.glfwGetPrimaryMonitor())
                    : "Unknown GPU";
            }
        } catch (Throwable e) {
            AirExtraClient.LOGGER.debug("Could not get GPU info via GLFW: {}", e.getMessage());
        }
        
        try {
            String vendor = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR);
            String renderer = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_RENDERER);
            return (vendor != null ? vendor : "") + " " + (renderer != null ? renderer : "");
        } catch (Throwable e) {
            AirExtraClient.LOGGER.debug("Could not get GPU info via GL: {}", e.getMessage());
        }
        
        return "Unknown";
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
    }
}
