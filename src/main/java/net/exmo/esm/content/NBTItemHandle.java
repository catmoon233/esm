package net.exmo.esm.content;

import net.exmo.esm.Esm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NBTItemHandle {
    public static Map<String, NbtItem> items = new HashMap<>() {
    };
    public static NbtItem getNbtItem(String s) {
        return items.get(s);
    }
//    @SubscribeEvent
//    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
//        ItemStack itemStack = event.getEntity().getItemInHand(event.getHand());
//        if (itemStack.isEmpty())return;
//        CompoundTag tag = itemStack.getTag();
//        if (tag!=null && tag.contains("esm_item_id")){
//            String id = tag.getString("esm_item_id");
//            NbtItem nbtItem = NBTItemHandle.getNbtItem(id);
//            if (nbtItem==null)return;;
//            nbtItem.onRightClick(itemStack, event.getLevel(), event.getEntity(), event.getHand());
//            event.setCanceled(true);
//            event.setCancellationResult(InteractionResultHolder.success(itemStack).getResult());
//        }
//    }
    public static Optional<NbtItem> getNbtItem(ItemStack stack){
        if (stack.hasTag() && stack.getTag().contains("esm_item_id")) {
            return Optional.ofNullable(items.get(stack.getTag().getString("esm_item_id")));
        } else {
            return Optional.empty();
        }
    }
    public static final NbtItem TheMirrorOfSight = new NbtItem(Items.LIGHT_GRAY_STAINED_GLASS_PANE, "TheMirrorOfSight"){
        @Override
        public Component getName() {
            return Component.literal("§7明视之镜");
        }

        @Override
        public void onRightClick(ItemStack itemStack, Level level, Player player, InteractionHand interactionHand) {
            if (player instanceof ServerPlayer serverPlayer){
                serverPlayer.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,1f,1f);
                serverPlayer.sendSystemMessage(Component.literal(GameProcess.fen_ge_xian));
                itemStack.shrink(1);
                serverPlayer.sendSystemMessage(Component.literal("§7你在镜子面前看了看"+player.getScoreboardName()));
                Esm.queueServerWork(20,()->{
                    serverPlayer.sendSystemMessage(Component.literal("§7你发现..."));
                });
                Esm.queueServerWork(40,()->{
                    serverPlayer.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,1f,1f);
                    GameDontDoChallenge gameDontDoChallenge = GameProcess.getProfiler(serverPlayer).getDontDoChallenge();
                    serverPlayer.sendSystemMessage(Component.literal("§7你不能做: "+ gameDontDoChallenge.name.getString()));
                    serverPlayer.sendSystemMessage(Component.literal("§7描述: "+ gameDontDoChallenge.describe.getString()));
                    serverPlayer.sendSystemMessage(Component.literal(GameProcess.fen_ge_xian));
                });
            }

            super.onRightClick(itemStack, level, player, interactionHand);
        }
    };
}
