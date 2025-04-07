package net.exmo.esm.event;

import net.exmo.esm.content.GameConfig;
import net.exmo.esm.content.GameProcess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.exmo.esm.content.ScoreboardManager.getQuestRound;


@Mod.EventBusSubscriber
public class XuEvent {
    @SubscribeEvent
    public static void pvp(LivingDeathEvent deathEvent){
        if (deathEvent.getEntity() instanceof ServerPlayer player) {
            if (getQuestRound(player.level().getScoreboard()) >= GameConfig.PVP_ROUND.get()){
                GameProcess.getProfiler(player).shinkHeath(3,player);
            }
        }
}
    // 事件状态追踪器
/*    private static enum EventState {
        INACTIVE, // 未激活
        ACTIVE    // 事件进行中
    }

    private static EventState currentState = EventState.INACTIVE;
    private static int eventTimer = 0;
    private static int cooldownTimer = 0;
    private static final Random random = new Random();

    // 时间常量（单位：tick，1秒=20ticks）
    private static final int EVENT_DURATION = 1200;    // 60秒 = 1200ticks
    private static final int COOLDOWN_DURATION = 4800; // 4分钟 = 4800ticks
    private static final float TRIGGER_CHANCE = 0.75f;

    // 注册事件监听
    public static void init() {
        MinecraftForge.EVENT_BUS.register(RandomEventSystem.class);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        switch (currentState) {
            case INACTIVE:
                handleInactiveState();
                break;
            case ACTIVE:
                handleActiveState();
                break;
        }
    }

    private static void handleInactiveState() {
        cooldownTimer++;

        // 达到冷却时间时尝试触发
        if (cooldownTimer >= COOLDOWN_DURATION) {
            if (random.nextFloat() <= TRIGGER_CHANCE) {
                startEvent();
            } else {
                resetCooldown();
            }
        }
    }

    private static void handleActiveState() {
        eventTimer--;

        // 事件结束处理
        if (eventTimer <= 0) {
            endEvent();
        } else {
            executeEventLogic();
        }
    }

    private static void startEvent() {
        currentState = EventState.ACTIVE;
        eventTimer = EVENT_DURATION;
        cooldownTimer = 0;

        // 这里添加事件启动逻辑
        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(
                Component.literal("随机事件已触发！"), false
        );
    }

    private static void endEvent() {
        currentState = EventState.INACTIVE;
        resetCooldown();

        // 这里添加事件结束清理逻辑
        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(
                Component.literal("随机事件结束"), false
        );
    }

    private static void resetCooldown() {
        cooldownTimer = 0;
    }

    private static void executeEventLogic() {
        // 这里添加持续事件的效果逻辑
        // 示例：每2秒给所有玩家一个效果
        if (eventTimer % 40 == 0) {
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.GLOWING, 60, 0, false, true
                ));
            }
        }
    }

    // 获取当前事件状态（可用于其他系统查询）
    public static boolean isEventActive() {
        return currentState == EventState.ACTIVE;
    }
 */
}