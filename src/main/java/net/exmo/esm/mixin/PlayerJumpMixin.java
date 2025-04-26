package net.exmo.esm.mixin;

import net.exmo.esm.content.ESMGameEventHandle;
import net.exmo.esm.content.GameDontDoChallengeHandle;
import net.exmo.esm.content.GameProcess;
import net.exmo.esm.content.PlayerGameProfiler;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerJumpMixin {
    @Inject(at = @At("HEAD"), method = "jumpFromGround", cancellable = true)
    public void jumpFromGround(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(serverPlayer);
            if (profiler.getActiveDontChallenge().equals(GameDontDoChallengeHandle.DON_JUMP.id())) {
                profiler.recordViolation( "§c§4跳跃违规",  serverPlayer);
                ci.cancel();
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer.getId(), new Vec3(serverPlayer.getDeltaMovement().x, 0, serverPlayer.getDeltaMovement().z)));
            }
        }
    }
}
