package com.herbrine8403.airextra;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class AirExtra implements ClientModInitializer {
    public static final String MOD_ID = "airextra";
    public static AirExtraConfig config;
    private static MinecraftClient client;
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 100; // 每100 ticks检查一次 (约5秒)

    @Override
    public void onInitializeClient() {
        config = AirExtraConfig.load();
        client = MinecraftClient.getInstance();
        
        // 平台检测
        PlatformDetector.detect();
        
        // 首次运行时自动启用检测
        if (config.firstRun && config.autoEnableOnApple) {
            if (PlatformDetector.isApplePlatform()) {
                config.enabled = true;
                config.firstRun = false;
                AirExtraConfig.save();
            }
        }
        
        if (!config.enabled) {
            return;
        }
        
        // 注册事件
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        HudRenderCallback.EVENT.register(this::onHudRender);
        
        // 启动 UDP 监听
        if (config.enableTouchControllerCheck) {
            UDPMonitor.getInstance().start();
        }
        
        // 应用性能优化
        if (PlatformDetector.isIOS()) {
            PerformanceOptimizer.applyOptimizations();
        }
    }

    private void onClientTick(MinecraftClient client) {
        tickCounter++;
        
        if (tickCounter < CHECK_INTERVAL) {
            return;
        }
        
        tickCounter = 0;
        
        if (client.world == null || client.player == null) {
            return;
        }
        
        // 检查内存警告
        checkMemoryWarning();
        
        // 检查 MobileGlues
        checkMobileGlues();
        
        // 检查竖屏模式
        checkPortraitMode();
        
        // 检查分配内存
        checkAllocatedMemory();
    }

    private void onHudRender(net.minecraft.client.gui.DrawContext drawContext, float tickDelta) {
        if (config.enablePerformanceHUD && PlatformDetector.isIOS()) {
            renderPerformanceHUD(drawContext);
        }
    }

    private void checkMemoryWarning() {
        if (!config.enableMemoryWarning) return;
        
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        long availableMemory = maxMemory - usedMemory;
        
        if (availableMemory < config.minFreeMemoryMB) {
            String message = config.memoryWarningText
                .replace("{free}", String.valueOf(availableMemory));
            showWarning(message);
        }
    }

    private void checkMobileGlues() {
        if (!config.enableMobileGluesWarning) return;
        
        if (!PlatformDetector.isUsingMobileGlues()) {
            showWarning(config.mobileGluesWarningText);
        }
    }

    private void checkPortraitMode() {
        if (!config.enablePortraitWarning) return;
        
        if (client.getWindow() == null) return;
        
        int width = client.getWindow().getFramebufferWidth();
        int height = client.getWindow().getFramebufferHeight();
        
        if (height > width) {
            showWarning(config.portraitWarningText);
        }
    }

    private void checkAllocatedMemory() {
        if (!config.enableMemoryWarning) return;
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        
        if (maxMemory < config.minAllocatedMemoryMB) {
            String message = config.allocatedMemoryWarningText
                .replace("{allocated}", String.valueOf(maxMemory));
            showWarning(message);
        }
    }

    private void renderPerformanceHUD(net.minecraft.client.gui.DrawContext drawContext) {
        if (!config.enablePerformanceHUD) return;
        
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        int fps = client.getCurrentFps();
        
        String hudText = String.format(
            "AirExtra | FPS: %d | Mem: %d/%d MB",
            fps, usedMemory, maxMemory
        );
        
        int x = 5;
        int y = 5;
        
        drawContext.drawText(
            client.textRenderer,
            Text.literal(hudText),
            x,
            y,
            0xFFFFFF,
            true
        );
    }

    public static void showWarning(String message) {
        if (client.player == null) return;
        
        // 使用成就/配方解锁通知样式
        client.player.sendMessage(Text.literal(message), true);
    }

    public static AirExtraConfig getConfig() {
        return config;
    }
}