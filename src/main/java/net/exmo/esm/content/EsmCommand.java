package net.exmo.esm.content;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.exmo.esm.event.NBTItemEventHandle;
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
    public static final SuggestionProvider<CommandSourceStack> Suggestion_NBT_ITEMs = (ctx, builder) -> SharedSuggestionProvider.suggest(NBTItemHandle.items.keySet(), builder);
    public static final SuggestionProvider<CommandSourceStack> Suggestion_Dont_challenge = (ctx, builder) -> SharedSuggestionProvider.suggest(GameDontDoChallengeHandle.challenges.keySet(), builder);
    public static final SuggestionProvider<CommandSourceStack> Suggestion_challenge = (ctx, builder) -> SharedSuggestionProvider.suggest(GameChallengeHandle.challenges.keySet(), builder);

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
        event.getDispatcher().register(Commands.literal("esm_game_set_dont_challenge").requires(s -> s.hasPermission(4)).then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("challenge", StringArgumentType.word()).suggests(Suggestion_Dont_challenge).executes(arguments -> {
            try {

                 GameProcess.getProfiler(EntityArgument.getPlayer(arguments, "player")).dontDoChallenge = StringArgumentType.getString(arguments, "challenge");


            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }))));
        event.getDispatcher().register(Commands.literal("esm_game_set_challenge").requires(s -> s.hasPermission(4)).then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("challenge", StringArgumentType.word()).suggests(Suggestion_challenge).executes(arguments -> {
            try {
                GameProcess.getProfiler(EntityArgument.getPlayer(arguments, "player")).challenge = StringArgumentType.getString(arguments, "challenge");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }))));
        event.getDispatcher().register(Commands.literal("getEsmItem").requires(s -> s.hasPermission(4)).then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("item_id", StringArgumentType.word()).suggests(Suggestion_NBT_ITEMs).executes(arguments -> {

            String _setval = StringArgumentType.getString(arguments, "item_id");
            Player player = EntityArgument.getPlayer(arguments, "player");

            try {
                player.addItem(NBTItemHandle.getNbtItem(_setval).getItemStack());
            }catch (Exception e){
                e.printStackTrace();
            }
            return 0;
        }))));
    }



}
