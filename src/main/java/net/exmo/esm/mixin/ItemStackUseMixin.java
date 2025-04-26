package net.exmo.esm.mixin;

import net.exmo.esm.content.NBTItemHandle;
import net.exmo.esm.content.NbtItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(ItemStack.class)
public class ItemStackUseMixin {
    @Inject(method = "onItemUse",at = @At("HEAD"), cancellable = true,remap = false)
    public void use(UseOnContext p_41662_, Function<UseOnContext, InteractionResult> callback, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        if (itemStack.isEmpty())return;
        CompoundTag tag = itemStack.getTag();
        if (tag!=null && tag.contains("esm_item_id")){
            String id = tag.getString("esm_item_id");
            if (id!=null){
                NbtItem nbtItem = NBTItemHandle.getNbtItem(id);
                if (nbtItem==null)return;;
                nbtItem.onRightClick(itemStack, p_41662_.getLevel(), p_41662_.getPlayer(), p_41662_.getHand());
                cir .setReturnValue(InteractionResultHolder.success(itemStack).getResult());
                cir.cancel();
            }
        }
    }
    @Inject(method = "use",at = @At("HEAD"), cancellable = true)
    public void use(Level p_41683_, Player p_41684_, InteractionHand p_41685_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;

        if (itemStack.isEmpty())return;
        CompoundTag tag = itemStack.getTag();
        if (tag!=null && tag.contains("esm_item_id")){
            String id = tag.getString("esm_item_id");
            if (id!=null){
                NbtItem nbtItem = NBTItemHandle.getNbtItem(id);
                if (nbtItem==null)return;;
                nbtItem.onRightClick(itemStack, p_41683_, p_41684_, p_41685_);
                cir .setReturnValue(InteractionResultHolder.success(itemStack));
                cir.cancel();
            }
        }
    }
}
