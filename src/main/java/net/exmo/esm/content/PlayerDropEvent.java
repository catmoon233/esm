package net.exmo.esm.content;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerDropEvent extends PlayerEvent {
    public ItemStack item;
    public PlayerDropEvent(Player player, ItemStack item) {
        super(player);
        this.item = item;
    }
}
