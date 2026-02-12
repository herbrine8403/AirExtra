package com.air_extra.event;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import com.air_extra.feature.MemoryMonitor;
import com.air_extra.feature.ScreenOrientationDetector;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class TickListener {
    
    private static int tickCounter = 0;
    private static int memoryCheckInterval = 200;
    private static int orientationCheckInterval = 100;
    
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!AirExtraClient.isModEnabled() || client.world == null) {
                return;
            }
            
            tickCounter++;
            
            AirExtraConfig config = AirExtraClient.getConfig();
            if (config == null) return;
            
            // 定期检查内存
            if (tickCounter % memoryCheckInterval == 0) {
                MemoryMonitor.check(client, config);
            }
            
            // 定期检查屏幕方向
            if (tickCounter % orientationCheckInterval == 0) {
                ScreenOrientationDetector.checkOrientation(client, config);
            }
        });
        
        AirExtraClient.LOGGER.info("Tick listener registered");
    }
}
