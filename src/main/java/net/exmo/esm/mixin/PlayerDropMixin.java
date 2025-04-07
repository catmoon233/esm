package net.exmo.esm.mixin;

import net.exmo.esm.content.GameDontDoChallengeHandle;
import net.exmo.esm.content.GameProcess;
import net.exmo.esm.content.PlayerDropEvent;
import net.exmo.esm.event.OutEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class PlayerDropMixin {
    @Inject(at = @At("HEAD"), method = "swing", cancellable = true)
    public void swing(InteractionHand p_9031_, CallbackInfo ci) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
            if(GameProcess.getProfiler(serverPlayer).getActiveDontChallenge().equals(GameDontDoChallengeHandle.DON_HIGH_CCCCCCCCCC.id)){
                GameProcess.getProfiler(serverPlayer).recordViolation("左右键使用违规", serverPlayer);
                ci.cancel();
            }
    }
    @Inject(at = @At("HEAD"), method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;")
    public void drop(ItemStack p_9085_, boolean p_9086_, boolean p_9087_, CallbackInfoReturnable<ItemEntity> cir) {
        MinecraftForge.EVENT_BUS.post(new PlayerDropEvent(
                (ServerPlayer) (Object) this,
                p_9085_
        ));
    }
}
