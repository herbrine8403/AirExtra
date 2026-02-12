package com.air_extra.mixin;

import com.air_extra.AirExtraClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    
    @Inject(method = "run", at = @At("HEAD"))
    private void onRun(CallbackInfo ci) {
        AirExtraClient.LOGGER.info("MinecraftClient started, AirExtra initializing...");
    }
}
