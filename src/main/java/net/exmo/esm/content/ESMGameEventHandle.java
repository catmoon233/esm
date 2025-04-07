package net.exmo.esm.content;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.HashMap;
import java.util.Map;

public class ESMGameEventHandle {
    public static Map<String,ESMGameEvent> events =new HashMap<>();
    public static ESMGameEvent findEvent(String id){
        return events.get(id);
    }
    public static final ESMGameEvent ANVIL_TIME = new ESMGameEvent("ANVIL_TIME").time(100).name("天降正义").describe("玩家头上将持续掉落铁砧（挖掘后不掉落）").appearType(ESMGameEvent.AppearType.TICK).appear(
            (serverLevel) -> {
                if (GameProcess.CommonEvent.tick % 80 == 0) {
                    serverLevel.players().forEach(
                            player -> {
                                // 获取玩家的位置
                                BlockPos playerPos = player.blockPosition();
                                // 计算铁砧放置的位置（玩家头顶上方10格）
                                BlockPos anvilPos = playerPos.above(10);

                                // 创建铁砧实体
                                FallingBlockEntity fallingBlockEntity =  FallingBlockEntity.fall(serverLevel, anvilPos, Blocks.ANVIL.defaultBlockState());

                                // 设置铁砧实体无视碰撞
                                fallingBlockEntity.setHurtsEntities(2, 5);

                                //fallingBlockEntity.noPhysics =true;
                                fallingBlockEntity.setDeltaMovement(0, -1, 0);

                                // 将铁砧实体添加到世界中
                        //        serverLevel.addFreshEntity(fallingBlockEntity);
                            }
                    );
                }

            }
    );
    public static final ESMGameEvent LIGHTNING_STORM = new ESMGameEvent("LIGHTNING_STORM")
            .time(120).name("雷霆风暴").describe("玩家周围将不断落下闪电")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 80 == 0) {
                    serverLevel.players().forEach(player -> {
                        BlockPos pos = player.blockPosition().offset(
                                serverLevel.random.nextInt(5) - 2,
                                0,
                                serverLevel.random.nextInt(5) - 2
                        );
                        LightningBolt p8837 = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
                        p8837.moveTo(pos, 0, 0);
                        serverLevel.addFreshEntity(p8837);
                    });
                }
            });

    public static final ESMGameEvent ZOMBIE_INVASION = new ESMGameEvent("ZOMBIE_INVASION")
            .time(180).name("僵尸围城").describe("大量僵尸将在玩家周围生成")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 200 == 0) {
                    serverLevel.players().forEach(player -> {
                        for (int i = 0; i < 2; i++) {
                            BlockPos spawnPos = player.blockPosition().offset(
                                    serverLevel.random.nextInt(5) - 2,
                                    0,
                                    serverLevel.random.nextInt(5) - 2
                            );
                            Zombie zombie = new Zombie(EntityType.ZOMBIE, serverLevel);

                            zombie.moveTo(spawnPos, 0, 0);
                            serverLevel.addFreshEntity(zombie);
                        }
                    });
                }
            });

    public static final ESMGameEvent SUPER_JUMP = new ESMGameEvent("SUPER_JUMP")
            .time(90).name("超级跳跃").describe("玩家获得跳跃提升效果")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                serverLevel.players().forEach(player ->
                        player.addEffect(new MobEffectInstance(
                                MobEffects.JUMP,
                                40,
                                4,  // 跳跃提升V
                                false,
                                false
                        ))
                );
            });

    public static final ESMGameEvent LAVA_FLOOR = new ESMGameEvent("LAVA_FLOOR")
            .time(150).name("熔岩地面").describe("玩家脚下的方块会变成岩浆块")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                serverLevel.players().forEach(player -> {
                    BlockPos pos = player.blockPosition().below();
                    BlockState blockState = serverLevel.getBlockState(pos);
                    if ( blockState.canEntityDestroy(serverLevel, pos, player) && !blockState.isAir()){
                        serverLevel.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
                    }
                });
            });
    public static final ESMGameEvent TIME_CHAOS = new ESMGameEvent("TIME_CHAOS")
            .time(150).name("时空紊乱").describe("世界时间将随机快速变化")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 100 == 0) {
                    serverLevel.setDayTime(serverLevel.random.nextInt(24000));
                }
            });

    public static final ESMGameEvent REVERSE_GRAVITY = new ESMGameEvent("REVERSE_GRAVITY")
            .time(90).name("重力反转").describe("玩家跳跃时会向上飘浮")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                serverLevel.players().forEach(player -> {
                    if (player.verticalCollision) {
                        player.setDeltaMovement(player.getDeltaMovement().add(0, 0.2, 0));
                    }
                });
            });

    public static final ESMGameEvent INVISIBLE_THREAT = new ESMGameEvent("INVISIBLE_THREAT")
            .time(120).name("无形威胁").describe("所有玩家获得隐身但持续受到伤害")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 120 == 0) {
                serverLevel.players().forEach(player -> {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.INVISIBILITY,
                            120,
                            0,
                            false,
                            false
                    ));
                    player.hurt(player.damageSources().magic(), 1f);
                });
                }
            });

    public static final ESMGameEvent BLOCK_DECAY = new ESMGameEvent("BLOCK_DECAY")
            .time(180).name("方块腐蚀").describe("玩家周围的方块会随机消失")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 10 == 0) {
                    serverLevel.players().forEach(player -> {
                        BlockPos targetPos = player.blockPosition().offset(
                                serverLevel.random.nextInt(7) - 3,
                                serverLevel.random.nextInt(3) - 1,
                                serverLevel.random.nextInt(7) - 3
                        );
                        if (!serverLevel.getBlockState(targetPos).isAir()) {
                            serverLevel.destroyBlock(targetPos, true);
                        }
                    });
                }
            });

    public static final ESMGameEvent PHANTOM_SWARM = new ESMGameEvent("PHANTOM_SWARM")
            .time(100).name("幻翼突袭").describe("大量幻翼将包围玩家")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 200 == 0) {
                    serverLevel.players().forEach(player -> {
                        for (int i = 0; i < 5; i++) {
                            Phantom phantom = new Phantom(EntityType.PHANTOM, serverLevel);
                            BlockPos spawnPos = player.blockPosition().offset(
                                    serverLevel.random.nextInt(15) - 7,
                                    20 + serverLevel.random.nextInt(10),
                                    serverLevel.random.nextInt(15) - 7
                            );
                            phantom.moveTo(spawnPos, 0, 0);
                            phantom.setTarget(player);
                            serverLevel.addFreshEntity(phantom);
                        }
                    });
                }
            });

    public static final ESMGameEvent EXPLOSIVE_TOUCH = new ESMGameEvent("EXPLOSIVE_TOUCH")
            .time(60).name("爆炸接触").describe("玩家攻击生物会引发爆炸")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                serverLevel.players().forEach(player -> {
                    if (player.getLastHurtMob() != null &&
                            serverLevel.getGameTime() - player.getLastHurtMobTimestamp() < 5) {
                        serverLevel.explode(null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                3.0f,
                                Level.ExplosionInteraction.MOB);
                    }
                });
            });
    public static final ESMGameEvent RANDOM_TP = new ESMGameEvent("RANDOM_TP")
            .time(60).name("空间扭曲").describe("玩家会随机传送到附近位置")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 200 == 0) {
                    serverLevel.players().forEach(player -> {
                        BlockPos newPos = player.blockPosition().offset(
                                serverLevel.random.nextInt(21) - 10,
                                0,
                                serverLevel.random.nextInt(21) - 10
                        );
                        player.teleportTo(
                                newPos.getX() + 0.5,
                                serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, newPos.getX(), newPos.getZ()),
                                newPos.getZ() + 0.5
                        );
                    });
                }
            });
    public static final ESMGameEvent NONE = new ESMGameEvent("NONE").time(100).name("无事件").describe("当前回合无事件").appearType(ESMGameEvent.AppearType.NONE).appear(
            (serverLevel) -> {
            }
    );
    public static final ESMGameEvent SNOWBALL_STORM = new ESMGameEvent("SNOWBALL_STORM")
            .time(120).name("雪球风暴").describe("玩家将被持续不断的雪球攻击")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 5 == 0) {
                    serverLevel.players().forEach(player -> {
                        Snowball snowball = new Snowball(EntityType.SNOWBALL, serverLevel);
                        BlockPos spawnPos = player.blockPosition()
                                .offset(serverLevel.random.nextInt(11) - 5, 15, serverLevel.random.nextInt(11) - 5);
                        snowball.moveTo(spawnPos, 0, 0);
                        snowball.shoot(player.getX() - spawnPos.getX(),
                                player.getY() - spawnPos.getY(),
                                player.getZ() - spawnPos.getZ(),
                                1.5f, 1.0f);
                        serverLevel.addFreshEntity(snowball);
                    });
                }
            });


    public static final ESMGameEvent LIFE_EXCHANGE = new ESMGameEvent("LIFE_EXCHANGE")
            .time(180).name("生命交换").describe("攻击生物时会互换生命值")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                serverLevel.players().forEach(player -> {
                    if (player.getLastHurtMob() != null&&
                            serverLevel.getGameTime() - player.getLastHurtMobTimestamp() < 2) {
                        LivingEntity target = player.getLastHurtMob();
                        float playerHealth = player.getHealth();
                        float targetHealth = target.getHealth();

                        player.setHealth(targetHealth);
                        target.setHealth(playerHealth);
                    }
                });
            });

    public static final ESMGameEvent TIME_FREEZE = new ESMGameEvent("TIME_FREEZE")
            .time(60).name("时间冻结").describe("所有实体（除玩家）将无法移动")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                serverLevel.getAllEntities().forEach(entity -> {
                    if (!(entity instanceof Player)) {
                        entity.setDeltaMovement(Vec3.ZERO);
                        if (entity instanceof LivingEntity livingEntity) {
                            livingEntity.setDeltaMovement(Vec3.ZERO);
                        }
                    }
                });
            });



    public static final ESMGameEvent MIRROR_CLONES = new ESMGameEvent("MIRROR_CLONES")
            .time(120).name("幻影分身").describe("生成会攻击玩家的克隆体")
            .appearType(ESMGameEvent.AppearType.TICK)
            .appear(serverLevel -> {
                if (GameProcess.CommonEvent.tick % 200 == 0) {
                    serverLevel.players().forEach(player -> {
                        Zombie clone = new Zombie(EntityType.ZOMBIE, serverLevel);
                        clone.setCustomName(player.getName().copy().append("的影子"));
                        clone.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0);
                        clone.setTarget(player);
                        serverLevel.addFreshEntity(clone);
                    });
                }
            });
}
