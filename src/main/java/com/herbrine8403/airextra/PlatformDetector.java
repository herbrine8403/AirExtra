package com.herbrine8403.airextra;

import net.fabricmc.loader.api.FabricLoader;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;

import java.util.List;

public class PlatformDetector {
    private static Boolean isApplePlatform = null;
    private static String osName = null;
    private static String gpuModel = null;
    private static String cpuModel = null;
    private static String renderDriver = null;

    public static void detect() {
        osName = System.getProperty("os.name", "").toLowerCase();
        
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            List<GraphicsCard> graphicsCards = systemInfo.getHardware().getGraphicsCards();
            
            cpuModel = processor.getProcessorIdentifier().getName();
            if (!graphicsCards.isEmpty()) {
                gpuModel = graphicsCards.get(0).getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderDriver = System.getProperty("java.awt.graphicsenv", "").toLowerCase();

        isApplePlatform = detectApplePlatform();
    }

    private static boolean detectApplePlatform() {
        if (gpuModel != null && gpuModel.contains("Apple")) {
            return true;
        }
        if (cpuModel != null && cpuModel.contains("Apple")) {
            return true;
        }
        if (osName.contains("ios") || osName.contains("ipados") || osName.contains("mac os x")) {
            return true;
        }
        return false;
    }

    public static boolean isApplePlatform() {
        if (isApplePlatform == null) {
            detect();
        }
        return isApplePlatform;
    }

    public static String getOSName() {
        if (osName == null) {
            detect();
        }
        return osName;
    }

    public static String getGpuModel() {
        if (gpuModel == null) {
            detect();
        }
        return gpuModel;
    }

    public static String getCpuModel() {
        if (cpuModel == null) {
            detect();
        }
        return cpuModel;
    }

    public static String getRenderDriver() {
        if (renderDriver == null) {
            detect();
        }
        return renderDriver;
    }

    public static boolean isUsingMobileGlues() {
        String driver = getRenderDriver();
        return driver != null && driver.contains("mobileglues");
    }

    public static boolean isIOS() {
        String os = getOSName();
        return os.contains("ios") || os.contains("ipados");
    }

    public static boolean isMacOS() {
        String os = getOSName();
        return os.contains("mac os x") || os.contains("macos");
    }
}