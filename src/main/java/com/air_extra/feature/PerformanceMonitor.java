package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.ParticlesMode;

import java.util.concurrent.atomic.AtomicBoolean;

public class PerformanceMonitor {
    
    private static final AtomicBoolean monitoring = new AtomicBoolean(false);
    private static Thread monitorThread;
    private static int currentFPS = 0;
    private static int lowFPSCount = 0;
    private static final int LOW_FPS_THRESHOLD = 3;
    
    // 设置备份
    private static SettingsBackup backup = null;
    private static boolean hasBackup = false;
    
    /**
     * 存储优化前的设置备份
     */
    public static class SettingsBackup {
        public final int renderDistance;
        public final CloudRenderMode cloudRenderMode;
        public final ParticlesMode particlesMode;
        public final long backupTime;
        
        public SettingsBackup(int renderDistance, CloudRenderMode cloudRenderMode, ParticlesMode particlesMode) {
            this.renderDistance = renderDistance;
            this.cloudRenderMode = cloudRenderMode;
            this.particlesMode = particlesMode;
            this.backupTime = System.currentTimeMillis();
        }
    }
    
    public static void startMonitoring(MinecraftClient client, AirExtraConfig config) {
        if (!config.isEnablePerformanceMonitor() || monitoring.get()) return;
        
        monitoring.set(true);
        
        monitorThread = new Thread(() -> {
            while (monitoring.get()) {
                try {
                    Thread.sleep(5000);
                    
                    if (client.player != null && client.world != null) {
                        currentFPS = client.getCurrentFps();
                        
                        if (AirExtraClient.getConfig().enableDebugLogging) {
                            AirExtraClient.LOGGER.debug("Current FPS: {}", currentFPS);
                        }
                        
                        if (currentFPS < 20 && currentFPS > 0) {
                            lowFPSCount++;
                            
                            if (lowFPSCount >= LOW_FPS_THRESHOLD) {
                                applyPerformanceOptimizations(client, config);
                                lowFPSCount = 0;
                            }
                        } else {
                            lowFPSCount = Math.max(0, lowFPSCount - 1);
                        }
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "AirExtra-Performance-Monitor");
        
        monitorThread.setDaemon(true);
        monitorThread.start();
        
        AirExtraClient.LOGGER.info("Performance monitoring started");
    }
    
    private static void applyPerformanceOptimizations(MinecraftClient client, AirExtraConfig config) {
        if (!config.enableAutoFPSLimit) return;
        
        client.execute(() -> {
            try {
                GameOptions options = client.options;
                
                // 在优化前备份当前设置
                backupSettings(options);
                
                int currentRenderDistance = options.getViewDistance().getValue();
                if (currentRenderDistance > 4) {
                    options.getViewDistance().setValue(currentRenderDistance - 2);
                }
                
                options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
                options.getParticles().setValue(ParticlesMode.MINIMAL);
                
                String message = String.format(config.lowFPSText, currentFPS);
                ToastHelper.showInfoToast(client, "AirExtra", message.replace("§e", ""));
                
                AirExtraClient.LOGGER.info("[PerformanceMonitor] Applied performance optimizations due to low FPS (backup created)");
                
            } catch (Exception e) {
                AirExtraClient.LOGGER.warn("[PerformanceMonitor] Failed to apply performance optimizations: {}", e.getMessage());
            }
        });
    }
    
    /**
     * 备份当前游戏设置
     */
    private static void backupSettings(GameOptions options) {
        backup = new SettingsBackup(
            options.getViewDistance().getValue(),
            options.getCloudRenderMode().getValue(),
            options.getParticles().getValue()
        );
        hasBackup = true;
        AirExtraClient.LOGGER.info("[PerformanceMonitor] Settings backed up: renderDistance={}, clouds={}, particles={}", 
            backup.renderDistance, backup.cloudRenderMode, backup.particlesMode);
    }
    
    /**
     * 恢复备份的游戏设置
     * @param client MinecraftClient 实例
     * @return 是否成功恢复
     */
    public static boolean restoreSettings(MinecraftClient client) {
        if (!hasBackup || backup == null) {
            AirExtraClient.LOGGER.warn("[PerformanceMonitor] No backup available to restore");
            return false;
        }
        
        if (client == null) {
            AirExtraClient.LOGGER.warn("[PerformanceMonitor] Client is null, cannot restore settings");
            return false;
        }
        
        client.execute(() -> {
            try {
                GameOptions options = client.options;
                
                options.getViewDistance().setValue(backup.renderDistance);
                options.getCloudRenderMode().setValue(backup.cloudRenderMode);
                options.getParticles().setValue(backup.particlesMode);
                
                AirExtraClient.LOGGER.info("[PerformanceMonitor] Settings restored: renderDistance={}, clouds={}, particles={}", 
                    backup.renderDistance, backup.cloudRenderMode, backup.particlesMode);
                
                ToastHelper.showInfoToast(client, "AirExtra", "已恢复优化前的游戏设置");
                
                // 清除备份
                clearBackup();
                
            } catch (Exception e) {
                AirExtraClient.LOGGER.error("[PerformanceMonitor] Failed to restore settings", e);
            }
        });
        
        return true;
    }
    
    /**
     * 清除备份
     */
    public static void clearBackup() {
        backup = null;
        hasBackup = false;
        AirExtraClient.LOGGER.debug("[PerformanceMonitor] Backup cleared");
    }
    
    /**
     * 检查是否有可用的备份
     */
    public static boolean hasBackup() {
        return hasBackup && backup != null;
    }
    
    /**
     * 获取备份信息字符串
     */
    public static String getBackupInfo() {
        if (!hasBackup || backup == null) {
            return "无可用备份";
        }
        return String.format("视距: %d, 云: %s, 粒子: %s", 
            backup.renderDistance, 
            backup.cloudRenderMode.name(), 
            backup.particlesMode.name());
    }
    
    public static void stopMonitoring() {
        monitoring.set(false);
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        AirExtraClient.LOGGER.info("Performance monitoring stopped");
    }
    
    public static int getCurrentFPS() {
        return currentFPS;
    }
    
    public static boolean isMonitoring() {
        return monitoring.get();
    }
}
