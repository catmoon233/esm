package net.exmo.esm.content;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;



@Mod.EventBusSubscriber
public class EsmCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("esm_game_start").requires(s -> s.hasPermission(4)).executes(arguments -> {
            try {

            GameProcess.start((ServerLevel) arguments.getSource().getUnsidedLevel());


            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }));
        event.getDispatcher().register(Commands.literal("esm_game_over").requires(s -> s.hasPermission(4)).executes(arguments -> {
            try {

                GameProcess.over( arguments.getSource().getUnsidedLevel().getScoreboard());


            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }));
        event.getDispatcher().register(Commands.literal("esm_game_info").requires(s -> s.hasPermission(4)).executes(arguments -> {
            try {

                GameProcess.CommonEvent.serverLoad();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }));
    }



}
