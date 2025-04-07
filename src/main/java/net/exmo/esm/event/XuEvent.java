package net.exmo.esm.event;

import net.exmo.esm.content.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
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

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent tickEvent){
        ESMGameEventHandle.events.forEach(
                (s, e) -> {
                    if (e.appearType == ESMGameEvent.AppearType.TICK){
                        if (GameEventManager.getCurrentGameEvent(tickEvent.getServer().getLevel(Level.OVERWORLD)).equals(e.id)) {
                            if (e.onDo != null) {
                                e.onDo.accept(tickEvent.getServer().getLevel(Level.OVERWORLD));
                            }
                        }
                    }
                }
        );
    }
}