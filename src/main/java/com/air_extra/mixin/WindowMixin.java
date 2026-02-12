package com.air_extra.mixin;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import com.air_extra.feature.ToastHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    
    @Shadow @Final private MinecraftClient client;
    
    @Shadow private int width;
    @Shadow private int height;
    
    private int lastWidth = 0;
    private int lastHeight = 0;
    
    @Inject(method = "onFramebufferSizeChange", at = @At("RETURN"))
    private void onWindowSizeChanged(long window, int width, int height, CallbackInfo ci) {
        if (lastWidth != width || lastHeight != height) {
            lastWidth = width;
            lastHeight = height;
            
            if (AirExtraClient.isModEnabled()) {
                AirExtraConfig config = AirExtraClient.getConfig();
                if (config != null && config.isEnablePortraitCheck()) {
                    boolean isPortrait = height > width;
                    if (isPortrait && client.player != null) {
                        ToastHelper.showWarningToast(client, config.portraitWarningText);
                    }
                    
                    if (config.enableDebugLogging) {
                        AirExtraClient.LOGGER.debug("Window size changed: {}x{}, Portrait: {}", width, height, isPortrait);
                    }
                }
            }
        }
    }
}
