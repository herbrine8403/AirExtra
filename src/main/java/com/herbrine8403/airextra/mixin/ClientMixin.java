package com.herbrine8403.airextra.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ClientMixin {
    
    @Inject(method = "run", at = @At("HEAD"))
    private void onGameStart(CallbackInfo ci) {
        // 可以在游戏启动时执行一些初始化操作
    }
    
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    private void onFocusChanged(boolean focused, CallbackInfo ci) {
        // 处理焦点变化事件，可以用于优化性能
    }
}