package net.exmo.esm.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.exmo.esm.content.*;
import net.exmo.esm.content.GameProcess;
import net.exmo.esm.content.PlayerGameProfiler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static net.exmo.esm.content.GameChallengeHandle.*;


@Mod.EventBusSubscriber
public class EntEvent {
    private static  Item nowItem = Items.AIR;
    @SubscribeEvent
    public static void randomEventUp(QuestUpdateEvent event){
        List<Item> list = ForgeRegistries.ITEMS.getValues().stream().filter(
                item -> {
                    return item != Items.COMMAND_BLOCK &&
                            item != Items.COMMAND_BLOCK_MINECART &&
                            item != Items.CHAIN_COMMAND_BLOCK &&
                            item != Items.REPEATING_COMMAND_BLOCK &&
                            item != Items.BEDROCK &&
                            item != Items.BARRIER &&
                            item != Items.AIR &&
                            item != Items.POTION &&
                            item != Items.SPLASH_POTION &&
                            item != Items.LINGERING_POTION ;

                }
        ).toList();
        List<Item> toolItems = list.stream()
                .filter(item -> item.getDefaultInstance().is(ItemTags.create(new ResourceLocation("forge", "tools"))))
                .collect(Collectors.toList());

        if (!toolItems.isEmpty()) {
            nowItem = toolItems.get(RandomSource.create().nextInt(toolItems.size()));
        } else {
            nowItem = Items.AIR;
        }

        selectRandomItems();
        if (Objects.equals(GameProcess.nowQuest, RANDOM_ARMOR_CHALLENGE.name.getString())){
            GameProcess.nowQuest = GameProcess.nowQuest+" : "+RANDOM_EQUIPMENT.getName(RANDOM_EQUIPMENT.getDefaultInstance()).getString();
        }
        if (Objects.equals(GameProcess.nowQuest, RANDOM_BLOCK_CHALLENGE.name.getString())){
            GameProcess.nowQuest = GameProcess.nowQuest+" : "+RANDOM_BLOCK.asItem().getName(RANDOM_EQUIPMENT.getDefaultInstance()).getString();
        }
        if (Objects.equals(GameProcess.nowQuest, RANDOM_FOOD_CHALLENGE.name.getString())){
            GameProcess.nowQuest = GameProcess.nowQuest+" : "+RANDOM_FOOD.getName(RANDOM_EQUIPMENT.getDefaultInstance()).getString();
        }
        if (Objects.equals(GameProcess.nowQuest, RANDOM_ITEMS.name.getString())){
            GameProcess.nowQuest = GameProcess.nowQuest+" : "+nowItem.getDescription().getString();
        }
    }
    @SubscribeEvent
    public static void equipmentChange(LivingEquipmentChangeEvent event){
        executeChallenge(event, (player, profiler) -> {
            if (event.getSlot().getType() == EquipmentSlot.Type.HAND) {
                if (event.getTo().getItem() == nowItem) {
                    profiler.finishQuest("手持物品: "+event.getTo().getDisplayName().getString(), player);
                }
            }
        }, RANDOM_ITEMS);
    }
    // 在 EntEvent 类中添加以下静态变量
    private static Item RANDOM_FOOD = Items.APPLE;
    private static Block RANDOM_BLOCK = Blocks.STONE;
    private static Item RANDOM_EQUIPMENT = Items.LEATHER_HELMET;

    // 随机选择挑战物品的方法（可以在游戏开始时或挑战激活时调用）
    private static void selectRandomItems() {
        Set<Item> WHITELIST = Set.of(
                Items.APPLE,
                Items.BREAD,
                Items.PORKCHOP,
                Items.COOKED_PORKCHOP,
                Items.GOLDEN_CARROT,
                Items.CARROT,
                Items.POTATO,
                Items.BAKED_POTATO,
                Items.BEEF,
                Items.COOKED_BEEF,
                Items.CHICKEN,
                Items.COOKED_CHICKEN,
                Items.ROTTEN_FLESH,
                Items.SPIDER_EYE,
                Items.COD,
                Items.COOKED_COD,
                Items.SALMON,
                Items.COOKED_SALMON,
                Items.TROPICAL_FISH,
                Items.MUTTON,
                Items.COOKED_MUTTON,
                Items.RABBIT,
                Items.COOKED_RABBIT,
                Items.EGG,
                Items.MILK_BUCKET,
                Items.HONEY_BOTTLE,
                Items.MELON_SLICE,
                Items.PUMPKIN_PIE,
                Items.COOKIE,
                Items.CAKE,
                Items.BEETROOT,
                Items.SWEET_BERRIES,
                Items.GLOW_BERRIES
        );

        // 随机食物（排除特殊物品）
        List<Item> foods = WHITELIST.stream().toList();
        RANDOM_FOOD = foods.get(new Random().nextInt(foods.size()));

        // 随机可破坏方块（排除基岩等）
        List<Block> breakableBlocks = ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> block.defaultDestroyTime() > 0 && // 可破坏判断
                        block != Blocks.BEDROCK &&
                        block != Blocks.COMMAND_BLOCK &&
                        block != Blocks.BARRIER)
                .toList();
        RANDOM_BLOCK = breakableBlocks.get(new Random().nextInt(breakableBlocks.size()));

        // 随机可装备物品（盔甲/工具）
        List<Item> equipments = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof ArmorItem ||
                        item instanceof TieredItem ||
                        item instanceof ElytraItem)
                .filter(item -> item != Items.BEDROCK) // 排除特殊物品
                .toList();
        RANDOM_EQUIPMENT = equipments.get(new Random().nextInt(equipments.size()));

        lastEatCache.cleanUp();
        VEGGIE_CACHE.clear();
        WATER_CACHE.clear();
        WEAPON_CACHE.clear();
        ALTITUDE_CACHE.clear();
        APPLE_CACHE.clear();
        ATTACK_COUNTER.clear();
        BEDROCK_CACHE.clear();
        BLOCK_CACHE.clear();
        CEILING_CACHE.clear();
        FOOD_CACHE.clear();
        GROUP_CACHE.clear();
        HEALTH_CACHE.clear();
        healthCache.cleanUp();
        HUG_CACHE.clear();
        KILL_CACHE.clear();
        LAST_ATTACK_TIME.clear();
        LAST_DAMAGE_TIME.clear();
        LAST_DROP_TIME.clear();
        LAST_KILL_TIME.clear();
        LAST_PLACE_TIME.clear();
        LAVA_CACHE.clear();
        LIGHT_CACHE.clear();
        PORTAL_CACHE.clear();
        POSITION_HISTORY.clear();
        SATURATION_CACHE.clear();
        positionHistory.clear();

    }

    // 食用随机食物挑战
    @SubscribeEvent
    public static void onEatFood2(LivingEntityUseItemEvent.Finish event) {
        executeChallenge(event, (player, profiler) -> {
            if (event.getItem().getItem() == RANDOM_FOOD) {
                profiler.finishQuest("食用指定食物", player);
                // 奖励：增加随机效果
                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        200,
                        new Random().nextInt(2)
                ));
            }
        }, RANDOM_FOOD_CHALLENGE); // 替换为实际的挑战枚举
    }

    // 挖掘随机方块挑战
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        executeChallenge(event, (player, profiler) -> {
            if (event.getState().getBlock() == RANDOM_BLOCK) {
                // 验证是否可破坏（防止创造模式破坏）
                if (event.getState().getDestroySpeed(player.level(), event.getPos()) > 0) {
                    if (!profiler.finishQuest) {
                        player.getMainHandItem().enchant(
                                Enchantments.BLOCK_EFFICIENCY,
                                new Random().nextInt(5) + 1
                        );
                    }
                    profiler.finishQuest("挖掘指定方块", player);
                    // 惩罚：随机破坏工具
                    if (new Random().nextFloat() < 0.3f) {
                        player.getMainHandItem().hurtAndBreak(
                                10,
                                player,
                                p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND)
                        );

                    }
                }
            }
        }, RANDOM_BLOCK_CHALLENGE); // 替换为实际的挑战枚举
    }

    // 装备随机物品挑战
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        executeChallenge(event, (player, profiler) -> {
            if (event.getTo().getItem() == RANDOM_EQUIPMENT) {
                if (!profiler.finishQuest) {

                    ItemStack stack = event.getTo();
                    if (stack.isEnchantable()) {
                        ChallengeEventHandlers.addRandomEnchantment(
                                stack,
                                RandomSource.create()
                        );
                    }
                }
                profiler.finishQuest("装备指定物品", player);
                // 奖励：添加随机附魔

            }
        }, RANDOM_ARMOR_CHALLENGE); // 替换为实际的挑战枚举
    }

    // 通用执行方法（需要根据你的项目结构调整）
    private static void executeChallenge(BlockEvent.BreakEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameChallenge challenge) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }
    private static void executeChallenge(TickEvent.PlayerTickEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameChallenge challenge) {
        if (event.player instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }

    private static void executeChallenge(EntityEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameChallenge challenge) {
        if (event.getEntity() instanceof ServerPlayer player) {

            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }


        }
    }
    private static void executeChallenge(ItemTossEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameChallenge challenge) {
        if (event.getPlayer() instanceof ServerPlayer player) {

            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }


        }
    }
    @Mod.EventBusSubscriber
    public static class ChallengeEventHandlers {
        // 通用事件处理器
        private static void executeChallenge(EntityEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameChallenge challenge) {
            if (event.getEntity() instanceof ServerPlayer player) {
                PlayerGameProfiler profiler = GameProcess.getProfiler(player);
                if (profiler.getActiveChallenge().equals(challenge.id())) {
                    action.accept(player, profiler);
                }


            }
        }

        private static void executeChallenge(TickEvent.PlayerTickEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameChallenge challenge) {
            if (event.player instanceof ServerPlayer player) {
                PlayerGameProfiler profiler = GameProcess.getProfiler(player);
                if (profiler.getActiveChallenge().equals(challenge.id())) {
                    action.accept(player, profiler);
                }


            }
        }


        // ================== 基础工具方法 ================== //
        private static List<ServerPlayer> getNearbyPlayers(ServerPlayer player, double radius) {
            return player.level().getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(radius),
                    p -> p != player && p.distanceTo(player) <= radius);
        }
        // ================== 挑战检测处理器 ================== //

        // 铁锭/铁工具检测（每20 ticks检查一次）
        @SubscribeEvent
        public static void onIronToolCheck(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
            if (event.player.tickCount % 20 != 0) return; // 降低检测频率

            executeChallenge(event, (player, profiler) -> {
                boolean hasRequirement = player.getInventory().items.stream()
                        .anyMatch(stack ->
                                stack.is(Items.IRON_INGOT) ||
                                        (stack.getItem() instanceof TieredItem tieredItem &&
                                                tieredItem.getTier() == Tiers.IRON)
                        );

                if (hasRequirement) {
                    profiler.finishQuest("获得铁工具", player);
                    applyIronPenalty(player);
                }
            }, CHALLENGE_IRON_OR_TOOL);
        }

        private static void applyIronPenalty(ServerPlayer player) {
            player.getInventory().items.forEach(
                    e -> {
                        if (e.getItem() instanceof TieredItem tieredItem &&
                                tieredItem.getTier() == Tiers.IRON) {
                            if (!GameProcess.getProfiler(player).finishQuest) {

                                List<Enchantment> list = ForgeRegistries.ENCHANTMENTS.getValues().stream().toList();
                                e.enchant(
                                        list.get(
                                                player.level().getRandom().nextInt(list.size())
                                        ), player.getRandom().nextInt(5)
                                );
                            }
                        }
                    }
            );

        }

        // ================ 通用工具方法 ================ //
        public static void addRandomEnchantment(ItemStack stack, RandomSource random) {
            List<Enchantment> applicable = ForgeRegistries.ENCHANTMENTS.getValues().stream()
                    .filter(e -> e.canApplyAtEnchantingTable(stack))
                    .toList();

            if (!applicable.isEmpty()) {
                Enchantment selected = applicable.get(random.nextInt(applicable.size()));
                int level = random.nextInt(selected.getMaxLevel()) + 1;
                stack.enchant(selected, level);
            }
        }

        // ================ 移动检测挑战 ================ //
        private static final Map<UUID, Vec3> LAST_POSITIONS = new ConcurrentHashMap<>();

        @SubscribeEvent
        public static void onMovementChallenge(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
            if (event.player.tickCount % 20 != 0) return;

            executeChallenge(event, (player, profiler) -> {
                Vec3 currentPos = player.position();
                Vec3 lastPos = LAST_POSITIONS.getOrDefault(player.getUUID(), currentPos);

                boolean isMoving = currentPos.distanceToSqr(lastPos) > 4; // 移动超过0.1格
                LAST_POSITIONS.put(player.getUUID(), currentPos);

                if (isMoving) {
                    profiler.finishQuest("保持移动", player);
                }
            }, CHALLENGE_HOLD_W);
        }

        // ================ 手持木头挑战 ================ //
        @SubscribeEvent
        public static void onWoodHoldingCheck(LivingEquipmentChangeEvent event) {
            executeChallenge(event, (player, profiler) -> {
                boolean holdingWood = player.getMainHandItem().is(ItemTags.LOGS) ||
                        player.getOffhandItem().is(ItemTags.LOGS);

                if (holdingWood) {
                    profiler.finishQuest("手持原木", player);
                }
            }, CHALLENGE_HOLD_WOOD);
        }

        // ================ 血量状态挑战 ================ //
        @SubscribeEvent
        public static void onHealthCheck(TickEvent.PlayerTickEvent event) {
            executeChallenge(event, (player, profiler) -> {
                float threshold = player.getMaxHealth() / 2;
                boolean isHealthy = player.getHealth() > threshold;

                if (isHealthy) {
                    profiler.finishQuest("维持健康", player);
                }
            }, CHALLENGE_HEALTH_HALF);
        }
        // ================ 缓存系统 ================ //
        private static final Map<UUID, Boolean> FOOD_CACHE = new ConcurrentHashMap<>();
        private static final Map<UUID, Boolean> MEAT_CACHE = new ConcurrentHashMap<>();
        private static final Map<UUID, Boolean> VEGGIE_CACHE = new ConcurrentHashMap<>();
        private static final Map<UUID, List<ServerPlayer>> NEARBY_CACHE = new ConcurrentHashMap<>();

        // ================ 附近玩家检测（分频检测） ================ //
        @SubscribeEvent
        public static void onNearbyPlayerCheck(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
            if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

            executeChallenge(event, (player, profiler) -> {
                List<ServerPlayer> nearbyPlayers = NEARBY_CACHE.computeIfAbsent(player.getUUID(),
                        k -> findNearbyPlayers(player, 6.0));

                boolean hasPlayers = !nearbyPlayers.isEmpty();
                if (hasPlayers) {
                    profiler.finishQuest("玩家聚集", player);
                }

                // 每10秒刷新缓存
                if (player.tickCount % 200 == 0) {
                    NEARBY_CACHE.put(player.getUUID(), findNearbyPlayers(player, 6.0));
                }
            }, CHALLENGE_NEAR_PLAYER);
        }

        private static List<ServerPlayer> findNearbyPlayers(ServerPlayer player, double radius) {
            AABB area = player.getBoundingBox().inflate(radius);
            return player.level().getEntitiesOfClass(ServerPlayer.class, area,
                    p -> p != player && p.distanceToSqr(player) <= radius * radius);
        }



        private static boolean[] checkFoodCategories(ServerPlayer player) {
            boolean[] result = new boolean[3]; // [普通食物, 肉类, 素食]

            for (ItemStack stack : player.getInventory().items) {
                if (stack.isEdible()) {
                    result[0] = true;
                    FoodProperties foodProperties = stack.getFoodProperties(
                            player
                    );
                    if (foodProperties !=null && foodProperties.isMeat()) result[1] = true;
                    if (foodProperties !=null && !foodProperties.isMeat()) result[2] = true;

                    // 提前退出条件
                    if (result[1] && result[2]) break;
                }
            }
            return result;
        }

        // ================ 缓存清理 ================ //
        @SubscribeEvent
        public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
            UUID uuid = event.getEntity().getUUID();
            FOOD_CACHE.remove(uuid);
            MEAT_CACHE.remove(uuid);
            VEGGIE_CACHE.remove(uuid);
            NEARBY_CACHE.remove(uuid);
        }
        // ================ 贵重物品缓存 ================ //
        private static final Cache<UUID, Boolean> diamondCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build();
        private static final Cache<UUID, Boolean> goldCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build();

        // ================ 钻石&金锭检测（低频扫描） ================ //


        private static boolean checkCachedItem(ServerPlayer player, Item target, Cache<UUID, Boolean> cache) {
            try {
                return cache.get(player.getUUID(), () ->
                        player.getInventory().hasAnyOf(Set.of(target)));
            } catch (ExecutionException e) {
                return false;
            }
        }
        // ================== 钻石检测 ================== //
        private static final Map<UUID, Boolean> DIAMOND_CACHE = new ConcurrentHashMap<>();

        @SubscribeEvent
        public static void onDiamondCheck(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
            if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

            executeChallenge(event, (player, profiler) -> {
                Boolean cached = DIAMOND_CACHE.get(player.getUUID());
                if (cached != null && cached) {
                    profiler.finishQuest("钻石保持", player);
                    return;
                }

                boolean hasDiamond = player.getInventory().hasAnyOf(Set.of(Items.DIAMOND));
                if (hasDiamond) {
                    DIAMOND_CACHE.put(player.getUUID(), true);
                    profiler.finishQuest("获得钻石", player);
                }
            }, CHALLENGE_HAS_DIAMOND);
        }

        // ================== 金锭检测 ================== //
        private static final Map<UUID, Boolean> GOLD_CACHE = new ConcurrentHashMap<>();

        @SubscribeEvent
        public static void onGoldCheck(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
            if (event.player.tickCount % 40 != 0) return;

            executeChallenge(event, (player, profiler) -> {
                Boolean cached = GOLD_CACHE.get(player.getUUID());
                if (cached != null && cached) {
                    profiler.finishQuest("金锭保持", player);
                    return;
                }

                boolean hasGold = player.getInventory().hasAnyOf(Set.of(Items.GOLD_INGOT));
                if (hasGold) {
                    GOLD_CACHE.put(player.getUUID(), true);
                    profiler.finishQuest("获得金锭", player);
                }
            }, CHALLENGE_HAS_GOLD);
        }

        // ================== 工具检测 ================== //
        private static final Map<UUID, Boolean> TOOL_CACHE = new ConcurrentHashMap<>();

        @SubscribeEvent
        public static void onToolCheck(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
            if (event.player.tickCount % 60 != 0) return; // 3秒检测间隔

            executeChallenge(event, (player, profiler) -> {
                Boolean cached = TOOL_CACHE.get(player.getUUID());
                if (cached != null && cached) {
                    profiler.finishQuest("工具保持", player);
                    return;
                }

                boolean hasTool = player.getInventory().items.stream()
                        .anyMatch(stack -> stack.getItem() instanceof TieredItem);

                if (hasTool) {
                    TOOL_CACHE.put(player.getUUID(), true);
                    profiler.finishQuest("获得工具", player);
                }
            }, CHALLENGE_HAS_TOOL);
        }

        // ================== 盔甲检测 ================== //
        private static final Map<UUID, Boolean> ARMOR_CACHE = new ConcurrentHashMap<>();

        @SubscribeEvent
        public static void onArmorCheck(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
            if (event.player.tickCount % 60 != 0) return;

            executeChallenge(event, (player, profiler) -> {
                Boolean cached = ARMOR_CACHE.get(player.getUUID());
                if (cached != null && cached) {
                    profiler.finishQuest("盔甲保持", player);
                    return;
                }

                boolean hasArmor = player.getInventory().items.stream()
                        .anyMatch(stack -> stack.getItem() instanceof ArmorItem)
                        || player.getInventory().armor.stream()
                        .anyMatch(stack -> !stack.isEmpty());

                if (hasArmor) {
                    ARMOR_CACHE.put(player.getUUID(), true);
                    profiler.finishQuest("获得盔甲", player);
                }
            }, CHALLENGE_HAS_ARMOR);
        }

        // ================== 进食检测 ================== //
        private static final Map<UUID, Long> LAST_EAT_TIME = new ConcurrentHashMap<>();

        @SubscribeEvent
        public static void onEatFood(LivingEntityUseItemEvent.Finish event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                if (event.getItem().isEdible()) {
                    LAST_EAT_TIME.put(player.getUUID(), System.currentTimeMillis());
                }
            }
        }

        @SubscribeEvent
        public static void validateEating(TickEvent.PlayerTickEvent event) {
            executeChallenge(event, (player, profiler) -> {
                Long lastEat = LAST_EAT_TIME.get(player.getUUID());
                boolean hasEaten = lastEat != null &&
                        (System.currentTimeMillis() - lastEat) < 5000; // 5秒内有效

                if (hasEaten) {
                    profiler.finishQuest("及时进食", player);
                }
            }, CHALLENGE_EAT_FOOD);
        }


        // ================== 缓存清理 ================== //
        @SubscribeEvent
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            UUID uuid = event.getEntity().getUUID();
            DIAMOND_CACHE.remove(uuid);
            GOLD_CACHE.remove(uuid);
            TOOL_CACHE.remove(uuid);
            ARMOR_CACHE.remove(uuid);
            LAST_EAT_TIME.remove(uuid);
        }
    }



        // ================ 进食检测（事件驱动） ================ //
        private static final LoadingCache<UUID, Long> lastEatCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build(CacheLoader.from(() -> 0L));

        @SubscribeEvent
        public static void onEatFood(LivingEntityUseItemEvent.Finish event) {
            if (event.getEntity() instanceof ServerPlayer player &&
                    event.getItem().isEdible()) {
                lastEatCache.put(player.getUUID(), System.currentTimeMillis());
            }
        }
    // ================ 生命值状态追踪 ================ //
    private static final Cache<UUID, Float> healthCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();

    // ================ 饱和度状态追踪 ================ //
    private static final Map<UUID, Boolean> saturationState = new ConcurrentHashMap<>();

    // ================ 联合状态检测（智能频率） ================ //
    @SubscribeEvent
    public static void onVitalCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        ServerPlayer player = (ServerPlayer) event.player;

        // 动态调整检测频率（基于移动状态）
        int checkInterval = isMoving(player) ? 20 : 40; // 移动时1秒，静止时2秒
        if (player.tickCount % checkInterval != 0) return;

        executeChallenge(event, (player1, profiler) -> {
            if (player1.getHealth() <= player1.getMaxHealth()/3) {
                profiler.finishQuest("生命值低下", player1);
            }
                },
                CHALLENGE_HEALTH_HALF
                );
     //   checkHealthState(player);
        checkSaturationState(player);
    }

//    private static void checkHealthState(ServerPlayer player) {
//        float currentHealth = player.getHealth();
//        float maxHealth = player.getMaxHealth();
//        float threshold = maxHealth * 0.5f;
//
//        // 获取上次记录的健康状态
//        Float lastHealth = healthCache.getIfPresent(player.getUUID());
//        boolean stateChanged = lastHealth == null ||
//                (currentHealth <= threshold) != (lastHealth <= threshold);
//
//        if (stateChanged) {
//            if (currentHealth <= threshold) {
//                if (Objects.equals(GameProcess.getProfiler(player).getActiveChallenge(), CHALLENGE_HEALTH_HALF.id)) GameProcess.getProfiler(player).finishQuest("生命值低下", player);
//            }
//            healthCache.put(player.getUUID(), currentHealth);
//        }
//    }

    private static void checkSaturationState(ServerPlayer player) {
        float saturation = player.getFoodData().getSaturationLevel();
        boolean hasSaturation = saturation > 0.0f;
        Boolean lastState = saturationState.get(player.getUUID());

        // 只在状态变化时更新
        if (lastState == null || hasSaturation != lastState) {
            if (hasSaturation) {
                if (Objects.equals(GameProcess.getProfiler(player).getActiveChallenge(), CHALLENGE_HAS_SATURATION.id))
                GameProcess.getProfiler(player).finishQuest("保持饱腹", player);
            }
            saturationState.put(player.getUUID(), hasSaturation);
        }
    }

    // ================ 移动状态辅助检测 ================ //
    private static final Map<UUID, Vec3> positionHistory = new ConcurrentHashMap<>();

    private static boolean isMoving(ServerPlayer player) {
        Vec3 currentPos = player.position();
        Vec3 lastPos = positionHistory.getOrDefault(player.getUUID(), currentPos);
        positionHistory.put(player.getUUID(), currentPos);

        return currentPos.distanceToSqr(lastPos) > 0.0025; // 移动超过0.05格
    }

    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect1(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        healthCache.invalidate(uuid);
        saturationState.remove(uuid);
        positionHistory.remove(uuid);
    }
    // ================== 食物检测缓存 ================== //
    private static final Map<UUID, Boolean> FOOD_CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> MEAT_CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> VEGGIE_CACHE = new ConcurrentHashMap<>();

    // ================== 通用食物检测方法 ================== //
    private interface FoodPredicate {
        boolean test(ItemStack stack);
    }

    private static boolean checkFoodInventory(ServerPlayer player, FoodPredicate predicate) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEdible() && predicate.test(stack)) {
                return true;
            }
        }
        return false;
    }

    // ================== 普通食物检测 ================== //
    @SubscribeEvent
    public static void onFoodCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 20 != 0) return; // 1秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            Boolean cached = FOOD_CACHE.get(player.getUUID());
            if (cached != null && cached) {
                profiler.finishQuest("食物保持", player);
                return;
            }

            boolean hasFood = checkFoodInventory(player, stack -> true);
            if (hasFood) {
                FOOD_CACHE.put(player.getUUID(), true);
                profiler.finishQuest("获得食物", player);
            }
        }, CHALLENGE_HAS_FOOD);
    }

    // ================== 肉类检测 ================== //
    @SubscribeEvent
    public static void onMeatCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 20 != 0) return;

        executeChallenge(event, (player, profiler) -> {
            Boolean cached = MEAT_CACHE.get(player.getUUID());
            if (cached != null && cached) {
                profiler.finishQuest("肉类保持", player);
                return;
            }

            boolean hasMeat = checkFoodInventory(player, stack ->
            {
                FoodProperties foodProperties = stack.getFoodProperties(player);
                return foodProperties !=null && foodProperties.isMeat();
            });

            if (hasMeat) {
                MEAT_CACHE.put(player.getUUID(), true);
                profiler.finishQuest("获得肉类", player);
            }
        }, CHALLENGE_HAS_MEAT);
    }

    // ================== 素食检测 ================== //
    @SubscribeEvent
    public static void onVeggieCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 20 != 0) return;

        executeChallenge(event, (player, profiler) -> {
            Boolean cached = VEGGIE_CACHE.get(player.getUUID());
            if (cached != null && cached) {
                profiler.finishQuest("素食保持", player);
                return;
            }

            boolean hasVeggie = checkFoodInventory(player, stack ->
            {
                FoodProperties foodProperties = stack.getFoodProperties(player);
                return foodProperties!=null && !foodProperties.isMeat();
            });

            if (hasVeggie) {
                VEGGIE_CACHE.put(player.getUUID(), true);
                profiler.finishQuest("获得素食", player);
            }
        }, CHALLENGE_HAS_VEGGIE);
    }

    // ================== 缓存失效处理 ================== //
    @SubscribeEvent
    public static void onInventoryChange(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FOOD_CACHE.remove(player.getUUID());
            MEAT_CACHE.remove(player.getUUID());
            VEGGIE_CACHE.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        FOOD_CACHE.remove(uuid);
        MEAT_CACHE.remove(uuid);
        VEGGIE_CACHE.remove(uuid);
    }


    // ================ 生命值状态检测 ================ //
    private static final Map<UUID, Float> HEALTH_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onHealthCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 20 != 0) return; // 1秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();
            float threshold = maxHealth * 0.5f;

            // 获取上次缓存值
            Float lastHealth = HEALTH_CACHE.get(player.getUUID());
            boolean stateChanged = lastHealth == null ||
                    (currentHealth <= threshold) != (lastHealth <= threshold);

            if (stateChanged) {
                if (currentHealth <= threshold) {
                    profiler.finishQuest("生命值低下", player);
                }
                HEALTH_CACHE.put(player.getUUID(), currentHealth);
            }
        }, CHALLENGE_HEALTH_LESS_HALF);
    }

    // ================ 饱和度检测 ================ //
    private static final Map<UUID, Boolean> SATURATION_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onSaturationCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            float saturation = player.getFoodData().getSaturationLevel();
            boolean hasSaturation = saturation > 0.0f;

            // 获取上次缓存状态
            Boolean lastState = SATURATION_CACHE.get(player.getUUID());
            if (lastState != null && lastState == hasSaturation) {
                profiler.finishQuest("饱和度保持", player);
                return;
            }

            if (hasSaturation) {
                SATURATION_CACHE.put(player.getUUID(), true);
                profiler.finishQuest("获得饱和度", player);
            }
        }, CHALLENGE_HAS_SATURATION);
    }

    // ================ 事件响应优化 ================ //
    @SubscribeEvent
    public static void onHealthChange(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 生命值变化时立即更新缓存
            HEALTH_CACHE.put(player.getUUID(), player.getHealth());
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 治疗时更新缓存
            HEALTH_CACHE.put(player.getUUID(), player.getHealth());
        }
    }

    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect4(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        HEALTH_CACHE.remove(uuid);
        SATURATION_CACHE.remove(uuid);
    }



    // ================ 功能方块检测 ================ //
    private static final Map<UUID, Boolean> BLOCK_CACHE = new ConcurrentHashMap<>();
    //private static final TagKey<Block> FUNCTIONAL_BLOCKS = BlockTags.create(new ResourceLocation("forge", "functional_blocks"));

    private static List<Item> fur_blocks = List.of(
            Items.FURNACE, Items.BLAST_FURNACE, Items.SMOKER, Items.CRAFTING_TABLE, Items.FLETCHING_TABLE, Items.LOOM, Items.CARTOGRAPHY_TABLE, Items.SMITHING_TABLE, Items.GRINDSTONE, Items.STONECUTTER, Items.BARREL, Items.CHEST, Items.TRAPPED_CHEST, Items.ENDER_CHEST, Items.BELL, Items.COMPOSTER, Items.SMOKER, Items.CAMPFIRE, Items.SOUL_CAMPFIRE, Items.LODESTONE, Items.RESPAWN_ANCHOR, Items.ENCHANTING_TABLE, Items.BREWING_STAND, Items.BEACON, Items.COMPARATOR, Items.DAYLIGHT_DETECTOR, Items.HOPPER, Items.DROPPER, Items.DISPENSER, Items.NOTE_BLOCK, Items.JUKEBOX, Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX, Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX
    );
    @SubscribeEvent
    public static void onFunctionalBlockCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            BlockPos center = player.blockPosition();
            boolean hasBlock = BlockPos.betweenClosedStream(center.offset(-4, -2, -4), center.offset(4, 2, 4))
                    .anyMatch(pos -> fur_blocks.contains(player.level().getBlockState(pos).getBlock().asItem()));

            if (hasBlock) {
                profiler.finishQuest("附近有功能方块", player);
                BLOCK_CACHE.put(player.getUUID(), true);
            } else {
                BLOCK_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_NEAR_BLOCK);
    }

    // ================ 受伤检测 ================ //
    private static final Map<UUID, Long> LAST_DAMAGE_TIME = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LAST_DAMAGE_TIME.put(player.getUUID(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void validateDamage(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            Long lastDamage = LAST_DAMAGE_TIME.get(player.getUUID());
            boolean hasDamaged = lastDamage != null &&
                    (System.currentTimeMillis() - lastDamage) < 5000; // 5秒内

            if (hasDamaged) {
                profiler.finishQuest("受到伤害", player);
            }
        }, CHALLENGE_TAKE_DAMAGE);
    }

    // ================ 放置物品检测 ================ //
    private static final Map<UUID, Long> LAST_PLACE_TIME = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LAST_PLACE_TIME.put(player.getUUID(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void validatePlacement(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            Long lastPlace = LAST_PLACE_TIME.get(player.getUUID());
            boolean hasPlaced = lastPlace != null &&
                    (System.currentTimeMillis() - lastPlace) < 3000; // 3秒内

            if (hasPlaced) {
                profiler.finishQuest("放置物品", player);
            }
        }, CHALLENGE_PLACE_ITEM);
    }

    // ================ 水域检测 ================ //
    private static final Map<UUID, Boolean> WATER_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onWaterCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 60 != 0) return; // 3秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            BlockPos pos = player.blockPosition();
            boolean nearWater = BlockPos.betweenClosedStream(pos.offset(-3, -1, -3), pos.offset(3, 1, 3))
                    .anyMatch(p -> player.level().getFluidState(p).is(FluidTags.WATER));

            if (nearWater) {
                profiler.finishQuest("附近有水", player);
                WATER_CACHE.put(player.getUUID(), true);
            } else {
                WATER_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_NEAR_WATER);
    }

    // ================ 击杀检测 ================ //
    private static final Map<UUID, Long> LAST_KILL_TIME = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onMobKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LAST_KILL_TIME.put(player.getUUID(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void validateKill(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            Long lastKill = LAST_KILL_TIME.get(player.getUUID());
            boolean hasKilled = lastKill != null &&
                    (System.currentTimeMillis() - lastKill) < 10000; // 10秒内

            if (hasKilled) {
                profiler.finishQuest("击杀生物", player);
            }
        }, CHALLENGE_KILL_MOB);
    }



    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect3(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        BLOCK_CACHE.remove(uuid);
        LAST_DAMAGE_TIME.remove(uuid);
        LAST_PLACE_TIME.remove(uuid);
        WATER_CACHE.remove(uuid);
        LAST_KILL_TIME.remove(uuid);
    }
    // ================ 地狱门检测 ================ //
    private static final Map<UUID, Boolean> PORTAL_CACHE = new ConcurrentHashMap<>();
    private static final int PORTAL_SCAN_RANGE = 5;

    @SubscribeEvent
    public static void onPortalCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 3秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            BlockPos center = player.blockPosition();
            boolean nearPortal = BlockPos.betweenClosedStream(
                            center.offset(-PORTAL_SCAN_RANGE, -2, -PORTAL_SCAN_RANGE),
                            center.offset(PORTAL_SCAN_RANGE, 2, PORTAL_SCAN_RANGE))
                    .anyMatch(pos -> player.level().getBlockState(pos).getBlock() instanceof NetherPortalBlock);

            if (nearPortal) {
                profiler.finishQuest("附近有地狱门", player);
                PORTAL_CACHE.put(player.getUUID(), true);
            } else {
                PORTAL_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_NEAR_PORTAL);
    }

    // ================ 丢弃物品检测 ================ //
    private static final Map<UUID, Long> LAST_DROP_TIME = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onItemDrop(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            LAST_DROP_TIME.put(player.getUUID(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void validateDrop(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            Long lastDrop = LAST_DROP_TIME.get(player.getUUID());
            boolean hasDropped = lastDrop != null &&
                    (System.currentTimeMillis() - lastDrop) < 3000; // 3秒内有效

            if (hasDropped) {
                profiler.finishQuest("丢弃物品", player);
            }
        }, CHALLENGE_DROP_ITEM);
    }

    // ================ 苹果检测 ================ //
    private static final Map<UUID, Boolean> APPLE_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onAppleCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            boolean hasApple = player.getInventory().hasAnyOf(Set.of(Items.APPLE));

            if (hasApple) {
                profiler.finishQuest("拥有苹果", player);
                APPLE_CACHE.put(player.getUUID(), true);
            } else {
                APPLE_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_HAS_APPLE);
    }

    // ================ 头顶空间检测 ================ //
    private static final Map<UUID, Boolean> CEILING_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onCeilingCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 10 != 0) return; // 0.5秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            // 检测头顶10格范围内是否有遮挡
            boolean hasCeiling = false;
            for (int i = 1; i <= 10; i++) {
                BlockPos pos = player.blockPosition().above(i);
                if (!player.level().isEmptyBlock(pos)) {
                    hasCeiling = true;
                    break;
                }
            }

            if (!hasCeiling) {
                profiler.finishQuest("头顶无遮挡", player);
                CEILING_CACHE.put(player.getUUID(), true);
            } else {
                CEILING_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_NO_CEILING);
    }


    // ================ 玩家攻击检测 ================ //
    private static final Map<UUID, Long> LAST_ATTACK_TIME = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer attacker &&
                event.getTarget() instanceof ServerPlayer target) {
            LAST_ATTACK_TIME.put(attacker.getUUID(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void validateAttack(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            Long lastAttack = LAST_ATTACK_TIME.get(player.getUUID());
            boolean hasAttacked = lastAttack != null &&
                    (System.currentTimeMillis() - lastAttack) < 5000; // 5秒内有效

            if (hasAttacked) {
                profiler.finishQuest("攻击玩家", player);
            }
        }, CHALLENGE_ATTACK_PLAYER);
    }



    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect2(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        PORTAL_CACHE.remove(uuid);
        LAST_DROP_TIME.remove(uuid);
        APPLE_CACHE.remove(uuid);
        CEILING_CACHE.remove(uuid);
        LAST_ATTACK_TIME.remove(uuid);
    }
    // ================ 绿宝石检测 ================ //
    private static final Map<UUID, Boolean> EMERALD_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEmeraldCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            boolean hasEmerald = player.getInventory().hasAnyOf(Set.of(Items.EMERALD));

            if (hasEmerald) {
                profiler.finishQuest("获得绿宝石", player);
                EMERALD_CACHE.put(player.getUUID(), true);
            } else {
                EMERALD_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_HAS_EMERALD);
    }

    // ================ 生物存在检测 ================ //
    private static final Map<UUID, Boolean> MOB_CACHE = new ConcurrentHashMap<>();
    private static final double MOB_DETECT_RANGE = 8.0;

    @SubscribeEvent
    public static void onMobProximityCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 60 != 0) return; // 3秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            List<LivingEntity> entities = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(MOB_DETECT_RANGE),
                    e -> !(e instanceof Player) && e.isAlive()
            );

            boolean hasMob = !entities.isEmpty();
            if (hasMob) {
                profiler.finishQuest("附近有生物", player);
                MOB_CACHE.put(player.getUUID(), true);
            } else {
                MOB_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_NEAR_MOB);
    }

    // ================ 岩浆检测 ================ //
    private static final Map<UUID, Boolean> LAVA_CACHE = new ConcurrentHashMap<>();
    private static final int LAVA_SCAN_RADIUS = 3;

    @SubscribeEvent
    public static void onLavaCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 60 != 0) return; // 3秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            BlockPos center = player.blockPosition();
            boolean nearLava = BlockPos.betweenClosedStream(
                            center.offset(-LAVA_SCAN_RADIUS, -1, -LAVA_SCAN_RADIUS),
                            center.offset(LAVA_SCAN_RADIUS, 1, LAVA_SCAN_RADIUS))
                    .anyMatch(pos -> player.level().getFluidState(pos).is(FluidTags.LAVA));

            if (nearLava) {
                profiler.finishQuest("附近有岩浆", player);
                LAVA_CACHE.put(player.getUUID(), true);
            } else {
                LAVA_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_NEAR_LAVA);
    }



    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect7(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        EMERALD_CACHE.remove(uuid);
        MOB_CACHE.remove(uuid);
        LAVA_CACHE.remove(uuid);
    }
    // ================ 后退检测 ================ //
    private static final Map<UUID, Vec3> POSITION_HISTORY = new ConcurrentHashMap<>();
    private static final double BACKWARD_THRESHOLD = 0.05; // 每tick至少移动0.03格

    @SubscribeEvent
    public static void onBackwardCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 5 != 0) return; // 每0.25秒检测

        executeChallenge(event, (player, profiler) -> {
            Vec3 currentPos = player.position();
            Vec3 lastPos = POSITION_HISTORY.getOrDefault(player.getUUID(), currentPos);

            // 计算移动方向与视角的夹角
            float yaw = player.getYRot();
            Vec3 moveDir = new Vec3(
                    Mth.sin(-yaw * 0.017453292F),
                    0,
                    Mth.cos(yaw * 0.017453292F)
            ).normalize();

            Vec3 actualMove = currentPos.subtract(lastPos);
            double dotProduct = moveDir.dot(actualMove.normalize());

            boolean isMovingBack = dotProduct < -0.7 && actualMove.lengthSqr() > BACKWARD_THRESHOLD;
            POSITION_HISTORY.put(player.getUUID(), currentPos);

            if (isMovingBack) {
                profiler.finishQuest("持续后退", player);
            }
        }, CHALLENGE_HOLD_S);
    }

    // ================ 武器持有检测 ================ //
    private static final Map<UUID, Boolean> WEAPON_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onWeaponCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 20 != 0) return; // 1秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            ItemStack mainHand = player.getMainHandItem();
            boolean isWeapon = mainHand.getItem() instanceof SwordItem ||
                    mainHand.getItem() instanceof AxeItem ||
                    mainHand.getItem() instanceof TridentItem;

            Boolean cached = WEAPON_CACHE.get(player.getUUID());
            if (cached != null && cached == isWeapon) {
                if (isWeapon) profiler.finishQuest("保持持武", player);
                return;
            }

            if (isWeapon) {
                profiler.finishQuest("手持武器", player);
                WEAPON_CACHE.put(player.getUUID(), true);
            } else {
                WEAPON_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_HOLD_WEAPON);
    }

    // ================ 深度检测 ================ //
    private static final Map<UUID, Boolean> DEPTH_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onDepthCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 10 != 0) return; // 0.5秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            boolean isBelow = player.getY() < 30.0;
            Boolean cached = DEPTH_CACHE.get(player.getUUID());

            if (cached != null && cached == isBelow) {
                if (isBelow) profiler.finishQuest("保持深度", player);
                return;
            }

            if (isBelow) {
                profiler.finishQuest("抵达深层", player);
                DEPTH_CACHE.put(player.getUUID(), true);
            } else {
                DEPTH_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_BELOW_Y30);
    }



    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect8(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        POSITION_HISTORY.remove(uuid);
        WEAPON_CACHE.remove(uuid);
        DEPTH_CACHE.remove(uuid);
    }
    // ================ 多人聚集检测 ================ //
    private static final Map<UUID, Long> GROUP_CACHE = new ConcurrentHashMap<>();
    private static final double GROUP_RANGE = 6.0;

    @SubscribeEvent
    public static void onGroupCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            List<ServerPlayer> players = player.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    player.getBoundingBox().inflate(GROUP_RANGE),
                    p -> p != player
            );

            if (players.size() >= 2) {
                profiler.finishQuest("多人聚集", player);
                GROUP_CACHE.put(player.getUUID(), System.currentTimeMillis());
            } else if (GROUP_CACHE.containsKey(player.getUUID())) {
                GROUP_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_DONW_JIYAN);
    }

    // ================ 高空检测 ================ //
    private static final Map<UUID, Boolean> ALTITUDE_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onHighAltitudeCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 10 != 0) return; // 0.5秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            boolean isHigh = player.getY() > 120.0;
            Boolean cached = ALTITUDE_CACHE.get(player.getUUID());

            if (cached != null && cached == isHigh) {
                if (isHigh) profiler.finishQuest("维持高度", player);
                return;
            }

            if (isHigh) {
                profiler.finishQuest("抵达高空", player);
                ALTITUDE_CACHE.put(player.getUUID(), true);
            } else {
                ALTITUDE_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_CC_Y30);
    }

    // ================ 基岩站立检测 ================ //
    private static final Map<UUID, Long> BEDROCK_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onBedrockCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 20 != 0) return; // 1秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            BlockPos standingPos = player.blockPosition().below();
            boolean isOnBedrock = player.level().getBlockState(standingPos).is(Blocks.BEDROCK);

            if (isOnBedrock) {
                profiler.finishQuest("基岩立足", player);
                BEDROCK_CACHE.put(player.getUUID(), System.currentTimeMillis());
            } else if (BEDROCK_CACHE.containsKey(player.getUUID())) {
                BEDROCK_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_CAR);
    }

    // ================ 光照检测 ================ //
    private static final Map<UUID, Boolean> LIGHT_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onLightCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 15 != 0) return; // 0.75秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            BlockPos pos = player.blockPosition();
            int lightLevel = player.level().getMaxLocalRawBrightness(pos);

            if (lightLevel > 11) {
                profiler.finishQuest("高亮区域", player);
                LIGHT_CACHE.put(player.getUUID(), true);
            } else if (LIGHT_CACHE.containsKey(player.getUUID())) {
                LIGHT_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_PPIC);
    }



    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect9(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        GROUP_CACHE.remove(uuid);
        ALTITUDE_CACHE.remove(uuid);
        BEDROCK_CACHE.remove(uuid);
        LIGHT_CACHE.remove(uuid);
    }
    // ================ 饮水检测 ================ //
    private static final Map<UUID, Long> DRINK_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onDrinkWater(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player &&
                event.getItem().getItem() == Items.POTION &&
                PotionUtils.getPotion(event.getItem()) == Potions.WATER) {
            DRINK_CACHE.put(player.getUUID(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void validateDrinking(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            Long lastDrink = DRINK_CACHE.get(player.getUUID());
            boolean hasDrunk = lastDrink != null &&
                    (System.currentTimeMillis() - lastDrink) < 3000; // 3秒内有效

            if (hasDrunk) {
                profiler.finishQuest("完成饮水", player);
            }
        }, CHALLENGE_CUMOP);
    }

    // ================ 玩家击杀检测 ================ //
    private static final Map<UUID, Long> KILL_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer killer &&
                event.getEntity() instanceof ServerPlayer victim) {
            KILL_CACHE.put(killer.getUUID(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void validateKill9(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            Long lastKill = KILL_CACHE.get(player.getUUID());
            boolean hasKilled = lastKill != null &&
                    (System.currentTimeMillis() - lastKill) < 5000; // 5秒内有效

            if (hasKilled) {
                profiler.finishQuest("击杀玩家", player);
            }
        }, CHALLENGE_CCCCCC);
    }

    // ================ 被攻击次数统计 ================ //
    private static final Map<UUID, AtomicInteger> ATTACK_COUNTER = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerAttack3(AttackEntityEvent event) {
        if (event.getTarget() instanceof ServerPlayer target &&
                event.getEntity() instanceof ServerPlayer attacker) {
            ATTACK_COUNTER.computeIfAbsent(target.getUUID(), k -> new AtomicInteger())
                    .incrementAndGet();
        }
    }

    @SubscribeEvent
    public static void validateAttackCount(TickEvent.PlayerTickEvent event) {
        executeChallenge(event, (player, profiler) -> {
            AtomicInteger count = ATTACK_COUNTER.get(player.getUUID());
            if (count != null && count.get() >= 5) {
                profiler.finishQuest("承受攻击", player);
                count.set(0); // 重置计数器
            }
        }, CHALLENGE_AAAAAAA);
    }

    // ================ 玩家贴贴检测 ================ //
    private static final Map<UUID, Long> HUG_CACHE = new ConcurrentHashMap<>();
    private static final double HUG_DISTANCE = 0.5; // 0.5格距离

    @SubscribeEvent
    public static void onHugCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 35 != 0) return; // 1.5秒检测间隔

        executeChallenge(event, (player, profiler) -> {
            List<ServerPlayer> nearby = player.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    player.getBoundingBox().inflate(HUG_DISTANCE),
                    p -> p != player
            );

            if (!nearby.isEmpty()) {
                profiler.finishQuest("玩家贴贴", player);
                HUG_CACHE.put(player.getUUID(), System.currentTimeMillis());
            } else if (HUG_CACHE.containsKey(player.getUUID())) {
                HUG_CACHE.remove(player.getUUID());
            }
        }, CHALLENGE_HHHHHHH);
    }
    // 存储玩家受伤前的生命值

    // ================ 伤害预检测 ================


    // ================ 实际伤害检测 ================
    @SubscribeEvent
    public static void onPostDamage(LivingHurtEvent event) {
        executeChallenge(
                event,
                (player, profiler) -> {
                    if (event.getAmount() > 10.0f) {
                        profiler.finishQuest("承受巨额伤害", player);
                    }
                },
                CHALLENGE_XXXHHHH
        );

//        if (event.getEntity() instanceof ServerPlayer player) {
//            UUID uuid = player.getUUID();
//            Float preHealth = PRE_DAMAGE_HEALTH.get(uuid);
//
//            if (preHealth != null) {
//                // 计算实际损失的生命值（包括吸收护盾的消耗）
//                float postHealth = player.getHealth() + player.getAbsorptionAmount();
//                float damageTaken = preHealth - postHealth;
//
//                // 清理缓存（无论是否触发都移除）
//                PRE_DAMAGE_HEALTH.remove(uuid);
//            }
//        }
    }
    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        DRINK_CACHE.remove(uuid);
        KILL_CACHE.remove(uuid);
        ATTACK_COUNTER.remove(uuid);
        HUG_CACHE.remove(uuid);
    }
}





