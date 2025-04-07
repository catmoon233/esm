package net.exmo.esm.event;

import net.exmo.esm.content.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;



@Mod.EventBusSubscriber
public class OutEvent {
    public static void executeEvent(EntityEvent event, BiConsumer<Player, PlayerGameProfiler> playerConsumer, GameChallenge challenge) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (GameProcess.getProfiler(player).challenge.equals(challenge.id))
                playerConsumer.accept(player, GameProcess.getProfiler(player));
        }
    }

    public static void executeEventD(EntityEvent event, BiConsumer<Player, PlayerGameProfiler> playerConsumer, GameDontDoChallenge dontDoChallenge) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (GameProcess.getProfiler(player).dontDoChallenge.equals(dontDoChallenge.id))
                playerConsumer.accept(player, GameProcess.getProfiler(
                        player));
        }
    }
//    @SubscribeEvent
//    public static void event(LivingEvent.LivingJumpEvent event) {
//       executeEventD(event,(player,playerGameProfiler)->{
//           playerGameProfiler.getDontDoChallenge().fail(Component.literal("Jump"));
//       }, GameDontDoChallengeHandle.DON_JUMP);
//    }
    @Mod.EventBusSubscriber
    public static class ChallengeEventHandlers {
    // 通用事件处理器
    private static void executeDontChallenge(EntityEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }
    private static void executeDontChallenge(LivingAttackEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }

    private static void executeDontChallenge(ItemTossEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }
    private static void executeDontChallenge(LivingDeathEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }

    private static void executeDontChallenge(BlockEvent.BreakEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }

    private static void executeDontChallenge(BlockEvent.EntityPlaceEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }

    private static void executeDontChallenge(LivingHurtEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }

    private static void executeDontChallenge(TickEvent.PlayerTickEvent event, BiConsumer<ServerPlayer, PlayerGameProfiler> action, GameDontDoChallenge challenge) {
        if (event.player instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(challenge.id())) {
                action.accept(player, profiler);
            }
        }
    }

    // 跳跃挑战
//    @SubscribeEvent
//    public static void onJump(LivingEvent.LivingJumpEvent event) {
//        executeDontChallenge(event, (player, profiler) -> {
//            profiler.recordViolation("跳跃违规", player);
//            player.displayClientMessage(Component.literal("禁止跳跃！"), true);
//            player.connection
//                    .send(new ClientboundSetEntityMotionPacket(player.getId(), player.getDeltaMovement().add(new Vec3(0, -1, 0))));
//        }, GameDontDoChallengeHandle.DON_JUMP);
//    }

    // 攻击生物挑战
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            Entity target = event.getTarget();
            String msg = "禁止攻击 " + target.getType().getDescription().getString();
            profiler.recordViolation(msg, player);

            event.setCanceled(true);
        }, GameDontDoChallengeHandle.DON_HURT_HOSTILE_MOBS);
    }

    // 方块破坏挑战
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerGameProfiler profiler = GameProcess.getProfiler(player);
            if (profiler.getActiveDontChallenge().equals(GameDontDoChallengeHandle.DON_PUNCH_BLOCKS.id())) {
                profiler.recordViolation("方块破坏违规", player);
                event.setCanceled(true);
                player.swing(event.getPlayer().getUsedItemHand());
            }

        }

    }

    // 物品使用挑战（每tick检测）
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        executeDontChallenge(event, (player, profiler) -> {
            ItemStack stack = event.getItemStack();
            String itemName = stack.getHoverName().getString();
            profiler.recordViolation("使用了禁止物品: " + itemName, player);
            event.setCanceled(true);
        }, GameDontDoChallengeHandle.DON_USE_TOOLS);
    }

    // 移动挑战（持续检测）
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        if (GameProcess.CommonEvent.tick % 15 == 0) {
            // 后退移动检测
//            executeDontChallenge(event, (player, profiler) -> {
//                if (player.zza < 0) { // 检测S键后退
//                    profiler.recordViolation("反向移动违规",player);
//                    player.setDeltaMovement(player.getDeltaMovement().scale(0.5));
//                }
//            }, GameDontDoChallengeHandle.DON_MOVE_BACKWARD);

            // 空间限制检测
            executeDontChallenge(event, (player, profiler) -> {
                //todo 检测周围是否有方块
                int a = 0;
                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos pos = player.blockPosition().offset(x, y, z);
                            BlockState state = player.level().getBlockState(pos);
                            if (state.isCollisionShapeFullBlock(player.level(), pos)) a++;
                        }
                    }
                }
                if (a > 7) {
                    profiler.recordViolation("狭窄空间违规", player);
                    player.hurt(player.damageSources().magic(), 1.0F);
                }
            }, GameDontDoChallengeHandle.DON_ENTER_3X3_SPACE);
        }
    }

    // 生命值检测
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (player.getHealth() < player.getMaxHealth() / 2) {
                profiler.recordViolation("生命值过低", player);
                player.addEffect(new MobEffectInstance(
                        MobEffects.HEAL, 100, 1
                ));
            }
        }, GameDontDoChallengeHandle.DON_HP_BELOW_HALF);
    }

    // 光照条件检测
    @SubscribeEvent
    public static void onLightCheck(TickEvent.PlayerTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            int lightLevel = player.level().getLightEmission(player.blockPosition());
            if (lightLevel > 10) {
                profiler.recordViolation("强光环境违规", player);
                player.addEffect(new MobEffectInstance(
                        MobEffects.BLINDNESS, 200, 0
                ));
            }
        }, GameDontDoChallengeHandle.DON_BRIGHT_LIGHT_10);
    }

    // 工作方块使用检测
    @SubscribeEvent
    public static void onWorkbenchUse(PlayerInteractEvent.RightClickBlock event) {
        executeDontChallenge(event, (player, profiler) -> {
            BlockState state = event.getLevel().getBlockState(event.getPos());

            // 检测所有工作台类型（包括模组添加的）
            if (state.is(Blocks.CRAFTING_TABLE)) {
                profiler.recordViolation("使用工作台", player);
                event.setCanceled(true);
                player.displayClientMessage(
                        Component.literal("禁止使用工作设施！").withStyle(ChatFormatting.RED),
                        true
                );

                // 保留交互动画
                player.swing(event.getHand());
            }
        }, GameDontDoChallengeHandle.DON_USE_WORK_BLOCKS);
    }

    // 伤害接收检测（含伤害豁免机制）
    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            // 过滤非玩家实体和创造模式
            if (!(event.getEntity() instanceof Player) || player.isCreative()) return;

            // 豁免特定伤害来源
            DamageSource source = event.getSource();
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

            profiler.recordViolation("受到伤害: " + source.getMsgId(), player);

            // 伤害反转处理
//                event.setAmount(0);
//                player.setHealth(player.getHealth() + event.getOriginalDamage()); // 恢复生命
//                player.invulnerableTime = 20; // 设置短时无敌
//
//                // 粒子反馈
//                ParticleUtils.spawnHealParticles(player);

        }, GameDontDoChallengeHandle.DON_TAKE_DAMAGE);
    }

    // 食物食用检测系统
    @SubscribeEvent
    public static void onItemConsume(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();

        // 肉类检测（支持标签系统）
        executeDontChallenge(event, (player, profiler) -> {
            if (stack.getItem().getUseAnimation(stack).equals(UseAnim.EAT)) {
                FoodProperties foodProperties = stack.getFoodProperties(player);
                if (foodProperties != null && foodProperties.isMeat()) {
                    profiler.recordViolation("食用肉类: " + stack.getHoverName().getString(), player);
                    event.setCanceled(true);
                    applyFoodPenalty(player); // 例如：给予反胃效果
                }
            }
        }, GameDontDoChallengeHandle.DON_EAT_MEAT);

//            // 素食检测
//            executeDontChallenge(event, (player, profiler) -> {
//                if (stack.is(Tags.Items.FOODS_VEGETARIAN)) {
//                    profiler.recordViolation("食用素食: " + stack.getHoverName().getString());
//                    event.setCanceled(true);
//                    applyFoodPenalty(player);
//                }
//            }, GameDontDoChallengeHandle.DON_EAT_VEGETARIAN_FOOD);
    }

    private static void applyFoodPenalty(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER));
        player.addEffect(new MobEffectInstance(MobEffects.UNLUCK));
        player.addEffect(new MobEffectInstance(MobEffects.WITHER));
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING));
    }

    // 状态效果拦截系统
    @SubscribeEvent
    public static void onEffectApply(MobEffectEvent.Added event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (event.getEffectInstance() != null) {
                profiler.recordViolation("获得效果: " + event.getEffectInstance().getEffect().getDisplayName().getString(), player);
                event.setCanceled(true);
                player.displayClientMessage(Component.literal("身体排斥外来物质！"), true);
            }
        }, GameDontDoChallengeHandle.DON_GET_BUFFS);
    }

    // 玩家攻击检测
    @SubscribeEvent
    public static void onPlayerAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            attackingPlayers.add(player);
        }
        if (event.getEntity() == event.getSource().getEntity()) return;
        // 攻击玩家检测
        executeDontChallenge(event, (player, profiler) -> {
            if (event.getEntity() instanceof Player) {
                // 攻击玩家检测

                profiler.recordViolation("攻击玩家: " + event.getEntity().getName().getString(), player);
                event.setCanceled(true);
                applyCombatPenalty(player, event); // 例如：攻击者受到伤害反弹
            }
        }, GameDontDoChallengeHandle.DON_ATTACK_PLAYERS);

        // 非玩家生物攻击检测
        executeDontChallenge(event, (player, profiler) -> {
            if (!(event.getEntity() instanceof Player)) {
                profiler.recordViolation("攻击生物: " + event.getEntity().getType().getDescription().getString(), player);
                event.setCanceled(true);
                applyCombatPenalty(player, event); // 例如：攻击者受到伤害反弹
            }
        }, GameDontDoChallengeHandle.DON_HURT_NON_PLAYER_MOBS);
    }

    private static void applyCombatPenalty(Player player, LivingHurtEvent event) {
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER));
        player.hurt(new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), player), event.getAmount());
        // player.level().addFreshEntity(new LightningBolt(EntityType.LIGHTNING_BOLT,player.level()));
    }

    // 区域死亡监控系统
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player deadPlayer) {
            // 以死亡玩家为中心检测周围玩家
            Level level = deadPlayer.level();
            List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class,
                    new AABB(deadPlayer.blockPosition()).inflate(10), // 10格半径
                    p -> p != deadPlayer
            );

            nearbyPlayers.forEach(player -> {
                executeDontChallenge(event, (p, profiler) -> {
                    profiler.recordViolation("附近玩家死亡: " + deadPlayer.getScoreboardName(), player);
                    applyMourningEffect(player); // 施加哀悼效果
                }, GameDontDoChallengeHandle.DON_NEARBY_DEATH);
            });
        }
    }

    private static void applyMourningEffect(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.POISON));
    }

    // 近战武器检测系统
    @SubscribeEvent
    public static void onMeleeAttack(AttackEntityEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.getItem() instanceof SwordItem ||
                    weapon.getItem() instanceof AxeItem || weapon.getItem() instanceof PickaxeItem || weapon.getItem() instanceof ShovelItem || weapon.getItem() instanceof HoeItem
            ) {
                profiler.recordViolation("使用近战武器: " + weapon.getHoverName().getString(), player);
                event.setCanceled(true);
                disarmPlayer(player); // 击落武器
            }
        }, GameDontDoChallengeHandle.DON_MELEE_WEAPON);
    }

    private static void disarmPlayer(Player player) {
        player.drop(player.getMainHandItem().copy(), true, true);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

    }

    // 远程武器检测系统
    @SubscribeEvent
    public static void onRangedAttack(ArrowLooseEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            ItemStack weapon = event.getBow();
            profiler.recordViolation("使用远程武器: " + weapon.getHoverName().getString(), player);
            event.setCanceled(true);
            weapon.hurtAndBreak(5, player, p -> p.broadcastBreakEvent(p.getUsedItemHand())); // 快速损耗
        }, GameDontDoChallengeHandle.DON_RANGED_WEAPON);
    }

    // 玩家距离监测系统（每2秒检测）
    @SubscribeEvent
    public static void onPlayerProximityCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.tickCount % 40 != 0) return;

        executeDontChallenge(event, (player, profiler) -> {
            List<Player> nearby = player.level().getEntitiesOfClass(
                    Player.class,
                    new AABB(player.blockPosition()).inflate(2.5), // 2.5格半径
                    p -> p != player
            );

            if (!nearby.isEmpty()) {
                profiler.recordViolation("接近玩家: " + nearby.get(0).getScoreboardName(), player);
                pushPlayersApart(player, nearby.get(0)); // 推开玩家
            }
        }, GameDontDoChallengeHandle.DON_PLAYER_CLOSE);
    }

    private static void pushPlayersApart(Player player1, Player player2) {
        // 获取两个玩家的位置
        Vec3 pos1 = player1.position();
        Vec3 pos2 = player2.position();

        // 计算推开方向
        Vec3 direction = pos1.subtract(pos2).normalize();

        // 施加反向力
        double pushStrength = 2; // 推开力度
        player1.setDeltaMovement(player1.getDeltaMovement().add(direction.scale(pushStrength)));
        player2.setDeltaMovement(player2.getDeltaMovement().add(direction.scale(-pushStrength)));
    }


    // 流体接触检测系统
    @SubscribeEvent
    public static void onFluidContact(LivingEvent.LivingTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            BlockPos feetPos = player.blockPosition();
            BlockState state = player.level().getBlockState(feetPos);

            // 水体检测（含流动水）
            if (state.getFluidState().is(FluidTags.WATER)) {
                profiler.recordViolation("接触水体", player);
                DMoveToSafeArea(player); // 跳回岸上
            }
        }, GameDontDoChallengeHandle.DON_TOUCH_WATER);
    }

    private static void DMoveToSafeArea(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 2));
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 30, 2));
        player.connection
                .send(new ClientboundSetEntityMotionPacket(player.getId(), player.getDeltaMovement().add(0, 0.75, 0)));
    }

    // 游泳状态检测（每tick）
    @SubscribeEvent
    public static void onSwimCheck(LivingEvent.LivingTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (player.isInWater() && player.getDeltaMovement().length() > 0.08) {
                profiler.recordViolation("游泳移动", player);
                player.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS, 100, 2 // 降低游泳能力
                ));
            }
        }, GameDontDoChallengeHandle.DON_SWIM);
    }

    // 疾跑状态检测
    @SubscribeEvent
    public static void onSprint(LivingEvent.LivingTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (player.isSprinting() && player.onGround()) {
                profiler.recordViolation("陆地疾跑", player);
                player.setSprinting(false); // 强制取消疾跑
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN, 40, 2 // 短时减速
                ));
            }
        }, GameDontDoChallengeHandle.DON_SPRINT);
    }
    // 完整方块放置检测（支持模组方块）
//        @SubscribeEvent
//        public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
//            executeDontChallenge(event, (player, profiler) -> {
//                BlockState placedBlock = event.getPlacedBlock();
//
//                // 使用标签系统检测完整方块（需在tags/blocks/full_blocks.json定义）
//
//                    profiler.recordViolation("放置方块: " + placedBlock.getBlock().getName(),player);
//                    event.setCanceled(true);
//                    refundBlockItem(player, event.getPos());
//
//            }, GameDontDoChallengeHandle.DON_PLACE_FULL_BLOCK);
//        }

    private static void refundBlockItem(Player player, BlockPos pos) {
        player.drop(player.getMainHandItem().copy(), true);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

    }

    // 护甲穿戴检测系统（每2秒扫描）
    @SubscribeEvent
    public static void onArmorCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.player.tickCount % 40 != 0) return;

        executeDontChallenge(event, (player, profiler) -> {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) continue;
                ItemStack armor = player.getItemBySlot(slot);

                if (!armor.isEmpty() && armor.getItem() instanceof ArmorItem) {
                    profiler.recordViolation("穿戴护甲: " + armor.getHoverName().getString(), player);

                }
                unequipArmor(player, armor, slot);

            }
        }, GameDontDoChallengeHandle.DON_WEAR_ARMOR);
        executeDontChallenge(event, (player, profiler) -> {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot == EquipmentSlot.OFFHAND) {
                    ItemStack armor = player.getItemBySlot(slot);
                    if (armor.isEmpty()) continue;
                    profiler.recordViolation("副手拿东西: " + armor.getHoverName().getString(), player);

                    unequipArmor(player, armor, slot);
                }
            }
        }, GameDontDoChallengeHandle.DONT_HAND_ITEM);
    }

    private static void unequipArmor(Player player, ItemStack i, EquipmentSlot equipmentSlot) {

        player.drop(i.copy(), true);
        player.setItemSlot(
                equipmentSlot,
                ItemStack.EMPTY
        );
    }

    // 盾牌使用检测（含格挡动画）
    @SubscribeEvent
    public static void onShieldUse(LivingEntityUseItemEvent.Start event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (event.getItem().getItem() instanceof ShieldItem) {
                profiler.recordViolation("使用盾牌格挡", player);
                event.setCanceled(true);
                player.swing(InteractionHand.MAIN_HAND);
                player.displayClientMessage(
                        Component.literal("防御是不被允许的！").withStyle(ChatFormatting.RED),
                        true
                );
            }
        }, GameDontDoChallengeHandle.DON_USE_SHIELD);
    }

    // 潜行状态检测（每tick）
    @SubscribeEvent
    public static void onSneakCheck(LivingEvent.LivingTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (player.isShiftKeyDown()) {
                profiler.recordViolation("潜行移动", player);
                forceStandUp(player); // 强制解除潜行
            }
        }, GameDontDoChallengeHandle.DON_SNEAK);
    }

    private static void forceStandUp(ServerPlayer player) {
        player.setShiftKeyDown(false);
    }

    // 视角方向检测（每10 tick检测）
    @SubscribeEvent
    public static void onViewCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.tickCount % 10 != 0) return;

        // 垂直视角检测系统



        executeDontChallenge(event, (player, profiler) -> {
            if (event.player.getYHeadRot() > 85f) {
                profiler.recordViolation("仰视天空", player);
                event.player.setYHeadRot(80f);

            }
        }, GameDontDoChallengeHandle.DON_LOOK_UP);


        executeDontChallenge(event, (player, profiler) -> {
            if (event.player.getYHeadRot() < -85f) {
                profiler.recordViolation("俯视地面", player);
                event.player.setYHeadRot(-80f);
            }
        }, GameDontDoChallengeHandle.DON_LOOK_DOWN);
    }

    private static void adjustViewAngle(ServerPlayer player, Vec3 scale) {
        player.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition(1.0f).add(scale));
    }


    // 淋雨检测（每2秒检测）
    @SubscribeEvent
    public static void onRainCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.tickCount % 40 != 0) return;

        executeDontChallenge(event, (player, profiler) -> {
            // 世界天气检测
            Level level = player.level();
            boolean isRaining = level.isRainingAt(player.blockPosition());

            // 每日降雨概率控制

            if (isRaining) {
                profiler.recordViolation("暴露在雨中", player);
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN, 40, 2 // 短时减速
                ));
                player.addEffect(new MobEffectInstance(
                        MobEffects.WITHER, 40, 2 // 短时减速
                ));
            }
        }, GameDontDoChallengeHandle.DON_GET_RAINED);
    }

    // 背包打开拦截系统
    @SubscribeEvent
    public static void onInventoryOpen(PlayerContainerEvent.Open event) {
        executeDontChallenge(event, (player, profiler) -> {
            // 排除工作台等特殊界面
            if (!(event.getContainer().getClass() == InventoryMenu.class)) {
                profiler.recordViolation("打开背包", player);
                player.closeContainer();

                player.displayClientMessage(
                        Component.literal("背包被神秘力量封锁！").withStyle(ChatFormatting.RED),
                        true
                );
            }
        }, GameDontDoChallengeHandle.DON_OPEN_INVENTORY);
    }

    // 物品丢弃检测
    @SubscribeEvent
    public static void onItemDrop(ItemTossEvent event) {

        executeDontChallenge(event, (player, profiler) -> {
            profiler.recordViolation("丢弃物品: " + event.getEntity().getItem().getDisplayName().getString(), player);
            event.setCanceled(true);
            retrieveDroppedItem(player, event.getEntity().getItem());
        }, GameDontDoChallengeHandle.DON_DROP_ITEMS);
    }

    private static void retrieveDroppedItem(ServerPlayer player, ItemStack item) {

    }

    // 容器使用检测（箱子、熔炉等）
    @SubscribeEvent
    public static void onContainerUse(PlayerInteractEvent.RightClickBlock event) {
        executeDontChallenge(event, (player, profiler) -> {
            BlockState state = event.getLevel().getBlockState(event.getPos());
            if (state.getBlock() instanceof AbstractChestBlock ||
                    state.getBlock() instanceof AbstractFurnaceBlock) {
                profiler.recordViolation("使用容器: " + state.getBlock().getName().getString(), player);
                event.setCanceled(true);
                sealContainer(event.getPos(), player.level()); // 封印容器
            }
        }, GameDontDoChallengeHandle.DON_USE_CONTAINERS);
    }

    private static void sealContainer(@NotNull BlockPos pos, Level level) {
        if (level.getBlockState(pos.above(1)).isAir()) {
            level.setBlock(pos.above(1), Blocks.STONE.defaultBlockState(), 3);
        }
    }

    // 光源放置检测
    @SubscribeEvent
    public static void onLightPlace(BlockEvent.EntityPlaceEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            BlockState state = event.getPlacedBlock();
            if (state.getLightEmission(event.getLevel(), event.getPos()) > 0 ||
                    state.getBlock() instanceof TorchBlock ||
                    state.is(Blocks.TORCH)) {
                profiler.recordViolation("放置光源: " + state.getBlock().getName(), player);
                event.setCanceled(true);
                applyLightFearEffect(player);
            }
        }, GameDontDoChallengeHandle.DON_PLACE_LIGHT);
    }

    private static void applyLightFearEffect(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS));
    }

    // 光照等级检测系统（每秒检测2次）
    @SubscribeEvent
    public static void onLightLevelCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.tickCount % 10 != 0) return;

        executeDontChallenge(event, (player, profiler) -> {
            BlockPos feetPos = player.blockPosition();
            int lightLevel = getEffectiveLightLevel(player.level(), feetPos);

            if (lightLevel < 8) {
                profiler.recordViolation("进入低光照区域（等级" + lightLevel + "）", player);
                applyLightPenalty(player, feetPos);
            }
        }, GameDontDoChallengeHandle.DON_DIM_LIGHT_8);
    }

    private static void applyLightPenalty(ServerPlayer player, BlockPos feetPos) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40));

        //如果周围没有监守者
//        if (player.level().getNearbyEntities(Warden.class, null, player, player.getBoundingBox().inflate(16)).isEmpty()) {
//            // 创建监守者
//            Warden p46964 = new Warden(EntityType.WARDEN, player.level());
//            p46964.setHealth(p46964.getMaxHealth() / 8);
//            // 设置监守者位置为玩家位置
//            p46964.moveTo(feetPos.getX(), feetPos.getY(), feetPos.getZ(), 0, 0);
//            // 将监守者添加到世界
//            player.level().addFreshEntity(p46964);
//        }
    }

    private static int getEffectiveLightLevel(Level level, BlockPos feetPos) {
        return level.getMaxLocalRawBrightness(feetPos);
    }

    // 饱食度控制系统（实时监控）
    @SubscribeEvent
    public static void onHungerChange(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player)) return;

        executeDontChallenge(event, (player, profiler) -> {
            FoodProperties food = event.getItem().getFoodProperties(player);
            if (food != null && (player.getFoodData().getFoodLevel() + food.getNutrition()) > 18) {
                profiler.recordViolation("尝试超额进食: " + event.getItem().getHoverName().getString(), player);
                adjustHunger(player, food.getNutrition());
                //  event.setCanceled(true);
            }
        }, GameDontDoChallengeHandle.DON_FULL_HUNGER);
    }

    private static void adjustHunger(ServerPlayer player, int nutrition) {
        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - nutrition);
    }

    // 夜间行动限制系统
    @SubscribeEvent
    public static void onNightMovement(TickEvent.PlayerTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (isNightTime(player.level()) && hasSkyAccess(player)) {
                profiler.recordViolation("夜间露天移动", player);
                freezePlayer(player);
            }
        }, GameDontDoChallengeHandle.DON_NIGHT_MOVE);
    }

    private static void freezePlayer(ServerPlayer player) {
        // 冻结玩家，使其无法移动
        player.setDeltaMovement(Vec3.ZERO); // 停止玩家移动
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 255)); // 施加最大减速效果
    }

    private static boolean hasSkyAccess(ServerPlayer player) {
        // 检查玩家是否暴露在露天环境中
        //BlockPos playerPos = player.blockPosition();
        Level level = player.level();
        return isInSky(level, player);
    }

    public static boolean isInSky(Level level, Player player) {
        for (int i = 0; i < 20; i++) {
            if (!level.getBlockState(player.getOnPos().above(i)).isAir()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNightTime(Level level) {
        // 检查当前游戏时间是否为夜间
        long time = level.getDayTime() % 24000; // 获取当前游戏时间（0-23999）
        return time >= 13000 && time < 23000; // 13000-23000为夜间
    }


    // 非武器攻击检测
    @SubscribeEvent
    public static void onNonWeaponAttack(LivingHurtEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            ItemStack weapon = player.getMainHandItem();
            if (!isWeapon(weapon)) {
                profiler.recordViolation("非武器攻击", player);
                applyAttackPenalty(player);
              //  applyCombatPenalty(player, event); // 例如：攻击者受到伤害反弹
            }
        }, GameDontDoChallengeHandle.DON_NON_WEAPON_ATTACK);
    }

    private static void applyAttackPenalty(ServerPlayer player) {
        player.drop(player.getMainHandItem().copy(), true);
        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40));
    }

    private static boolean isWeapon(ItemStack weapon) {
        return weapon.getItem() instanceof SwordItem || weapon.getItem() instanceof AxeItem || weapon.getItem() instanceof PickaxeItem || weapon.getItem() instanceof ShovelItem || weapon.getItem() instanceof HoeItem;
    }

    // 武器攻击限制系统
    @SubscribeEvent
    public static void onWeaponAttack(LivingHurtEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            ItemStack weapon = player.getMainHandItem();
            if (isWeapon(weapon)) {
                profiler.recordViolation("使用禁用武器: " + weapon.getHoverName().getString(), player);
                disarmPlayer(player);
            }
        }, GameDontDoChallengeHandle.DON_ANY_WEAPON_ATTACK);
    }


    // 高伤害武器检测
    @SubscribeEvent
    public static void onHighDamageAttack(LivingHurtEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            ItemStack weapon = player.getMainHandItem();

            if (event.getAmount() > 6.0f) {
                profiler.recordViolation("高伤害攻击 (" + String.format("%.1f", event.getAmount()) + ")", player);
                event.setAmount(event.getAmount() * 0.1f);
            }
        }, GameDontDoChallengeHandle.DON_USE_OVER6_DAMAGE);
    }


    // 背包容量监控系统
    @SubscribeEvent
    public static void onInventoryCheck(TickEvent.PlayerTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (isInventoryFull(player)) {
                profiler.recordViolation("背包已满", player);
                blockItemPickups(player);
            }
        }, GameDontDoChallengeHandle.DON_FULL_INVENTORY);
    }

    private static void blockItemPickups(ServerPlayer player) {
        // 先检查背包是否真的已满
        if (!isInventoryFull(player)) return;

        // 创建待清理物品列表（避免在遍历时修改集合）
        List<ItemStack> toRemove = new ArrayList<>();
        int maxSlots = player.getInventory().getContainerSize();

        // 第一遍扫描：标记需要清理的物品
        player.getInventory().items.stream()
                .filter(stack -> !stack.isEmpty())
                .skip(9) // 保留前9个快捷栏物品
                .limit(maxSlots - 9) // 仅处理非快捷栏部分
                .forEach(toRemove::add);

        // 第二遍处理：清理并丢弃物品
        toRemove.forEach(stack -> {
            // 安全移除物品（使用副本避免ConcurrentModificationException）
            ItemStack copy = stack.copy();
            stack.setCount(0); // 清空原堆叠

            // 在玩家位置生成掉落物
            player.drop(copy, false, true); // 参数说明：不保留所有权，生成自然掉落效果
        });

        // 添加冷却机制防止无限循环
        player.getCooldowns().addCooldown(Items.BARRIER, 20); // 20 ticks冷却
    }

    private static boolean isInventoryFull(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        // 计算已用格子数（堆叠数>=1视为已占用）
        long usedSlots = inventory.items.stream()
                .filter(stack -> !stack.isEmpty())
                .count();
        // 总格子数减去快捷栏保留的9格
        return usedSlots >= (inventory.getContainerSize() - 18);
    }


    // 合成界面拦截系统
    @SubscribeEvent
    public static void onCraftingAttempt(PlayerEvent.ItemCraftedEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
                profiler.recordViolation("尝试在狭窄的背包合成", player);
                //event.setCanceled(true);
            if (!(event.getInventory() instanceof CraftingMenu)) {
                player.closeContainer();

                player.displayClientMessage(
                        Component.literal("合成功能已被禁用！").withStyle(ChatFormatting.RED),
                        true
                );

            }
        }, GameDontDoChallengeHandle.DON_USE_CRAFTING_GRID);
    }

    // 全局方块放置禁令
    @SubscribeEvent
    public static void onAnyBlockPlace(BlockEvent.EntityPlaceEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            profiler.recordViolation("放置方块: " + event.getPlacedBlock().getBlock().getName().getString(), player);
            event.setCanceled(true);
            refundBlockItem(player, event.getPos()); // 反转放置动作
        }, GameDontDoChallengeHandle.DON_PLACE_ANY_BLOCK);
    }

    private static void reversePlacement(ServerPlayer player, BlockPos pos) {

    }

    // 工具挖掘限制系统
    @SubscribeEvent
    public static void onToolMining(BlockEvent.BreakEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            ItemStack tool = event.getPlayer().getMainHandItem();
            if (isMiningTool(tool)) {
                profiler.recordViolation("使用工具挖掘: " + tool.getHoverName().getString(), player);
                event.setCanceled(true);
                applyToolFatigue(player, tool); // 工具损耗加强
            }
        }, GameDontDoChallengeHandle.DON_TOOL_MINING);
    }

    private static void applyToolFatigue(ServerPlayer player, ItemStack tool) {
        tool.hurtAndBreak(10, player, (p) -> {
            p.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
    }

    private static boolean isMiningTool(ItemStack tool) {
        return tool.getItem() instanceof PickaxeItem || tool.getItem() instanceof AxeItem || tool.getItem() instanceof ShovelItem;
    }

    // 社交行为监控系统（半径8格）
    @SubscribeEvent
    public static void onSocialBehaviorCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.tickCount % 10 != 0) return;

        List<Player> nearbyPlayers = getNearbyPlayers(event.player, 8.0);

        // 攻击行为传播检测
        nearbyPlayers.forEach(nearby -> {
            checkAttackingBehavior(event.player, nearby, event);
        });

        // 淋雨行为连锁反应
        nearbyPlayers.forEach(nearby -> {
            checkRainBehavior(event.player, nearby, event);
        });

        // 工作台使用检测
        nearbyPlayers.forEach(nearby -> {
            checkWorkbenchUsage(event.player, nearby, event);
        });
        attackingPlayers.clear();
    }

    private static List<Player> getNearbyPlayers(Player player, double v) {
        return player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(v), p -> p != player);
    }

    //=== 具体检测方法 ===//
    private static void checkAttackingBehavior(Player current, Player nearby, TickEvent.PlayerTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (isAttacking(nearby)) {
                profiler.recordViolation("附近玩家攻击: " + nearby.getScoreboardName(), player);
                player.hurt(new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC), player), 4f);


                applySocialPenalty(current);
            }
        }, GameDontDoChallengeHandle.DON_NEAR_ATTACKING);
    }

    private static void applySocialPenalty(Player current) {
        if (isAttacking(current)) {
            current.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));
            current.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
            current.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
            current.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
        }
    }

    public static List<Player> attackingPlayers = new ArrayList<>();

    private static boolean isAttacking(Player nearby) {

        if (attackingPlayers.contains(nearby)) {
            attackingPlayers.remove(nearby);
            return true;

        } else {
            return false;
        }
    }

    private static void checkRainBehavior(Player current, Player nearby, TickEvent.PlayerTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (isInRain(nearby)) {
                profiler.recordViolation("附近玩家淋雨: " + nearby.getScoreboardName(), player);
                spreadRainEffect(current);
            }
        }, GameDontDoChallengeHandle.DON_NEAR_RAINED);
    }

    private static void spreadRainEffect(Player current) {
        current.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));
        current.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
        current.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
        current.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
    }


    private static boolean isInRain(Player nearby) {
        return nearby.level().isRainingAt(nearby.blockPosition());
    }

    private static void checkWorkbenchUsage(Player current, Player nearby, TickEvent.PlayerTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (isUsingWorkbench(nearby)) {
                profiler.recordViolation("附近玩家工作: " + nearby.getScoreboardName(), player);
                spreadRainEffect(current);
            }
        }, GameDontDoChallengeHandle.DON_NEAR_WORKING);
    }

    private static boolean isUsingWorkbench(Player nearby) {
        return nearby.containerMenu instanceof CraftingMenu;
    }

    // 进食行为传播检测系统（半径5格）
    @SubscribeEvent
    public static void onNearbyEating(TickEvent.PlayerTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            getNearbyPlayers(player, 5.0).forEach(nearby -> {
                if (isEating(nearby)) {
                    profiler.recordViolation("附近玩家进食: " + nearby.getScoreboardName(), player);
                    induceHunger(player);
                }
            });
        }, GameDontDoChallengeHandle.DON_NEAR_EATING);
    }

    private static void induceHunger(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 2));
    }

    private static boolean isEating(Player nearby) {
        if (nearby.getTicksUsingItem() > 0) {
            if (nearby.getUseItem().getItem().getUseAnimation(nearby.getUseItem()) == UseAnim.EAT) {
                return true;
            }

        }
        return false;
    }

    // 方块破坏连锁反应系统
//    @SubscribeEvent
//    public static void onNearbyMining(BlockEvent.BreakEvent event) {
//        executeDontChallenge(event, (player, profiler) -> {
//            getNearbyPlayers(event.getPlayer(), 8.0).forEach(nearby -> {
//                profiler.recordViolation("领地入侵: " + event.getPlayer().getScoreboardName(),player);
//                defendTerritory(nearby, event.getPos());
//            });
//        }, GameDontDoChallengeHandle.DON_NEAR_PUNCHING);
//    }

    private static void defendTerritory(Player nearby, BlockPos pos) {
        if (!nearby.isCreative()) {

        }
    }

    // 燃烧状态实时监控
    @SubscribeEvent
    public static void onBurnCheck(LivingEvent.LivingTickEvent event) {
        executeDontChallenge(event, (player, profiler) -> {
            if (player.isOnFire() && !player.isSpectator()) {
                profiler.recordViolation("引火烧身", player);
                player.extinguishFire();
                applyBurnConsequences(player);
            }
        }, GameDontDoChallengeHandle.DON_SELF_BURN);
    }

    private static void applyBurnConsequences(ServerPlayer player) {
        player.setSecondsOnFire(7);
    }

    // 高空限制系统（每秒检测1次）
    @SubscribeEvent
    public static void onAltitudeCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.tickCount % 20 != 0) return;

        executeDontChallenge(event, (player, profiler) -> {
            if (player.getY() > getHeightLimit(player)) {
                profiler.recordViolation("恐高触发", player);
                handleAltitudeViolation(player);
            }
        }, GameDontDoChallengeHandle.DON_HIGH_Y_AXIS);
    }

    private static void handleAltitudeViolation(ServerPlayer player) {
        player.getDeltaMovement().add(0.0, -0.1, 0.0);
    }

    private static double getHeightLimit(ServerPlayer player) {
        //return player.level().getHeight() - 64.0;
        return 100;
    }


    // ================ 玩家距离检测 ================ //
    private static final Map<UUID, Long> PLAYER_PROXIMITY_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerProximityCheck1(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

        executeDontChallenge(event, (player, profiler) -> {
            List<ServerPlayer> players = player.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    player.getBoundingBox().inflate(2.5), // 5x5区域
                    p -> p != player && !p.isSpectator()
            );

            if (!players.isEmpty()) {
                profiler.recordViolation("附近存在其他玩家", player);
                applySocialAnxietyEffect(player);
            }
            PLAYER_PROXIMITY_CACHE.put(player.getUUID(), System.currentTimeMillis());
        }, GameDontDoChallengeHandle.DON_HIGH_YYYYY);
    }

    // ================ 草地检测 ================ //


    @SubscribeEvent
    public static void onGrassCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 40 != 0) return; // 2秒检测间隔

        executeDontChallenge(event, (player, profiler) -> {
            BlockPos footPos = player.blockPosition();
            if (player.level().getBlockState(footPos.below(1)).getBlock() == Blocks.GRASS_BLOCK) {
                profiler.recordViolation("站在草地上", player);
                applyGrassPhobiaEffect(player);
            }
        }, GameDontDoChallengeHandle.DON_HIGH_YACGF);
    }

    // ================ 敌对生物检测 ================ //
    private static final Map<UUID, Boolean> HOSTILE_MOB_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onHostileMobCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 60 != 0) return; // 3秒检测间隔

        executeDontChallenge(event, (player, profiler) -> {
            List<Mob> mobs = player.level().getEntitiesOfClass(
                    Mob.class,
                    player.getBoundingBox().inflate(8),
                    mob -> mob.getClassification(false) == MobCategory.MONSTER
            );

            if (!mobs.isEmpty()) {
                profiler.recordViolation("附近有敌对生物", player);
                applyFearEffect(player);
                HOSTILE_MOB_CACHE.put(player.getUUID(), true);
            } else {
                HOSTILE_MOB_CACHE.remove(player.getUUID());
            }
        }, GameDontDoChallengeHandle.DON_HIGH_HHHHHHH);
    }

    // ================ 血量变动检测 ================ //
    private static final Map<UUID, Float> LAST_HEALTH = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onHealthChangeCheck(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) return;
        if (event.player.tickCount % 10 != 0) return; // 1秒检测间隔

        executeDontChallenge(event, (player, profiler) -> {
            float current = player.getHealth();
            Float last = LAST_HEALTH.get(player.getUUID());

            if (last != null && Math.abs(current - last) > 0.01f) {
                profiler.recordViolation("一定幅度生命值变动", player);
                applyHealthLockEffect(player);
            }
            LAST_HEALTH.put(player.getUUID(), current);
        }, GameDontDoChallengeHandle.DON_HIGH_XXXXXXXX);
    }


    // ================ 输入禁用检测 ================ //
    @SubscribeEvent
    public static void onLeftClick(LivingAttackEvent event) {
        executeDontChallenge(
                event,
                (player, profiler) -> {
                        //applyInputBlockEffect(player);
                    profiler.recordViolation("左右键被禁止", player);
                        event.setCanceled(true);
                },
                GameDontDoChallengeHandle.DON_HIGH_CCCCCCCCCC
        );
    }

    @SubscribeEvent
    public static void onRightClick(BlockEvent.EntityPlaceEvent event) {
        executeDontChallenge(
                event,
                (player, profiler) -> {
                   // applyInputBlockEffect(player);
                  profiler.recordViolation("左右键被禁止", player);
                    event.setCanceled(true);
                },
                GameDontDoChallengeHandle.DON_HIGH_CCCCCCCCCC
        );
    }

    @SubscribeEvent
    public static void onRightClick(LivingEntityUseItemEvent event) {
        executeDontChallenge(
                event,
                (player, profiler) -> {
                   // applyInputBlockEffect(player);
                  profiler.recordViolation("左右键被禁止", player);
                  event.setCanceled(true);
                },
                GameDontDoChallengeHandle.DON_HIGH_CCCCCCCCCC
        );
    }




    // ================ 缓存清理 ================ //
    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        PLAYER_PROXIMITY_CACHE.remove(uuid);
        HOSTILE_MOB_CACHE.remove(uuid);
        LAST_HEALTH.remove(uuid);
    }

    // ================ 惩罚效果 ================ //
    private static void applySocialAnxietyEffect(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
    }

    private static void applyGrassPhobiaEffect(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
    }

    private static void applyFearEffect(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
    }

    private static void applyHealthLockEffect(ServerPlayer player) {
        player.setHealth(LAST_HEALTH.getOrDefault(player.getUUID(), player.getHealth()));
    }


}
}
