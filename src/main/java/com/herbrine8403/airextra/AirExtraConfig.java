package com.herbrine8403.airextra;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AirExtraConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "airextra.json");
    private static AirExtraConfig instance;

    // 主开关
    public boolean enabled = true;
    public boolean autoEnableOnApple = true;
    public boolean firstRun = true;

    // 内存警告配置
    public boolean enableMemoryWarning = true;
    public long minFreeMemoryMB = 300;
    public long minAllocatedMemoryMB = 1024;
    public String memoryWarningText = "⚠️ 游戏剩余内存不足 {free}MB，建议关闭一些应用或增加内存分配！";
    public String allocatedMemoryWarningText = "⚠️ 游戏分配内存不足 {allocated}MB，建议至少分配 1024MB！";

    // MobileGlues 检测配置
    public boolean enableMobileGluesWarning = true;
    public String mobileGluesWarningText = "⚠️ 检测到您未使用MobileGlues渲染器，可能会导致游戏崩溃，建议切换为MobileGlues渲染器进行游戏。";

    // TouchController 检测配置
    public boolean enableTouchControllerCheck = true;
    public int udpPort = 12450;
    public int udpTimeoutMinutes = 10;
    public String touchControllerWarningText = "⚠️ TouchController未安装或已被禁用！";

    // 竖屏模式检测配置
    public boolean enablePortraitWarning = true;
    public String portraitWarningText = "⚠️ 检测到竖屏模式，建议横屏游戏以获得更好的体验！";

    // 额外功能配置
    public boolean enableFPSLimit = true;
    public int maxFPS = 60;
    public boolean enableRenderDistanceOptimization = true;
    public int optimizedRenderDistance = 6;
    public boolean enableParticlesReduction = true;
    public boolean enableCloudsDisable = true;
    public boolean enableVSyncDisable = false;
    public boolean enablePerformanceHUD = true;

    // 平台检测配置
    public boolean enablePlatformDetection = true;

    public static AirExtraConfig load() {
        if (instance == null) {
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    instance = GSON.fromJson(reader, AirExtraConfig.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    instance = new AirExtraConfig();
                }
            } else {
                instance = new AirExtraConfig();
                save();
            }
        }
        return instance;
    }

    public static void save() {
        if (instance == null) return;
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AirExtraConfig getInstance() {
        return instance;
    }
}