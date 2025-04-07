package net.exmo.esm.mixin;

import net.exmo.esm.content.GameDontDoChallengeHandle;
import net.exmo.esm.content.GameProcess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerBuildMixin {
    @Inject(at = @At("HEAD"), method = "jumpFromGround", cancellable = true)
    public void jumpFromGround(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer serverPlayer){
            if(GameProcess.getProfiler(serverPlayer).getActiveDontChallenge().equals(GameDontDoChallengeHandle.DON_JUMP.id)){
                GameProcess.getProfiler(serverPlayer).recordViolation("跳跃违规", serverPlayer);
                ci.cancel();
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "mayBuild", cancellable = true)
    public void mayBuild(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer serverPlayer){
            if(GameProcess.getProfiler(serverPlayer).getActiveDontChallenge().equals(GameDontDoChallengeHandle.DON_HIGH_CCCCCCCCCC.id)){
                GameProcess.getProfiler(serverPlayer).recordViolation("左右键使用违规", serverPlayer);
                cir.setReturnValue(false);
            }
        }
    }
}
