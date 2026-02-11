package com.airextra;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class AirExtraConfig {
    private static final String CONFIG_FILE_NAME = "airextra.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // Warning toggles
    private boolean modEnabled = true;
    private boolean memoryWarningEnabled = true;
    private boolean rendererWarningEnabled = true;
    private boolean touchControllerCheckEnabled = true;
    private boolean orientationWarningEnabled = true;
    private boolean memoryAllocationWarningEnabled = true;
    
    // Custom warning messages
    private String lowMemoryWarningMessage = "游戏剩余内存不足300MB，请及时保存并关闭不必要的应用";
    private String rendererWarningMessage = "检测到您未使用MobileGlues渲染器，可能会导致游戏崩溃，建议切换为MobileGlues渲染器进行游戏。";
    private String touchControllerWarningMessage = "TouchController未安装或已被禁用";
    private String orientationWarningMessage = "检测到竖屏模式，建议切换到横屏以获得更好的游戏体验";
    private String lowMemoryWarningMessage = "游戏分配的内存小于1024MB，建议在启动器设置中增加分配内存";
    
    // Thresholds
    private long lowMemoryThreshold = 300 * 1024 * 1024; // 300MB
    private long minMemoryAllocation = 1024 * 1024 * 1024; // 1024MB
    
    public void load() {
        try {
            Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
            
            if (Files.exists(configPath)) {
                String jsonContent = Files.readString(configPath);
                JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
                
                // Load basic settings
                modEnabled = jsonObject.get("modEnabled").getAsBoolean();
                memoryWarningEnabled = jsonObject.get("memoryWarningEnabled").getAsBoolean();
                rendererWarningEnabled = jsonObject.get("rendererWarningEnabled").getAsBoolean();
                touchControllerCheckEnabled = jsonObject.get("touchControllerCheckEnabled").getAsBoolean();
                orientationWarningEnabled = jsonObject.get("orientationWarningEnabled").getAsBoolean();
                memoryAllocationWarningEnabled = jsonObject.get("memoryAllocationWarningEnabled").getAsBoolean();
                
                // Load custom messages
                if (jsonObject.has("lowMemoryWarningMessage")) {
                    lowMemoryWarningMessage = jsonObject.get("lowMemoryWarningMessage").getAsString();
                }
                if (jsonObject.has("rendererWarningMessage")) {
                    rendererWarningMessage = jsonObject.get("rendererWarningMessage").getAsString();
                }
                if (jsonObject.has("touchControllerWarningMessage")) {
                    touchControllerWarningMessage = jsonObject.get("touchControllerWarningMessage").getAsString();
                }
                if (jsonObject.has("orientationWarningMessage")) {
                    orientationWarningMessage = jsonObject.get("orientationWarningMessage").getAsString();
                }
                if (jsonObject.has("lowMemoryWarningMessage")) {
                    lowMemoryWarningMessage = jsonObject.get("lowMemoryWarningMessage").getAsString();
                }
                
                // Load thresholds
                if (jsonObject.has("lowMemoryThreshold")) {
                    lowMemoryThreshold = jsonObject.get("lowMemoryThreshold").getAsLong();
                }
                if (jsonObject.has("minMemoryAllocation")) {
                    minMemoryAllocation = jsonObject.get("minMemoryAllocation").getAsLong();
                }
                
                AirExtra.LOGGER.info("AirExtra config loaded successfully");
            } else {
                save(); // Create default config
            }
        } catch (Exception e) {
            AirExtra.LOGGER.error("Error loading AirExtra config", e);
        }
    }
    
    public void save() {
        try {
            Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
            JsonObject jsonObject = new JsonObject();
            
            // Save basic settings
            jsonObject.addProperty("modEnabled", modEnabled);
            jsonObject.addProperty("memoryWarningEnabled", memoryWarningEnabled);
            jsonObject.addProperty("rendererWarningEnabled", rendererWarningEnabled);
            jsonObject.addProperty("touchControllerCheckEnabled", touchControllerCheckEnabled);
            jsonObject.addProperty("orientationWarningEnabled", orientationWarningEnabled);
            jsonObject.addProperty("memoryAllocationWarningEnabled", memoryAllocationWarningEnabled);
            
            // Save custom messages
            jsonObject.addProperty("lowMemoryWarningMessage", lowMemoryWarningMessage);
            jsonObject.addProperty("rendererWarningMessage", rendererWarningMessage);
            jsonObject.addProperty("touchControllerWarningMessage", touchControllerWarningMessage);
            jsonObject.addProperty("orientationWarningMessage", orientationWarningMessage);
            jsonObject.addProperty("lowMemoryWarningMessage", lowMemoryWarningMessage);
            
            // Save thresholds
            jsonObject.addProperty("lowMemoryThreshold", lowMemoryThreshold);
            jsonObject.addProperty("minMemoryAllocation", minMemoryAllocation);
            
            Files.writeString(configPath, GSON.toJson(jsonObject));
            AirExtra.LOGGER.info("AirExtra config saved successfully");
        } catch (Exception e) {
            AirExtra.LOGGER.error("Error saving AirExtra config", e);
        }
    }
    
    // Getters and setters
    public boolean isModEnabled() { return modEnabled; }
    public void setModEnabled(boolean modEnabled) { this.modEnabled = modEnabled; }
    
    public boolean isMemoryWarningEnabled() { return memoryWarningEnabled; }
    public void setMemoryWarningEnabled(boolean memoryWarningEnabled) { this.memoryWarningEnabled = memoryWarningEnabled; }
    
    public boolean isRendererWarningEnabled() { return rendererWarningEnabled; }
    public void setRendererWarningEnabled(boolean rendererWarningEnabled) { this.rendererWarningEnabled = rendererWarningEnabled; }
    
    public boolean isTouchControllerCheckEnabled() { return touchControllerCheckEnabled; }
    public void setTouchControllerCheckEnabled(boolean touchControllerCheckEnabled) { this.touchControllerCheckEnabled = touchControllerCheckEnabled; }
    
    public boolean isOrientationWarningEnabled() { return orientationWarningEnabled; }
    public void setOrientationWarningEnabled(boolean orientationWarningEnabled) { this.orientationWarningEnabled = orientationWarningEnabled; }
    
    public boolean isMemoryAllocationWarningEnabled() { return memoryAllocationWarningEnabled; }
    public void setMemoryAllocationWarningEnabled(boolean memoryAllocationWarningEnabled) { this.memoryAllocationWarningEnabled = memoryAllocationWarningEnabled; }
    
    public String getLowMemoryWarningMessage() { return lowMemoryWarningMessage; }
    public void setLowMemoryWarningMessage(String lowMemoryWarningMessage) { this.lowMemoryWarningMessage = lowMemoryWarningMessage; }
    
    public String getRendererWarningMessage() { return rendererWarningMessage; }
    public void setRendererWarningMessage(String rendererWarningMessage) { this.rendererWarningMessage = rendererWarningMessage; }
    
    public String getTouchControllerWarningMessage() { return touchControllerWarningMessage; }
    public void setTouchControllerWarningMessage(String touchControllerWarningMessage) { this.touchControllerWarningMessage = touchControllerWarningMessage; }
    
    public String getOrientationWarningMessage() { return orientationWarningMessage; }
    public void setOrientationWarningMessage(String orientationWarningMessage) { this.orientationWarningMessage = orientationWarningMessage; }
    
    public String getLowMemoryWarningMessage() { return lowMemoryWarningMessage; }
    public void setLowMemoryWarningMessage(String lowMemoryWarningMessage) { this.lowMemoryWarningMessage = lowMemoryWarningMessage; }
    
    public long getLowMemoryThreshold() { return lowMemoryThreshold; }
    public void setLowMemoryThreshold(long lowMemoryThreshold) { this.lowMemoryThreshold = lowMemoryThreshold; }
    
    public long getMinMemoryAllocation() { return minMemoryAllocation; }
    public void setMinMemoryAllocation(long minMemoryAllocation) { this.minMemoryAllocation = minMemoryAllocation; }
}