package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ToastHelper {
    
    public static void showWarningToast(MinecraftClient client, String message) {
        if (client == null) return;
        
        client.execute(() -> {
            try {
                SystemToast.show(
                    client.getToastManager(),
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    Text.literal("AirExtra"),
                    Text.literal(message.replace("§c", "").replace("§e", "").replace("§6", ""))
                );
                
                if (AirExtraClient.getConfig().enableDebugLogging) {
                    AirExtraClient.LOGGER.info("Toast shown: {}", message);
                }
            } catch (Exception e) {
                AirExtraClient.LOGGER.warn("Failed to show toast: {}", e.getMessage());
            }
        });
    }
    
    public static void showInfoToast(MinecraftClient client, String title, String message) {
        if (client == null) return;
        
        client.execute(() -> {
            try {
                SystemToast.show(
                    client.getToastManager(),
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    Text.literal(title),
                    Text.literal(message)
                );
            } catch (Exception e) {
                AirExtraClient.LOGGER.warn("Failed to show info toast: {}", e.getMessage());
            }
        });
    }
    
    public static void showTutorialToast(MinecraftClient client, String title, String message) {
        if (client == null) return;
        
        client.execute(() -> {
            try {
                SystemToast.show(
                    client.getToastManager(),
                    SystemToast.Type.TUTORIAL_HINT,
                    Text.literal(title),
                    Text.literal(message)
                );
            } catch (Exception e) {
                AirExtraClient.LOGGER.warn("Failed to show tutorial toast: {}", e.getMessage());
            }
        });
    }
}
