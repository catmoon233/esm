package net.exmo.esm.content;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

public class EsmGameChangeEvent extends Event {
    public ServerLevel level;
    public ESMGameEvent<?> event;


    public EsmGameChangeEvent(ServerLevel level, ESMGameEvent<?> event) {
        this.level = level;
        this.event = event;
    }
}
