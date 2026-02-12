package com.air_extra.config;

import com.air_extra.AirExtraClient;
import com.air_extra.feature.PerformanceMonitor;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
        
        // 使用 SubCategory 添加恢复按钮区域
        SubCategoryBuilder restoreSubCategory = entryBuilder.startSubCategory(Text.literal("恢复操作"));
        restoreSubCategory.add(entryBuilder.startTextDescription(
                Text.literal("§7点击下方按钮恢复优化前的游戏设置"))
            .build());
        performanceCategory.addEntry(restoreSubCategory.build());
        
        // 设置保存监听器
        builder.setSavingRunnable(() -> {
            config.save();
        });
        
        // 在屏幕底部添加恢复按钮
        builder.setAfterInitConsumer(screen -> {
            // 添加恢复按钮到屏幕
            int buttonWidth = 150;
            int buttonHeight = 20;
            int centerX = screen.width / 2 - buttonWidth / 2;
            int buttonY = screen.height - 35;
            
            ButtonWidget restoreButton = ButtonWidget.builder(
                Text.literal("§e[恢复优化前设置]"),
                button -> {
                    if (PerformanceMonitor.hasBackup()) {
                        PerformanceMonitor.restoreSettings(MinecraftClient.getInstance());
                    } else {
                        // 如果没有备份，显示提示
                        button.setMessage(Text.literal("§c[无可用备份]"));
                    }
                }
            ).dimensions(centerX - 80, buttonY, buttonWidth, buttonHeight)
             .tooltip(Text.literal("恢复自动优化前的游戏设置（视距、云、粒子）"))
             .build();
            
            screen.addDrawableChild(restoreButton);
        });
        
        return builder.build();
    }
}
