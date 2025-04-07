package net.exmo.esm.content;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import java.util.HashMap;
import java.util.Map;

public class GameConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 直接使用Forge配置类型
    public static final ForgeConfigSpec.IntValue MAX_HEALTH;
    public static final ForgeConfigSpec.IntValue DONTDOROUND;
    public static final ForgeConfigSpec.IntValue NEXT_QUEST_TIME;
    public static final ForgeConfigSpec.IntValue BORDER_SIZE;
    public static final ForgeConfigSpec.IntValue TEAM_NUMBER;
    public static final ForgeConfigSpec.IntValue PVP_ROUND;
    public static final ForgeConfigSpec.BooleanValue SEE_ONLY_ONE_DONDO;

    // 配置项注册表
    private static final Map<String, ConfigEntry<?>> CONFIG_ENTRIES = new HashMap<>();



    static {
        BUILDER.push("game_settings");

        MAX_HEALTH = BUILDER
                .comment("Maximum player health")
                .defineInRange("max_health", 30, 1, 100);
        register("max_health", MAX_HEALTH);

        DONTDOROUND = BUILDER
                .comment("Don't do challenge round count")
                .defineInRange("dontdo_round", 88, 1, 1000);
        register("dontdo_round", DONTDOROUND);

        NEXT_QUEST_TIME = BUILDER
                .comment("Time between quests (seconds)")
                .defineInRange("next_quest_time", 68, 1, 3600);
        register("next_quest_time", NEXT_QUEST_TIME);

        BORDER_SIZE = BUILDER
                .comment("World border size")
                .defineInRange("border_size", 320, 100, 10000);
        register("border_size", BORDER_SIZE);

        SEE_ONLY_ONE_DONDO = BUILDER
                .comment("Show only one don't do challenge")
                .define("see_only_one_dondo", false);
        register("see_only_one_dondo", SEE_ONLY_ONE_DONDO);
        TEAM_NUMBER = BUILDER
                .comment("team number")
                .defineInRange("team_number", 4, 1, 4);
        register("team_number", TEAM_NUMBER);
        PVP_ROUND = BUILDER
                .comment("pvp round")
                .defineInRange("pvp_round", 25, 1, 10000000);
        register("pvp_round", PVP_ROUND);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static <T> void register(String name, ForgeConfigSpec.ConfigValue<T> configValue) {
        CONFIG_ENTRIES.put(name, new ConfigEntry<>(name, configValue));
    }

    @Mod.EventBusSubscriber
    public static class ConfigCommand {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(Commands.literal("esm_set_config")
                    .requires(source -> source.hasPermission(4))
                    .then(Commands.argument("key", StringArgumentType.string())
                            .suggests((context, builder) -> {
                                CONFIG_ENTRIES.keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(context -> handleConfigCommand(
                                            context.getSource(),
                                            StringArgumentType.getString(context, "key"),
                                            StringArgumentType.getString(context, "value")
                                    )))));
        }

        private static int handleConfigCommand(CommandSourceStack source, String key, String value) {
            ConfigEntry<?> entry = CONFIG_ENTRIES.get(key);
            if (entry == null) {
                source.sendFailure(Component.literal("Invalid config key! Available: "
                        + String.join(", ", CONFIG_ENTRIES.keySet())));
                return 0;
            }

            try {
                if (entry.configValue() instanceof ForgeConfigSpec.IntValue intValue) {
                    int parsed = Integer.parseInt(value);
                    intValue.set(parsed);
                    intValue.save();
                } else if (entry.configValue() instanceof ForgeConfigSpec.BooleanValue boolValue) {
                    boolean parsed = Boolean.parseBoolean(value);
                    boolValue.set(parsed);
                    boolValue.save();
                } else {
                    source.sendFailure(Component.literal("Unsupported config type"));
                    return 0;
                }

                SPEC.save();
                source.sendSuccess(() -> Component.literal("Config %s set to %s".formatted(key, value)), true);
                return 1;
            } catch (NumberFormatException e) {
                source.sendFailure(Component.literal("Invalid number format"));
            } catch (Exception e) {
                source.sendFailure(Component.literal("Error: " + e.getMessage()));
            }
            return 0;
        }
    }

    private record ConfigEntry<T>(String name, ForgeConfigSpec.ConfigValue<T> configValue) {
        public void set(T value) {
            configValue.set(value);
            configValue.save();
        }
    }

    public static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC);
    }
}