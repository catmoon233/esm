package net.exmo.esm.content;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.exmo.esm.content.GameProcess.getProfiler;

public class PlayerNBTManager {
    // NBT标签键值
    private static final String PROFILE_TAG = "GameProfile";
    private static final String CHALLENGE_KEY = "challenge";
    private static final String DONT_DO_KEY = "dontDoChallenge";
    private static final String FAIL_RULE_KEY = "failRule";
    private static final String GAME_OVER_KEY = "gameOver";
    private static final String FINISH_QUEST_KEY = "finishQuest";
    private static final String HEALTH_KEY = "health";

    // 保存数据到NBT
    public static void saveToNBT(Player player, PlayerGameProfiler profiler) {
        CompoundTag tag = player.getPersistentData();
        CompoundTag profileTag = new CompoundTag();

        profileTag.putString(CHALLENGE_KEY, profiler.challenge);
        profileTag.putString(DONT_DO_KEY, profiler.dontDoChallenge);
        profileTag.putBoolean(FAIL_RULE_KEY, profiler.failRule);
        profileTag.putBoolean(GAME_OVER_KEY, profiler.gameOver);
        profileTag.putBoolean(FINISH_QUEST_KEY, profiler.finishQuest);

        // 保存血量到NBT
        int health = PlayerGameProfiler.getHealth(player.getScoreboard(), player.getScoreboardName());
        profileTag.putInt(HEALTH_KEY, health);

        tag.put(PROFILE_TAG, profileTag);
    }

    // 从NBT加载数据
    public static void loadFromNBT(Player player, PlayerGameProfiler profiler) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains(PROFILE_TAG)) return;

        CompoundTag profileTag = tag.getCompound(PROFILE_TAG);
        profiler.challenge = profileTag.getString(CHALLENGE_KEY);
        profiler.dontDoChallenge = profileTag.getString(DONT_DO_KEY);
        profiler.failRule = profileTag.getBoolean(FAIL_RULE_KEY);
        profiler.gameOver = profileTag.getBoolean(GAME_OVER_KEY);
        profiler.finishQuest = profileTag.getBoolean(FINISH_QUEST_KEY);

        // 恢复血量
        int savedHealth = profileTag.getInt(HEALTH_KEY);
        PlayerGameProfiler.setHealth(savedHealth, player.getScoreboard(), player.getScoreboardName());
    }

    // 事件处理类
    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            // 处理玩家死亡重生
            if (event.isWasDeath()) {
                Player original = event.getOriginal();
                Player newPlayer = event.getEntity();

                // 保存旧数据
                original.revive();
                if (original instanceof ServerPlayer serverPlayer) {
                    PlayerGameProfiler profiler = getProfiler(serverPlayer);
                    saveToNBT(serverPlayer, profiler);

                    // 恢复数据到新玩家
                    loadFromNBT(newPlayer, profiler);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            // 玩家登录时加载数据
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                PlayerGameProfiler profiler = getProfiler(serverPlayer);
                loadFromNBT(serverPlayer, profiler);
            }

        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            // 玩家退出时保存数据
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                PlayerGameProfiler profiler = getProfiler(serverPlayer);
                saveToNBT(serverPlayer, profiler);
            }
        }
    }



}
