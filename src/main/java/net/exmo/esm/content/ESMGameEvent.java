package net.exmo.esm.content;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Consumer;

public class ESMGameEvent {
    public String id;
    public int time;
    public AppearType appearType;
    public Consumer<ServerLevel> onDo ;
    public ESMGameEvent(String id) {
        this.id = id;
        ESMGameEventHandle.events.put(
                id,this
        );
    }
    public Component name;
    public Component describe;

    public ESMGameEvent name(String s){
        name = Component.literal(s);

        return this;
    }
    public ESMGameEvent describe(String s){
        describe = Component.literal(s);
        return this;
    }
    public ESMGameEvent time(int time){
        this.time = time;
        return this;
    }
    public ESMGameEvent appear(Consumer<ServerLevel>consumer){
        this.onDo = consumer;
        return this;

    }
    public ESMGameEvent appearType(AppearType appearType){
        this.appearType = appearType;
        return this;
    }

    public static enum AppearType {
        TICK,
        NONE
    }



    public String id() {
        return  id;
    }
}
