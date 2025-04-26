package net.exmo.esm.content;

import net.exmo.esm.util.AttrGether;
import net.exmo.esm.util.EntityAttrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class ESMGameEventHandle {
    public static Map<String, ESMGameEvent<?>> events = new HashMap<>();

    public static ESMGameEvent<?> findEvent(String id) {
        return events.get(id);
    }

    public static final ESMGameEvent<TickEvent.PlayerTickEvent> ANVIL_TIME = new ESMGameEvent<>("ANVIL_TIME", TickEvent.PlayerTickEvent.class).time(30).name("天降正义").describe("玩家头上将持续掉落铁砧（挖掘后不掉落）").appearType(ESMGameEvent.AppearType.EVENT).appear(
            (serverLevel, event) -> {
                if (GameProcess.CommonEvent.tick % 80 == 0) {
                    Player player = event.player;
                    // 获取玩家的位置
                                BlockPos playerPos = player.blockPosition();
                                // 计算铁砧放置的位置（玩家头顶上方10格）
                                BlockPos anvilPos = playerPos.above(10);

                                // 创建铁砧实体
                                FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(serverLevel, anvilPos, Blocks.ANVIL.defaultBlockState());

                                // 设置铁砧实体无视碰撞
                                fallingBlockEntity.setHurtsEntities(2, 5);

                                //fallingBlockEntity.noPhysics =true;
                                fallingBlockEntity.setDeltaMovement(0, -1, 0);

                                // 将铁砧实体添加到世界中
                                //        serverLevel.addFreshEntity(fallingBlockEntity);
                }

            }
    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> LOSE_WEIGHT = new ESMGameEvent<>("LOSE_WEIGHT", TickEvent.PlayerTickEvent.class).time(50).name("重力失常").describe("你的重力将会失常").appearType(ESMGameEvent.AppearType.EVENT).appear(
            (serverLevel, event) -> {
                if (GameProcess.CommonEvent.tick % 80 == 0) {
                    Player player = event.player;
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 3));
                }

            }
    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> LIGHTING_TIME =
            new ESMGameEvent<>("LIGHTING_TIME", TickEvent.PlayerTickEvent.class)
                    .time(40)
                    .name("闪电时刻")
                    .describe("玩家将被雷电击中！")
                    .appearType(ESMGameEvent.AppearType.EVENT)
                    .appear((serverLevel, event) -> {
                        if (GameProcess.CommonEvent.tick % 100 == 0 && Math.random() <= 0.3f) {
                            Player player = event.player;
                            // 获取玩家所在坐标的最高表面方块Y值
                            int surfaceY = serverLevel.getHeight(
                                    Heightmap.Types.MOTION_BLOCKING,
                                    (int) player.getX(),
                                    (int) player.getZ()
                            );

                            // 在最高表面方块上方2格生成闪电
                            LightningBolt lightningBolt = new LightningBolt(
                                    EntityType.LIGHTNING_BOLT,
                                    serverLevel
                            );
                            lightningBolt.setPos(
                                    player.getX(),
                                    surfaceY ,  // 在表面上方2格
                                    player.getZ()
                            );
                            lightningBolt.setDamage(2f);
                            serverLevel.addFreshEntity(lightningBolt);
                        }
                    });

    public static final ESMGameEvent<LivingEvent.LivingTickEvent> FREEZE_TIME = new ESMGameEvent<>("FREEZE_TIME", LivingEvent.LivingTickEvent.class).time(100).name("冰冻风扇").describe("一切非玩家实体将冻结").appearType(ESMGameEvent.AppearType.EVENT).appear(
            (serverLevel, event) -> {
                LivingEntity entity = event.getEntity();
                entity.setDeltaMovement(0, 0, 0);
            }
    );
    public static final ESMGameEvent<LivingEvent.LivingTickEvent> NONE = new ESMGameEvent<>("NONE", LivingEvent.LivingTickEvent.class).weight(70).time(50).name("无事件").describe("当前无事件").appearType(ESMGameEvent.AppearType.NONE).appear(
            (serverLevel, event) -> {

            }
    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> HEALING_RAIN =
            new ESMGameEvent<>("HEALING_RAIN", TickEvent.PlayerTickEvent.class)
                    .time(30).weight(25).name("治愈之雨").describe("雨中恢复生命但消耗饥饿")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 80 == 0) {
                                    Player player = event.player;
                                    if (serverLevel.isRainingAt(player.blockPosition())) {
                                        // 生命恢复
                                        player.heal(2.0f);
                                        // 增加饥饿消耗
                                        player.causeFoodExhaustion(1.5f);
                                    }
                                }
                            }
                    );

    public static final ESMGameEvent<TickEvent.PlayerTickEvent> SPEED_BOOST =
            new ESMGameEvent<>("SPEED_BOOST", TickEvent.PlayerTickEvent.class)
                    .time(40).weight(20).name("速度狂热").describe("获得加速但降低防御")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                Player player = event.player;
                                // 速度提升II
                                player.addEffect(new MobEffectInstance(
                                        MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
                                // 抗性下降I
                                player.addEffect(new MobEffectInstance(
                                        MobEffects.DAMAGE_RESISTANCE, 40, -1, false, false));
                            }
                    );
    // 环境类事件
//    public static final ESMGameEvent<TickEvent.PlayerTickEvent> LAVA_SURGE =
//            new ESMGameEvent<>("LAVA_SURGE", TickEvent.PlayerTickEvent.class)
//                    .time(45).weight(15).name("熔岩喷发").describe("随机位置生成流动岩浆柱（持续5秒）")
//                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
//                            (serverLevel, event) -> {
//                                if (GameProcess.CommonEvent.tick % 100 == 0) {
//                                    Player player = event.player;
//                                    BlockPos basePos = player.blockPosition().offset(
//                                            ThreadLocalRandom.current().nextInt(-12, 12),
//                                            0,
//                                            ThreadLocalRandom.current().nextInt(-12, 12)
//                                    );
//
//                                    // 生成3格高的岩浆柱
//                                    for (int y = 0; y < 3; y++) {
//                                        BlockPos pos = basePos.above(y);
//                                        if (serverLevel.getBlockState(pos).isAir()) {
//                                            serverLevel.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
//                                            serverLevel.scheduleTick(pos, Blocks.LAVA, 80); // 4秒后消失
//                                        }
//                                    }
//                                }
//                            }
//                    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> NATURE_HARMONY =
            new ESMGameEvent<>("NATURE_HARMONY", TickEvent.PlayerTickEvent.class)
                    .time(60).weight(18).name("自然共鸣").describe("植物加速生长但土地枯竭")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 40 == 0) {
                                    BlockPos.betweenClosedStream(
                                                    event.player.blockPosition().offset(-10, -3, -10),
                                                    event.player.blockPosition().offset(10, 5, 10))
                                            .forEach(pos -> {
                                                BlockState state = serverLevel.getBlockState(pos);
                                                // 加速作物生长
                                                if (state.getBlock() instanceof CropBlock crop) {
                                                    serverLevel.setBlock(pos, crop.getStateForAge(
                                                            Math.min(crop.getMaxAge(), crop.getAge(state) + 1)), 3);
                                                }
                                                // 随机破坏草方块
                                                if (state.is(Blocks.GRASS_BLOCK) &&
                                                        ThreadLocalRandom.current().nextFloat() < 0.05f) {
                                                    serverLevel.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                                                }
                                            });
                                }
                            }
                    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> QUANTUM_ENTANGLEMENT =
            new ESMGameEvent<>("QUANTUM_ENTANGLEMENT", TickEvent.PlayerTickEvent.class)
                    .time(60).weight(12).name("量子纠缠").describe("随机传送生物但获得瞬移保护")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 80 == 0) {
                                    // 传送半径内生物
                                    serverLevel.getEntitiesOfClass(LivingEntity.class,
                                                    new AABB(event.player.blockPosition()).inflate(15)).stream()
                                            .filter(e -> !(e instanceof Player))
                                            .forEach(entity -> {
                                                BlockPos newPos = entity.blockPosition().offset(
                                                        ThreadLocalRandom.current().nextInt(-8, 8),
                                                        0,
                                                        ThreadLocalRandom.current().nextInt(-8, 8)
                                                );
                                                entity.teleportTo(
                                                        newPos.getX() + 0.5,
                                                        serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, newPos.getX(),newPos.getZ()),
                                                        newPos.getZ() + 0.5
                                                );
                                            });

                                    // 玩家传送保护
                                    event.player.addEffect(new MobEffectInstance(
                                            MobEffects.DAMAGE_RESISTANCE, 100, 2, false, true));
                                }
                            }
                    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> GRAVITY_WELL =
            new ESMGameEvent<>("GRAVITY_WELL", TickEvent.PlayerTickEvent.class)
                    .time(45).weight(20).name("重力井").describe("物品自动聚集但实体移动困难")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                event.player.addEffect(new MobEffectInstance(
                                        MobEffects.MOVEMENT_SLOWDOWN, 10, 2, false, true));
                                // 物品吸引逻辑
                                if (GameProcess.CommonEvent.tick % 5 == 0) {
                                    serverLevel.getEntitiesOfClass(ItemEntity.class,
                                                    new AABB(event.player.blockPosition()).inflate(20))
                                            .forEach(item -> {
                                                Vec3 motion = event.player.position()
                                                        .subtract(item.position())
                                                        .normalize()
                                                        .scale(0.4);
                                                item.setDeltaMovement(motion);
                                            });
                                }

                                // 实体移动阻力
                                serverLevel.getEntitiesOfClass(LivingEntity.class,
                                                new AABB(event.player.blockPosition()).inflate(12))
                                        .forEach(entity -> {
                                            if (!(entity instanceof Player)) {
                                                Vec3 motion = entity.getDeltaMovement();
                                                entity.setDeltaMovement(motion.x * 0.6, motion.y, motion.z * 0.6);
                                            }
                                            });
                            }
                    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> CHAOS_ALCHEMY =
            new ESMGameEvent<>("CHAOS_ALCHEMY", TickEvent.PlayerTickEvent.class)
                    .time(75).weight(10).name("混沌炼金").describe("随机转换物品但可能获得稀有材料")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 100 == 0) {
                                    Player player = event.player;
                                    // 背包物品转换
                                    for (int i = 0; i < 36; i++) {
                                        if (ThreadLocalRandom.current().nextFloat() < 0.15f) {
                                            ItemStack original = player.getInventory().getItem(i);
                                            if (!original.isEmpty()) {
                                                ItemStack transformed = transformItem(original.copy());
                                                player.getInventory().setItem(i, transformed);
                                            }
                                        }
                                    }

                                    // 播放转化音效
                                    player.playNotifySound(
                                            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f,
                                            ThreadLocalRandom.current().nextFloat() * 0.5f + 0.8f);
                                }
                            }
                    );

    private static ItemStack transformItem(ItemStack original) {
        Map<Item, Item> conversionMap = Map.of(
                Items.IRON_INGOT, Items.IRON_BLOCK,
                Items.GOLD_INGOT, Items.DIAMOND,
                Items.COBBLESTONE, Items.OBSIDIAN,
                Items.DIRT, Items.GRASS_BLOCK,
                Items.COBBLED_DEEPSLATE, Items.POLISHED_DEEPSLATE,
                Items.APPLE,Items.ENCHANTED_GOLDEN_APPLE,
                Items.BONE, Items.BONE_BLOCK,
                Items.BAMBOO, Items.BAMBOO_BLOCK,
                Items.BEEF, Items.COOKED_BEEF,
                Items.CHICKEN, Items.COOKED_CHICKEN
        );
        return new ItemStack(
                conversionMap.getOrDefault(original.getItem(), original.getItem()),
                original.getCount());
    }
    // 状态类事件
    public static final ESMGameEvent<LivingEvent.LivingTickEvent> PHANTOM_SWARM =
            new ESMGameEvent<>("PHANTOM_SWARM", LivingEvent.LivingTickEvent.class)
                    .time(60).weight(10).name("幻影突袭").describe("夜间生成幻翼并赋予夜视能力")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (event.getEntity() instanceof Player player) {
                                    // 添加夜视效果
                                    player.addEffect(new MobEffectInstance(
                                            MobEffects.NIGHT_VISION, 200, 0, false, false));

                                    // 夜晚生成幻翼
                                    if (serverLevel.getDayTime() % 24000 > 12000 &&
                                            GameProcess.CommonEvent.tick % 400 == 0) {
                                        Phantom phantom = EntityType.PHANTOM.create(serverLevel);
                                        phantom.setPos(player.getX() + 8, player.getY() + 6, player.getZ());
                                        serverLevel.addFreshEntity(phantom);
                                    }
                                }
                            }
                    );

    // 资源类事件
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> ORE_AWAKENING =
            new ESMGameEvent<>("ORE_AWAKENING", TickEvent.PlayerTickEvent.class)
                    .time(90).weight(20).name("矿脉觉醒").describe("裸露矿石自动掉落但消耗镐子耐久")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 10 == 0) {
                                    Player player = event.player;
                                    BlockPos.betweenClosedStream(
                                                    player.blockPosition().offset(-6, -3, -6),
                                                    player.blockPosition().offset(6, 3, 6))
                                            .forEach(pos -> {
                                                ItemStack tool = player.getMainHandItem();
                                                if (tool.is(Tags.Items.TOOLS)) {
                                                    BlockState state = serverLevel.getBlockState(pos);
                                                if (state.is(Tags.Blocks.ORES)) {
                                                        if (tool.isCorrectToolForDrops(state)) {
                                                            // 自动掉落矿石
                                                            serverLevel.destroyBlock(pos, false);
                                                            //吸收矿石
                                                            serverLevel.getBlockState(pos);
                                                            Block.getDrops(state, serverLevel, pos, null).forEach(
                                                                    itemStack -> {
                                                                ItemEntity itemEntity = new ItemEntity(
                                                                        serverLevel,
                                                                        player.getX(),
                                                                        player.getY(),
                                                                        player.getZ(),
                                                                        itemStack
                                                                );
                                                                serverLevel.addFreshEntity(itemEntity);
                                                           }
                                                            );

                                                            // 消耗工具耐久
                                                            tool.hurt(3, serverLevel.random, null);
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                    );

    // 时空扭曲类事件
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> TIME_DISTORTION =
            new ESMGameEvent<>("TIME_DISTORTION", TickEvent.PlayerTickEvent.class)
                    .time(120).weight(5).name("时间扭曲").describe("作物加速生长但消耗时间")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 20 == 0) {
                                    // 加速半径8格内作物生长
                                    BlockPos.betweenClosedStream(
                                                    event.player.blockPosition().offset(-8, -2, -8),
                                                    event.player.blockPosition().offset(8, 4, 8))
                                            .forEach(pos -> {
                                                BlockState state = serverLevel.getBlockState(pos);
                                                if (state.isRandomlyTicking()) {
                                                    for (int i = 0; i < 3; i++) {
                                                        state.randomTick(serverLevel, pos,serverLevel.random);
                                                    }
                                                }
                                            });

                                    // 每5秒推进时间1小时（需要服务器指令权限）
                                    serverLevel.setDayTime(serverLevel.getDayTime() + 1000L);
                                }
                            }
                    );

//    // 物理规则类事件
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> GRAVITY_SHIFT =
            new ESMGameEvent<>("GRAVITY_SHIFT", TickEvent.PlayerTickEvent.class)
                    .time(30).weight(15).name("重力异常").describe("跳跃高度翻倍但摔落伤害增加")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                LivingEntity entity = event.player;
                                // 修改重力参数
                                if (entity instanceof ServerPlayer player) {
                                    player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), player.getDeltaMovement().multiply(1, 2.0, 1)));

                                }

                                // 摔落伤害计算
                                if (entity.fallDistance > 3) {
                                    entity.hurt(entity.damageSources().fall(),
                                            (entity.fallDistance - 3) * 0.8f);
                                }
                            }
                    );
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> MOB_SWARM =
            new ESMGameEvent<>("MOB_SWARM", TickEvent.PlayerTickEvent.class)
                    .time(50).weight(15).name("虫群危机").describe("周围随机生成敌对生物")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 200 == 0) {
                                    Player player = event.player;
                                    RandomSource random = serverLevel.random;

                                    int x = random.nextInt(-8, 8);
                                    int z = random.nextInt(-8, 8);
                                    int height = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                                    BlockPos pos = player.blockPosition().offset(
                                            x,
                                            height,
                                            z
                                    );

                                    if (Math.random() <0.5){
                                        Zombie zombie = new Zombie(EntityType.ZOMBIE, serverLevel);
                                        zombie.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                zombie
                                        );
                                    }else if (Math.random() <0.5){
                                        Skeleton skeleton = new Skeleton(EntityType.SKELETON, serverLevel);
                                        skeleton.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                skeleton
                                        );
                                    }else if (Math.random() <0.5){
                                        Creeper creeper = new Creeper(EntityType.CREEPER, serverLevel);
                                        creeper.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                creeper
                                        );
                                    }else if (Math.random() <0.5){
                                        EnderMan enderman = new EnderMan(EntityType.ENDERMAN, serverLevel);
                                        enderman.setPos(pos.getCenter());
                                    }else if (Math.random() <0.5){
                                        Ghast ghast = new Ghast(EntityType.GHAST, serverLevel);
                                        ghast.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                ghast
                                        );
                                    }else if (Math.random() <0.5){
                                        Witch witch = new Witch(EntityType.WITCH, serverLevel);
                                        witch.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                witch
                                        );
                                    }else if (Math.random() <0.5){
                                        Vex vex = new Vex(EntityType.VEX, serverLevel);
                                        vex.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                vex
                                        );
                                    }else if (Math.random() <0.5){
                                        Vindicator vindicator = new Vindicator(EntityType.VINDICATOR, serverLevel);
                                        vindicator.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                vindicator
                                        );
                                    }else if (Math.random() <0.5){
                                        Pillager pillager = new Pillager(EntityType.PILLAGER, serverLevel);
                                        pillager.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                pillager
                                        );
                                    }else {
                                        Ravager ravager = new Ravager(EntityType.RAVAGER, serverLevel);
                                        ravager.setPos(pos.getCenter());
                                        serverLevel.addFreshEntity(
                                                ravager
                                        );
                                    }
                                }
                            }
                    );

    public static final ESMGameEvent<TickEvent.PlayerTickEvent> ITEM_VORTEX =
            new ESMGameEvent<>("ITEM_VORTEX", TickEvent.PlayerTickEvent.class)
                    .time(40).weight(25).name("物品漩涡").describe("自动吸引周围物品但随机丢失物品")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 10 == 0) {
                                    Player player = event.player;
                                    // 吸引物品逻辑
                                    serverLevel.getEntitiesOfClass(ItemEntity.class,
                                                    new AABB(player.blockPosition()).inflate(12))
                                            .forEach(item -> {
                                                Vec3 motion = player.position()
                                                        .subtract(item.position())
                                                        .normalize()
                                                        .scale(0.5).add(0,0.5,0);
                                                item.setDeltaMovement(motion);
                                            });

                                    // 5%概率随机丢弃物品
                                    if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
                                        ItemStack randomItem = player.getInventory()
                                                .getItem(ThreadLocalRandom.current().nextInt(36));
                                        if (!randomItem.isEmpty()) {

                                            player.drop(randomItem.copy(), true);
                                            randomItem.shrink(1);
                                        }
                                    }
                                }
                            }
                    );
    // 元素风暴事件（风险与防护并存）
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> ELEMENTAL_STORM =
            new ESMGameEvent<>("ELEMENTAL_STORM", TickEvent.PlayerTickEvent.class)
                    .time(60).weight(12).name("元素风暴").describe("随机落雷但获得抗性提升")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 40 == 0) {
                                    Player player = event.player;
                                    // 生成闪电
                                    BlockPos strikePos = player.blockPosition().offset(
                                            ThreadLocalRandom.current().nextInt(-10, 10),
                                            0,
                                            ThreadLocalRandom.current().nextInt(-10, 10)
                                    );
                                    LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(serverLevel);
                                    if (lightningbolt != null) {
                                        ChunkPos chunkpos = serverLevel.getChunk(strikePos).getPos();
                                        int i = chunkpos.getMinBlockX();
                                        int j = chunkpos.getMinBlockZ();
                                        BlockPos blockpos = findLightningTargetAround(serverLevel.getBlockRandomPos(i, 0, j, 15),serverLevel);
                                        lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                                        serverLevel.addFreshEntity(lightningbolt);
                                    }

                                    // 给予抗性提升
                                    player.addEffect(new MobEffectInstance(
                                            MobEffects.DAMAGE_RESISTANCE, 40, 1, false, true));
                                }
                            }
                    );
    private static BlockPos findLightningTargetAround(BlockPos p_143289_,ServerLevel serverLevel) {
        BlockPos blockpos = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, p_143289_);
        Optional<BlockPos> optional = findLightningRod(blockpos,serverLevel);
        if (optional.isPresent()) {
            return (BlockPos)optional.get();
        } else {
            AABB aabb = (new AABB(blockpos, new BlockPos(blockpos.getX(), serverLevel.getMaxBuildHeight(), blockpos.getZ()))).inflate((double)3.0F);
            List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, aabb, (p_289308_) -> p_289308_ != null && p_289308_.isAlive() && serverLevel.canSeeSky(p_289308_.blockPosition()));
            if (!list.isEmpty()) {
                return ((LivingEntity)list.get(serverLevel.random.nextInt(list.size()))).blockPosition();
            } else {
                if (blockpos.getY() == serverLevel.getMinBuildHeight() - 1) {
                    blockpos = blockpos.above(2);
                }

                return blockpos;
            }
        }
    }
    private static Optional<BlockPos> findLightningRod(BlockPos p_143249_,ServerLevel serverLevel) {
        Optional<BlockPos> optional = serverLevel.getPoiManager().findClosest((p_215059_) -> p_215059_.is(PoiTypes.LIGHTNING_ROD), (p_184055_) -> p_184055_.getY() == serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, p_184055_.getX(), p_184055_.getZ()) - 1, p_143249_, 128, PoiManager.Occupancy.ANY);
        return optional.map((p_184053_) -> p_184053_.above(1));
    }

    // 时空扭曲事件（加速与代价平衡）
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> TIME_DILATION =
            new ESMGameEvent<>("TIME_DILATION", TickEvent.PlayerTickEvent.class)
                    .time(90).weight(8).name("时空扭曲").describe("动作加速但饥饿消耗加倍")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                Player player = event.player;
                                // 速度提升
                                player.addEffect(new MobEffectInstance(
                                        MobEffects.DIG_SPEED, 40, 2, false, false));
                                player.addEffect(new MobEffectInstance(
                                        MobEffects.MOVEMENT_SPEED, 40, 1, false, false));

                                // 饥饿加速
                                if (player.tickCount % 20 == 0) {
                                    player.causeFoodExhaustion(0.1f);
                                }
                            }
                    );

    // 大地震颤事件（环境改变类）
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> EARTH_TREMOR =
            new ESMGameEvent<>("EARTH_TREMOR", TickEvent.PlayerTickEvent.class)
                    .time(45).weight(18).name("大地震颤").describe("地面随机生成裂缝与矿石")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (GameProcess.CommonEvent.tick % 80 == 0) {
                                    BlockPos center = event.player.blockPosition();
                                    BlockPos.betweenClosedStream(center.offset(-8, -3, -8), center.offset(8, 0, 8))
                                            .filter(pos -> ThreadLocalRandom.current().nextFloat() < 0.15f)
                                            .forEach(pos -> {
                                                if (serverLevel.getBlockState(pos).isSolid()) {
                                                    // 30%概率生成矿石，70%生成裂缝（空气）
                                                    if (ThreadLocalRandom.current().nextFloat() < 0.1f) {
                                                        if (ThreadLocalRandom.current().nextFloat() < 0.1f) {
                                                            serverLevel.setBlock(pos, Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), 3);
                                                        } else if (ThreadLocalRandom.current().nextFloat() < 0.1f) {
                                                            serverLevel.setBlock(pos, Blocks.COPPER_ORE.defaultBlockState(), 3);
                                                        } else if (ThreadLocalRandom.current().nextFloat() < 0.1f) {
                                                            serverLevel.setBlock(pos, Blocks.IRON_ORE.defaultBlockState(), 3);
                                                        }else if (ThreadLocalRandom.current().nextFloat() < 0.1f) {
                                                            serverLevel.setBlock(pos, Blocks.GOLD_ORE.defaultBlockState(), 3);
                                                        } else if (ThreadLocalRandom.current().nextFloat() < 0.1f) {
                                                            serverLevel.setBlock(pos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
                                                        } else {
                                                            serverLevel.setBlock(pos, Blocks.REDSTONE_ORE.defaultBlockState(), 3);
                                                        }

                                                    } else {
                                                        if (!serverLevel.getBlockState(pos).is(Tags.Blocks.ORES)) serverLevel.destroyBlock(pos, false);
                                                    }
                                                }
                                            });
                                }
                            }
                    );

    // 幻影侵袭事件（夜间专属事件）
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> PHANTOM_INCURSION =
            new ESMGameEvent<>("PHANTOM_INCURSION", TickEvent.PlayerTickEvent.class)
                    .time(120).weight(6).name("幻影侵袭").describe("夜间生成幻翼群但获得夜视能力")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                if (serverLevel.getDayTime() % 24000 > 12000) {
                                    Player player = event.player;
                                    // 每5秒生成2-3只幻翼
                                    if (GameProcess.CommonEvent.tick % 100 == 0) {
                                        int count = 2 + ThreadLocalRandom.current().nextInt(2);
                                        for (int i = 0; i < count; i++) {
                                            Phantom phantom = EntityType.PHANTOM.create(serverLevel);
                                            phantom.setPos(player.getX() + 8, player.getY() + 6, player.getZ());
                                            serverLevel.addFreshEntity(phantom);
                                        }
                                    }
                                    // 持续夜视效果
                                    player.addEffect(new MobEffectInstance(
                                            MobEffects.NIGHT_VISION, 220, 0, true, false));
                                }
                            }
                    );

    // 能量过载事件（高风险高回报）
    public static final ESMGameEvent<TickEvent.PlayerTickEvent> POWER_SURGE =
            new ESMGameEvent<>("POWER_SURGE", TickEvent.PlayerTickEvent.class)
                    .time(30).weight(10).name("能量过载").describe("攻击力翻倍但持续受到伤害")
                    .appearType(ESMGameEvent.AppearType.EVENT).appear(
                            (serverLevel, event) -> {
                                Player player = event.player;
                                // 攻击强化
                                player.addEffect(new MobEffectInstance(
                                        MobEffects.DAMAGE_BOOST, 40, 2, false, true));
                                // 持续伤害
                                if (player.tickCount % 40 == 0) {
                                    player.hurt(player.damageSources().magic(), 2.0f);
                                }
                            }
                    );

}
