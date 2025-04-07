package net.exmo.esm.content;

import net.exmo.esm.network.ESMVar;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class GameEventManager {
    // 读取当前游戏事件
    public static String getCurrentGameEvent(LevelAccessor world) {
        return ESMVar.MapVariables.get(world).nowGameEvent;
    }

    // 设置并保存新游戏事件
    public static void setCurrentGameEvent(LevelAccessor world, String newEvent) {
        ESMVar.MapVariables data = ESMVar.MapVariables.get(world);
        data.nowGameEvent = newEvent;
        data.setDirty();  // 重要：标记数据需要保存
    }

    // 带自动保存的更新方法
    public static void updateGameEvent(LevelAccessor world, String newEvent) {
        setCurrentGameEvent(world, newEvent);
        // 如果需要立即保存（非必要，setDirty()通常足够）
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.getDataStorage().save();
        }
    }
}
