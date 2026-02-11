package com.herbrine8403.airextra;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class PerformanceOptimizer {
    private static boolean optimizationsApplied = false;

    public static void applyOptimizations() {
        if (optimizationsApplied) return;
        
        AirExtraConfig config = AirExtraConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.options == null) return;
        
        // 应用 FPS 限制
        if (config.enableFPSLimit) {
            client.options.getMaxFps().setValue(config.maxFPS);
        }
        
        // 应用渲染距离优化
        if (config.enableRenderDistanceOptimization) {
            int currentRD = client.options.getViewDistance().getValue();
            if (currentRD > config.optimizedRenderDistance) {
                client.options.getViewDistance().setValue(config.optimizedRenderDistance);
            }
        }
        
        // 减少粒子效果
        if (config.enableParticlesReduction) {
            client.options.getParticles().setValue(GameOptions.Particles.MINIMAL);
        }
        
        // 禁用云朵
        if (config.enableCloudsDisable) {
            client.options.getCloudRenderMode().setValue(GameOptions.CloudRenderMode.OFF);
        }
        
        // 禁用 VSync
        if (config.enableVSyncDisable) {
            client.options.enableVsync().setValue(false);
        }
        
        optimizationsApplied = true;
    }

    public static void resetOptimizations() {
        if (!optimizationsApplied) return;
        
        AirExtraConfig config = AirExtraConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.options == null) return;
        
        // 重置 FPS 限制
        client.options.getMaxFps().setValue(260);
        
        // 重置 VSync
        client.options.enableVsync().setValue(true);
        
        optimizationsApplied = false;
    }

    public static boolean isOptimizationsApplied() {
        return optimizationsApplied;
    }
}