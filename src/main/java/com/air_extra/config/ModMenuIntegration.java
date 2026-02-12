package com.air_extra.config;

import com.air_extra.AirExtraClient;
import com.air_extra.feature.PerformanceMonitor;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
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
        // 获取 AutoConfig 默认生成的配置屏幕
        ConfigScreenProvider<AirExtraConfig> provider = 
            (ConfigScreenProvider<AirExtraConfig>) AutoConfig.getConfigScreen(AirExtraConfig.class, parent);
        
        // 在性能优化分类中添加额外的备份恢复选项
        provider.setI18nFunction((key, args) -> {
            if ("text.autoconfig.air_extra.option.performanceSettingsBackup".equals(key)) {
                return Text.literal("启用设置备份");
            }
            if ("text.autoconfig.air_extra.option.performanceRestoreSettings".equals(key)) {
                return Text.literal("恢复优化前设置");
            }
            if ("text.autoconfig.air_extra.option.performanceBackupStatus".equals(key)) {
                String backupInfo = PerformanceMonitor.hasBackup() 
                    ? "§a✓ 有可用备份: " + PerformanceMonitor.getBackupInfo()
                    : "§c✗ 无可用备份";
                return Text.literal(backupInfo);
            }
            return Text.translatable(key, args);
        });
        
        return provider.get();
    }
}
