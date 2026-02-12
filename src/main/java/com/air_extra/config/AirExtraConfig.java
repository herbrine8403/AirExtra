package com.air_extra.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

@Config(name = "air_extra")
public class AirExtraConfig implements ConfigData {
    
    @ConfigEntry.Category("general")
    public boolean modEnabled = true;
    
    @ConfigEntry.Category("general")
    public boolean initialized = false;
    
    @ConfigEntry.Category("memory")
    public boolean enableMemoryWarning = true;
    
    @ConfigEntry.Category("memory")
    @ConfigEntry.BoundedDiscrete(min = 100, max = 1000)
    public int memoryWarningThreshold = 300;
    
    @ConfigEntry.Category("memory")
    public String memoryWarningText = "§c警告：游戏剩余内存不足 %dMB，可能导致卡顿或崩溃！";
    
    @ConfigEntry.Category("memory")
    public boolean enableMemoryAllocationCheck = true;
    
    @ConfigEntry.Category("memory")
    @ConfigEntry.BoundedDiscrete(min = 512, max = 4096)
    public int minMemoryAllocation = 1024;
    
    @ConfigEntry.Category("memory")
    public String memoryAllocationWarningText = "§c警告：游戏分配内存不足 %dMB，建议增加内存分配！";
    
    @ConfigEntry.Category("renderer")
    public boolean enableRendererCheck = true;
    
    @ConfigEntry.Category("renderer")
    public String rendererWarningText = "§c检测到您未使用MobileGlues渲染器，可能会导致游戏崩溃，建议切换为MobileGlues渲染器进行游戏。";
    
    @ConfigEntry.Category("network")
    public boolean enableUDPListener = true;
    
    @ConfigEntry.Category("network")
    public int udpPort = 12450;
    
    @ConfigEntry.Category("network")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 30)
    public int udpTimeoutMinutes = 10;
    
    @ConfigEntry.Category("network")
    public String touchControllerWarningText = "§cTouchController未安装或已被禁用";
    
    @ConfigEntry.Category("display")
    public boolean enablePortraitCheck = true;
    
    @ConfigEntry.Category("display")
    public String portraitWarningText = "§c检测到您正在使用竖屏模式，建议切换为横屏以获得最佳游戏体验！";
    
    @ConfigEntry.Category("performance")
    public boolean enablePerformanceMonitor = true;
    
    @ConfigEntry.Category("performance")
    @ConfigEntry.BoundedDiscrete(min = 10, max = 200)
    public int targetFPS = 60;
    
    @ConfigEntry.Category("performance")
    public boolean enableAutoFPSLimit = true;
    
    @ConfigEntry.Category("performance")
    public boolean enableTouchOptimization = true;
    
    @ConfigEntry.Category("performance")
    public boolean enableChunkOptimization = true;
    
    @ConfigEntry.Category("performance")
    public boolean enableRenderDistanceOptimization = true;
    
    @ConfigEntry.Category("performance")
    @ConfigEntry.BoundedDiscrete(min = 2, max = 16)
    public int maxRenderDistance = 8;
    
    @ConfigEntry.Category("performance")
    public String lowFPSText = "§e检测到FPS较低 (%d)，已自动优化部分设置。";
    
    @ConfigEntry.Category("advanced")
    public boolean enableDebugLogging = false;
    
    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 20, max = 600)
    public int memoryCheckInterval = 200;
    
    @ConfigEntry.Category("advanced")
    public boolean enableBatteryMonitor = true;
    
    @ConfigEntry.Category("advanced")
    public String lowBatteryText = "§e检测到设备电量较低 (%d%%)，建议连接电源以获得最佳性能。";
    
    public static AirExtraConfig load() {
        return AutoConfig.register(AirExtraConfig.class, JanksonConfigSerializer::new).getConfig();
    }
    
    public void save() {
        AutoConfig.getConfigHolder(AirExtraConfig.class).save();
    }
    
    public boolean hasBeenInitialized() {
        return initialized;
    }
    
    public void setInitialized(boolean value) {
        this.initialized = value;
    }
    
    public boolean isModEnabled() {
        return modEnabled;
    }
    
    public void setModEnabled(boolean enabled) {
        this.modEnabled = enabled;
    }
    
    public boolean isEnableMemoryWarning() {
        return enableMemoryWarning;
    }
    
    public boolean isEnableRendererCheck() {
        return enableRendererCheck;
    }
    
    public boolean isEnablePortraitCheck() {
        return enablePortraitCheck;
    }
    
    public boolean isEnableMemoryAllocationCheck() {
        return enableMemoryAllocationCheck;
    }
    
    public boolean isEnableUDPListener() {
        return enableUDPListener;
    }
    
    public boolean isEnableTouchOptimization() {
        return enableTouchOptimization;
    }
    
    public boolean isEnablePerformanceMonitor() {
        return enablePerformanceMonitor;
    }
    
    public int getMemoryWarningThreshold() {
        return memoryWarningThreshold;
    }
    
    public int getMinMemoryAllocation() {
        return minMemoryAllocation;
    }
    
    public int getUdpPort() {
        return udpPort;
    }
    
    public int getUdpTimeoutMinutes() {
        return udpTimeoutMinutes;
    }
    
    public int getMemoryCheckInterval() {
        return memoryCheckInterval;
    }
}
