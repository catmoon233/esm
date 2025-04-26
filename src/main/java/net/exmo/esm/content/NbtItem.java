package net.exmo.esm.content;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NbtItem {
    public Item item;
    public String id;
    public NbtItem(Item item, String id){
        this.item = item;
        this.id = id;
        NBTItemHandle.items.put(id, this);
    }

    public Component getName(){
        return Component.translatable("item.esm." + id);
    }
    public void onRightClick(ItemStack itemStack, Level level, Player player, InteractionHand interactionHand){

    }
    public void onLeftClick(){

    }
    public ItemStack getItemStack(){
        ItemStack itemStack = new ItemStack(item);
        itemStack.setHoverName(getName());
        CompoundTag orCreateTag = itemStack.getOrCreateTag();
        orCreateTag.putString("esm_item_id", id);
        return itemStack;
    }
}
