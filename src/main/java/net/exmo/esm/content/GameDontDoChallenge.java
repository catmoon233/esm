package net.exmo.esm.content;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class GameDontDoChallenge {
    public String id;
    public Component name;
    public Component describe;

    public GameDontDoChallenge(String id) {
        this.id = id;
        GameDontDoChallengeHandle.challenges.put(
                id,this
        );
    }
    public String id (){
        return id;
    }
    public GameDontDoChallenge name(String s){
        name = Component.literal(s);

        return this;
    }
    public GameDontDoChallenge describe(String s){
        describe = Component.literal(s);
        return this;
    }




    public void fail(Component component, ServerPlayer player){
        PlayerGameProfiler profiler = GameProcess.getProfiler(player);
        if (!profiler.failRule) {
            profiler.shinkHeath(
                    2,player
            );
            MutableComponent append = Component.literal(player.getScoreboardName()).append(" ").append(component);
            if (GameProcess.source!=null){
                GameProcess.currentServer.getCommands().performPrefixedCommand(
                        GameProcess.source,
                        "title @a title \"\\u00a74"+append.getString()+" \" "
                );
            }
            if (player.level() instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(player, ParticleTypes.FALLING_LAVA, true, player.getX(), player.getY(), player.getZ(), 20, 0.4D, 0.8D, 0.4D, 0.3D);
            }
            player.playNotifySound(SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 1, 1);
            player.level().players().forEach(
                    p -> {
                        p.displayClientMessage(append, false);
                    }
            );
            profiler.failRule = true;
        }

    }
}
