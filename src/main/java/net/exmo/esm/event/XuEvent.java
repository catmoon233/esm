package net.exmo.esm.event;

import net.exmo.esm.content.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent tickEvent){
        ESMGameEventHandle.events.forEach(
                (s, e) -> {
                    if (e.event.equals(TickEvent.PlayerTickEvent.class)){
                        var a =  ((ESMGameEvent<TickEvent.PlayerTickEvent>) e);
                        Level level = tickEvent.player.level();
                        if (level instanceof ServerLevel serverLevel) {
                            if (GameEventManager.getCurrentGameEvent(serverLevel).equals(e.id)) {
                                if (a.onDo != null) {
                                    a.onDo.accept(serverLevel,tickEvent);
                                }
                            }
                        }
                    }
                }
        );
    }
    @SubscribeEvent
    public static void tick(LivingEvent.LivingTickEvent tickEvent){
        ESMGameEventHandle.events.forEach(
                (s, e) -> {
                    if (e.event.equals(LivingEvent.LivingTickEvent.class)){
                        var a =  ((ESMGameEvent<LivingEvent.LivingTickEvent>) e);
                        Level level = tickEvent.getEntity().level();
                        if (level instanceof ServerLevel serverLevel) {
                            if (GameEventManager.getCurrentGameEvent(serverLevel).equals(e.id)) {
                                if (a.onDo != null) {
                                    a.onDo.accept(serverLevel,tickEvent);
                                }
                            }
                        }
                    }
                }
        );
    }
    @SubscribeEvent
    public static void onChangeEvent(EsmGameChangeEvent event){
        if (event.event.id.equals(ESMGameEventHandle.HEALING_RAIN.id)){
            event.level.setWeatherParameters(0, event.event.time *20, true, false);
        }
    }
}