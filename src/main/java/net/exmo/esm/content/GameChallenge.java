package net.exmo.esm.content;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class GameChallenge {
    public String id;
    public GameChallenge(String id) {
        this.id = id;
        GameChallengeHandle.challenges.put(
                id,this
        );
    }
    public Component name;
    public Component describe;

    public GameChallenge name(String s){
        name = Component.literal(s);

        return this;
    }
    public GameChallenge describe(String s){
        describe = Component.literal(s);
        return this;
    }


    public void finnish(Component component, ServerPlayer player){
        PlayerGameProfiler profiler = GameProcess.getProfiler(player);
        if (!profiler.finishQuest) {
            if (player.level() instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(player, ParticleTypes.ELECTRIC_SPARK, true, player.getX(), player.getY(), player.getZ(), 20, 0.4D, 0.8D, 0.4D, 0.3D);
            }
            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 1);
            profiler.addHeath(1,player);
            player.level().players().forEach(
                    p -> p.displayClientMessage(Component.literal(player.getScoreboardName()).append(" §7完成了任务 §7->").append(component).withStyle(ChatFormatting.GREEN), false)
            );
            profiler.finishQuest = true;
        }
    }

    public String id() {
        return  id;
    }
}
