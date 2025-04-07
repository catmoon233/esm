package net.exmo.esm.content;

import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.eventbus.api.Event;

public class QuestUpdateEvent extends Event {
    public Scoreboard scoreboard;

    public QuestUpdateEvent(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
}
