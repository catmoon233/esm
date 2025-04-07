package net.exmo.esm.content;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class OnBuildEvent extends PlayerEvent {
    public OnBuildEvent(Player player) {
        super(player);
    }
}
