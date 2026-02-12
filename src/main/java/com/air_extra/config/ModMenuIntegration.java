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
        AirExtraConfig config = AirExtraClient.getConfig();
        
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("text.autoconfig.air_extra.title"));
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        
        // 创建性能优化分类
        ConfigCategory performanceCategory = builder.getOrCreateCategory(Text.translatable("text.autoconfig.air_extra.category.performance"));
        
        // 添加备份设置选项
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
        
        // 分隔线
        performanceCategory.addEntry(entryBuilder.startTextDescription(
                Text.literal("§7━━━━━━ 设置恢复 ━━━━━━"))
            .build());
        
        // 显示备份状态
        String backupInfo = PerformanceMonitor.hasBackup() 
            ? "§a✓ 有可用备份: " + PerformanceMonitor.getBackupInfo()
            : "§c✗ 无可用备份";
        performanceCategory.addEntry(entryBuilder.startTextDescription(
                Text.literal(backupInfo))
            .build());
        
        // 添加恢复按钮（使用 BooleanToggle 作为伪按钮）
        performanceCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.literal("恢复优化前设置"), 
                false)
            .setDefaultValue(false)
            .setYesNoTextSupplier(value -> value ? Text.literal("§a已恢复") : Text.literal("§e点击恢复"))
            .setSaveConsumer(value -> {
                if (value && PerformanceMonitor.hasBackup()) {
                    PerformanceMonitor.restoreSettings(MinecraftClient.getInstance());
                }
            })
            .setTooltip(Text.literal("恢复自动优化前的游戏设置（视距、云、粒子）"))
            .build());
        
        // 使用说明
        performanceCategory.addEntry(entryBuilder.startTextDescription(
                Text.literal("§7提示：开启「恢复优化前设置」开关即可恢复备份的设置"))
            .build());
        
        // 设置保存监听器
        builder.setSavingRunnable(() -> {
            config.save();
        });
        
        return builder.build();
    }
}
