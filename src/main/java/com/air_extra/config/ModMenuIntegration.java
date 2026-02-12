package com.air_extra.config;

import com.air_extra.AirExtraClient;
import com.air_extra.feature.PerformanceMonitor;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> createConfigScreen(parent);
    }
    
    private Screen createConfigScreen(Screen parent) {
        // 使用 AutoConfig 的基础屏幕
        Screen baseScreen = AutoConfig.getConfigScreen(AirExtraConfig.class, parent).get();
        
        // 创建自定义配置构建器
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("text.autoconfig.air_extra.title"));
        
        // 复制 AutoConfig 的配置
        AirExtraConfig config = AirExtraConfig.getConfig();
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        
        // 创建性能优化分类
        ConfigCategory performanceCategory = builder.getOrCreateCategory(Text.translatable("text.autoconfig.air_extra.category.performance"));
        
        // 添加恢复设置按钮
        performanceCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.literal("启用设置备份"), 
                config.enableSettingsBackup)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.enableSettingsBackup = value)
            .setTooltip(Text.literal("自动优化前备份原始设置，允许后续恢复"))
            .build());
        
        performanceCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.literal("启用自动优化"), 
                config.enableAutoFPSLimit)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.enableAutoFPSLimit = value)
            .setTooltip(Text.literal("FPS过低时自动降低设置"))
            .build());
        
        // 添加恢复按钮
        performanceCategory.addEntry(entryBuilder.startTextDescription(
                Text.literal("§7━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"))
            .build());
        
        // 显示备份状态
        String backupInfo = PerformanceMonitor.hasBackup() 
            ? "§a✓ 有可用备份: " + PerformanceMonitor.getBackupInfo()
            : "§c✗ 无可用备份";
        performanceCategory.addEntry(entryBuilder.startTextDescription(
                Text.literal(backupInfo))
            .build());
        
        // 恢复按钮
        performanceCategory.addEntry(entryBuilder.startButton(
                Text.literal("§e[恢复优化前设置]"),
                button -> {
                    if (PerformanceMonitor.hasBackup()) {
                        PerformanceMonitor.restoreSettings(MinecraftClient.getInstance());
                        button.setMessage(Text.literal("§a[已恢复设置]"));
                    } else {
                        button.setMessage(Text.literal("§c[无可用备份]"));
                    }
                })
            .setTooltip(Text.literal("恢复自动优化前的游戏设置（视距、云、粒子）"))
            .build());
        
        // 设置保存监听器
        builder.setSavingRunnable(() -> {
            config.save();
        });
        
        return builder.build();
    }
}
