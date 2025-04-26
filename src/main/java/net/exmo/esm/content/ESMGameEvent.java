package net.exmo.esm.content;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ESMGameEvent<T> {
    public String id;
    public int time;
    public Class<T> event;
    public AppearType appearType;
    public float weight = 10f;
    public BiConsumer<ServerLevel, T> onDo ;
    public ESMGameEvent(String id, Class<T> event) {
        this.id = id;
        ESMGameEventHandle.events.put(
                id,this
        );
        this.event = event;
    }
    public Component name;
    public Component describe;

    public ESMGameEvent<T> name(String s){
        name = Component.literal(s);

        return this;
    }
    public ESMGameEvent<T> describe(String s){
        describe = Component.literal(s);
        return this;
    }
    public ESMGameEvent<T> time(int time){
        this.time = time;
        return this;
    }
    public ESMGameEvent<T> appear(BiConsumer<ServerLevel,T> consumer){
        this.onDo = consumer;
        return this;

    }
    public ESMGameEvent<T> appearType(AppearType appearType){
        this.appearType = appearType;
        return this;
    }

    public static enum AppearType {
        EVENT,
        NONE
    }



    public String id() {
        return  id;
    }
    public ESMGameEvent<T> weight(float weight) {
        this.weight = weight;
        return this;
    }
}
