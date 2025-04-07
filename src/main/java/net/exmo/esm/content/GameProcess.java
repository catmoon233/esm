package net.exmo.esm.content;

import net.exmo.esm.Esm;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static com.mojang.text2speech.Narrator.LOGGER;
import static net.exmo.esm.Esm.workQueue;
import static net.exmo.esm.content.GameConfig.*;
import static net.exmo.esm.content.ScoreboardManager.*;

public class GameProcess {
//    public static final int MAX_HEALTH = 30;
//    public static final int DONTDOROUND = 88;
//    public static final int NEXT_QUEST_TIME = 68;
//    public static final int BORDER_SIZE = 320;

//    public static boolean seeOnlyOneDonDo = false;
    public static boolean startFire = false;
    public static ServerBossEvent nextDontDo ;
    public static ServerBossEvent nextDo ;
    public static CommandSourceStack source;
    public static String nowQuest ="";

//    public static int getLastNextDoTime(Level level){
//        return level.getScoreboard().getOrCreatePlayerScore("system", Objects.requireNonNull(level.getScoreboard().getObjective("esm_dontdo"))).getScore();
//    }
//    public static void setLastNextDoTime(Level level,int time){
//        level.getScoreboard().getOrCreatePlayerScore("system", Objects.requireNonNull(level.getScoreboard().getObjective("esm_dontdo"))).setScore(time);
//    }
//    public static int getLastNextDoTime(Scoreboard scoreboard){
//        return scoreboard.getOrCreatePlayerScore("system", Objects.requireNonNull(scoreboard.getObjective("esm_dontdo"))).getScore();
//    }
//    public static void setLastNextDoTime(Scoreboard scoreboard,int time){
//        scoreboard.getOrCreatePlayerScore("system", Objects.requireNonNull(scoreboard.getObjective("esm_dontdo"))).setScore(time);
//    }
//    public static int getNextQuestTime(Scoreboard sc){
//        return sc.getOrCreatePlayerScore("system", Objects.requireNonNull(sc.getObjective("esm_quest"))).getScore();
//    }
//    public static void setNextQuestTime(Scoreboard sc,int time){
//        sc.getOrCreatePlayerScore("system", Objects.requireNonNull(sc.getObjective("esm_quest"))).setScore(time);
//    }
//    public static int getQuestRound(Scoreboard sc) {
//        return sc.getOrCreatePlayerScore("system", Objects.requireNonNull(sc.getObjective("esm_quest_round"))).getScore();
//    }
//
//    public static void setQuestRound(Scoreboard sc, int round) {
//        sc.getOrCreatePlayerScore("system", Objects.requireNonNull(sc.getObjective("esm_quest_round"))).setScore(round);
//    }
//    public static int getDondoRound(Scoreboard sc) {
//        return sc.getOrCreatePlayerScore("system", Objects.requireNonNull(sc.getObjective("esm_dondo_round"))).getScore();
//    }
//
//    public static void setDondoRound(Scoreboard sc, int round) {
//        sc.getOrCreatePlayerScore("system", Objects.requireNonNull(sc.getObjective("esm_dondo_round"))).setScore(round);
//    }


    public static ServerBossEvent prepareStart ;
    public static ServerBossEvent gameEventBossBar ;
    //玩家档案

    public static Map<String, PlayerGameProfiler> player_profiler = new HashMap<>();

    public static PlayerGameProfiler getProfiler(ServerPlayer player) {
        if (!player_profiler.containsKey(player.getScoreboardName())) {
            player_profiler.put(player.getScoreboardName(), new PlayerGameProfiler());
        }
        return player_profiler.get(player.getScoreboardName());
    }

    public static MinecraftServer currentServer ;
    static int startDownTime = 0;

    public static enum GameState {
        PREPARE(0),
        IN_GAME(1),
        END(2);
        public final int anInt;

        GameState(int i) {
            this.anInt = i;
        }
    }

    public static boolean inGame(){
        return  getScore(currentServer.getScoreboard(), "game_state", "system") == GameState.IN_GAME.anInt;

    }
    private static Component getTeamMembersList(Scoreboard scoreboard, String teamName) {
        MutableComponent list = Component.empty();
        boolean first = true;

        for (String member : scoreboard.getPlayerTeam(teamName).getPlayers()) {
            if (!first) list.append(Component.literal("§7, "));
            list.append(Component.literal(member).withStyle(ChatFormatting.WHITE));
            first = false;
        }
        return list;
    }

    public static void start(ServerLevel level) {
        //初始化血量

        ServerScoreboard scoreboard = level.getScoreboard();
//        if (scoreboard.hasObjective("esm_health")) scoreboard.removeObjective(Objects.requireNonNull(scoreboard.getObjective("esm_health")));
//        if (!scoreboard.hasObjective("esm_health")) {
//            scoreboard.addObjective("esm_health", ObjectiveCriteria.DUMMY, Component.literal("健康值"), ObjectiveCriteria.RenderType.INTEGER);
//            scoreboard.setDisplayObjective(1, scoreboard.getObjective("esm_health"));
//        }
//        if (!scoreboard.hasObjective("esm_dontdo"))   scoreboard.addObjective("esm_dontdo",ObjectiveCriteria.DUMMY, Component.literal("禁止事件倒计时"), ObjectiveCriteria.RenderType.INTEGER);
//        if (!scoreboard.hasObjective("esm_quest"))   scoreboard.addObjective("esm_quest",ObjectiveCriteria.DUMMY, Component.literal("任务倒计时"), ObjectiveCriteria.RenderType.INTEGER);
//        if (!scoreboard.hasObjective("esm_dondo_round"))     scoreboard.addObjective("esm_quest_round",ObjectiveCriteria.DUMMY, Component.literal("任务回合"), ObjectiveCriteria.RenderType.INTEGER);
//        if (!scoreboard.hasObjective("esm_dondo_round"))    scoreboard.addObjective("esm_dondo_round",ObjectiveCriteria.DUMMY, Component.literal("禁止事件回合"), ObjectiveCriteria.RenderType.INTEGER);

        // 特殊处理健康值计分板
        handleHealthObjective(scoreboard);


        // 初始化其他计分板
        for (ScoreboardManager.ScoreboardObjective obj : ScoreboardManager.ScoreboardObjective.values()) {
            if (obj == ScoreboardManager.ScoreboardObjective.HEALTH) continue; // 已单独处理
            createIfAbsent(scoreboard, obj);
        }
            ScoreboardManager.setScore(scoreboard, "game_state", "system",GameState.IN_GAME.anInt);

        scoreboard.addPlayerTeam("r1").setColor(ChatFormatting.RED);
        scoreboard.addPlayerTeam("r2").setColor(ChatFormatting.BLUE);
        scoreboard.addPlayerTeam("r3").setColor(ChatFormatting.GREEN);
        scoreboard.addPlayerTeam("r4").setColor(ChatFormatting.YELLOW);
        scoreboard.addPlayerTeam("r5").setColor(ChatFormatting.AQUA);
        scoreboard.addPlayerTeam("r6").setColor(ChatFormatting.GOLD);
        scoreboard.addPlayerTeam("r7").setColor(ChatFormatting.LIGHT_PURPLE);
        scoreboard.addPlayerTeam("r8").setColor(ChatFormatting.WHITE);

        setQuestRound(scoreboard,0);
        setDondoRound(scoreboard,0);


        player_profiler = new HashMap<>();
        if (nextDontDo!=null){
            nextDontDo.setVisible(false);
        }
        if (nextDo!=null){
            nextDo.setVisible(false);
        }
        if (gameEventBossBar!=null){
            gameEventBossBar.setVisible(false);
        }
        setLastNextDoTime(level,DONTDOROUND.get());
        setNextQuestTime(level.getScoreboard(),NEXT_QUEST_TIME.get());
        List<String> list = ESMGameEventHandle.events.keySet().stream().toList();
        ESMGameEvent event= ESMGameEventHandle.events.get(list.get(level.getRandom().nextInt(ESMGameEventHandle.events.size())));
        GameEventManager.setCurrentGameEvent(level,event.id);
        setScore(level.getScoreboard(), "next_game_event", "system",event.time);
        nextDontDo = new ServerBossEvent(
                Component.literal("§c下一个禁止事件 "+getLastNextDoTime(level)+"秒"),
                ServerBossEvent.BossBarColor.RED,
                ServerBossEvent.BossBarOverlay.PROGRESS
        );
        nextDo = new ServerBossEvent(
                Component.literal("§a下一个任务 "+getNextQuestTime(level.getScoreboard())+"秒"),
                ServerBossEvent.BossBarColor.GREEN,
                ServerBossEvent.BossBarOverlay.PROGRESS
        );
        gameEventBossBar = new ServerBossEvent(
                Component.literal("§b当前游戏事件: "+ESMGameEventHandle.findEvent(GameEventManager.getCurrentGameEvent(level)).name.getString()+" "+getScore(level.getScoreboard(), "next_game_event", "system"+"秒")),
                ServerBossEvent.BossBarColor.BLUE,
                ServerBossEvent.BossBarOverlay.PROGRESS
        );
        Vec3 randomPositionCenter = new Vec3(
                level.getRandom().nextInt(100000) - 50000,
                250,
                level.getRandom().nextInt(100000) - 50000
        );
        level.getWorldBorder().setCenter(
                randomPositionCenter.x,
                randomPositionCenter.z
        );

         source = new CommandSourceStack(
                CommandSource.NULL,
                randomPositionCenter,
                Vec2.ZERO,
                level,
                4,
                level.getSharedSpawnPos().toString(),
                Component.literal(""),
                level.getServer(),
                null
        );
        level.getWorldBorder().setSize(BORDER_SIZE.get());
        level.getWorldBorder().setDamageSafeZone(2);



        level.setWeatherParameters(-1, 0, false, false);
        level.setDayTime(1000);

        level.getWorldBorder().setDamagePerBlock(2);
        currentServer.getCommands().performPrefixedCommand(source,"title @a title \"\\u00a7a游戏开始\" ");

        var strings = GameChallengeHandle.challenges.keySet().stream().filter(e-> !Objects.equals(e, "NONE")).toList();
        var challenge = strings.get(new Random().nextInt(strings.size()));
        nowQuest = GameChallengeHandle.challenges.get(challenge).name.getString();

        // 在 GameProcess.java 的 start 方法中添加以下代码（在创建队伍之后，玩家初始化之前）
        List<ServerPlayer> allPlayers = new ArrayList<>(level.players());
        Collections.shuffle(allPlayers); // 随机打乱顺序

        int totalPlayers = allPlayers.size();
        Integer i1 = TEAM_NUMBER.get();
        int basePerTeam = totalPlayers / i1;
        int remainder = totalPlayers % i1;

        int index = 0;
        for (int teamIndex = 0; teamIndex < i1; teamIndex++) {
            int teamSize = basePerTeam + (teamIndex < remainder ? 1 : 0);

            for (int i = 0; i < teamSize; i++) {
                if (index >= totalPlayers) break;

                ServerPlayer player = allPlayers.get(index++);
                String teamName = "r" + (teamIndex + 1);

                // 将玩家加入队伍
                scoreboard.addPlayerToTeam(player.getScoreboardName(), scoreboard.getPlayerTeam(teamName));

                // 设置队伍颜色标识
                player.setGlowingTag(true);
                switch (teamIndex) {
                    case 0 -> player.displayClientMessage(Component.literal("你的队伍：红队").withStyle(ChatFormatting.RED), false);
                    case 1 -> player.displayClientMessage(Component.literal("你的队伍：蓝队").withStyle(ChatFormatting.BLUE), false);
                    case 2 -> player.displayClientMessage(Component.literal("你的队伍：绿队").withStyle(ChatFormatting.GREEN), false);
                    case 3 -> player.displayClientMessage(Component.literal("你的队伍：黄队").withStyle(ChatFormatting.YELLOW), false);
                    case 4 -> player.displayClientMessage(Component.literal("你的队伍：青队").withStyle(ChatFormatting.AQUA), false);
                    case 5 -> player.displayClientMessage(Component.literal("你的队伍：橙队").withStyle(ChatFormatting.GOLD), false);
                    case 6 -> player.displayClientMessage(Component.literal("你的队伍：紫队").withStyle(ChatFormatting.LIGHT_PURPLE), false);
                    case 7 -> player.displayClientMessage(Component.literal("你的队伍：白队").withStyle(ChatFormatting.WHITE), false);
                }

            }
        }
        List<ItemStack> startedItems = new ArrayList<>(List.of(
                new ItemStack(Items.IRON_AXE),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(Items.IRON_SHOVEL),
                new ItemStack(Items.IRON_PICKAXE)

        ));

        level.players().forEach(player -> {
            Collections.shuffle(startedItems,RANDOM);
            player.getInventory().clearContent();
            player.addItem(startedItems.get(0));


            PlayerTeam playersTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
            if (playersTeam != null){
            player.displayClientMessage(
                    Component.literal("§a你的队友：")
                            .append(getTeamMembersList(scoreboard, playersTeam.getName()))
                            .withStyle(ChatFormatting.BOLD),
                    false);
            }
            player.heal(200);
            player.setGameMode(GameType.SURVIVAL);
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 10));
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 100, 10));

            nextDo.addPlayer(
                    player
            );
            gameEventBossBar.addPlayer(
                    player
            );
            nextDontDo.addPlayer(
                    player
            );
            level.getServer().getAdvancements().getAllAdvancements().forEach(
                    e->{
                        player.getAdvancements().revoke(
                                e,
                                "esm.advancement.fail"
                        );
                    }
            );


            Vec3 randomPosition = randomPositionCenter.add(
                    level.getRandom().nextInt(10) - 20,
                    0,
                    level.getRandom().nextInt(10) - 20
            );

            for (int i = 0; i < 200; i++){
                if (level.getBlockState(
                        new BlockPos((int) randomPosition.x, (int) randomPosition.y, (int) randomPosition.z)
                ).isAir()){
                    randomPosition = new Vec3(randomPosition.x, randomPosition.y - 1, randomPosition.z);
                }
            }

            player.setRespawnPosition(
                    level.dimension(),
                    new BlockPos(
                            (int) randomPosition.x,
                            (int) randomPosition.y,
                            (int) randomPosition.z
                    ),
                    0,
                    true,
                    true
            );
            player.teleportTo(randomPosition.x, randomPosition.y, randomPosition.z);
            player.connection
                    .send(new ClientboundSetEntityMotionPacket(player.getId(),new Vec3(0,-5,0)));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 10));
          PlayerGameProfiler.setHealth(MAX_HEALTH.get(),level.getScoreboard(),player.getScoreboardName());

          changeDontChallenge(player);
          changeQuestChallenge(player,challenge);




        });
//        level.players().forEach(e -> {
//            e.level().players().forEach(
//                    a -> {
//                        if (a instanceof ServerPlayer player) {
//                            if (player != e) {
//                                GameDontDoChallenge dontDoChallenge = GameProcess.getProfiler(player).getDontDoChallenge();
//                                e.sendSystemMessage(
//                                        Component.literal(player.getScoreboardName() + "的禁止事件为: ").append(dontDoChallenge.name).withStyle(ChatFormatting.AQUA)
//                                );
//                                e.sendSystemMessage(
//                                       Component.empty().append(dontDoChallenge.describe).withStyle(ChatFormatting.GRAY)
//                                );
//                            }
//                        }
//                    }
//            );
//
//
//        });
                playerTell.clear();
                displayOtherPeopleInfo(true,true);
                playerTell.clear();


        nextDontDo.setVisible(true);
        nextDo.setVisible(true);
        ;
    }
    public static void over(Scoreboard scoreboard) {
        //初始化血量
        player_profiler = new HashMap<>();
        currentServer.getPlayerList().getPlayers().forEach(player -> {


            PlayerGameProfiler.setHealth(MAX_HEALTH.get(),scoreboard,player.getScoreboardName());



            player.sendSystemMessage(
                  Component.literal("§7游戏结束")
                              .withStyle(style ->
                                      style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§7等待下一个游戏开启")))
                              )
          );
            sendTitle("§4游戏结束");
        });
        ScoreboardManager.setScore(scoreboard, "game_state", "system",GameState.PREPARE.anInt);

        if (nextDontDo!=null){
            nextDontDo.setVisible(false);
        }
        if (nextDo!=null){
            nextDo.setVisible(false);
        }
    }

    @Mod.EventBusSubscriber
    public static class CommonEvent {
        @SubscribeEvent
        public static void tick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
                workQueue.forEach(work -> {
                    work.setValue(work.getValue() - 1);
                    if (work.getValue() == 0)
                        actions.add(work);
                });
                actions.forEach(e -> e.getKey().run());
                workQueue.removeAll(actions);
            }
        }
        public static int tick = 0;
        @SubscribeEvent
        public static void ServerTick(TickEvent.ServerTickEvent event){
            if (event.phase == TickEvent.Phase.END) {
                ServerScoreboard scoreboard = event.getServer().getScoreboard();
                event.getServer().getFunctions().get(ResourceLocation.parse("esm:tick")).ifPresent(commandFunction -> {
                    event.getServer().getFunctions().execute(commandFunction, event.getServer().createCommandSourceStack().withSuppressedOutput().withPermission(4));
                });
                if (currentServer==null)currentServer = ServerLifecycleHooks.getCurrentServer();
                List<ServerPlayer> players = event.getServer().getPlayerList().getPlayers();

                tick++;
                if (tick % 20 == 0) {
                    if (ScoreboardManager.getScore(scoreboard, "game_state", "system") == GameState.PREPARE.anInt){
                        if (startDownTime==10){
                            prepareStart = new ServerBossEvent(Component.literal("游戏即将开始"), ServerBossEvent.BossBarColor.GREEN, ServerBossEvent.BossBarOverlay.PROGRESS);
                            players.forEach(e -> {
                                prepareStart.addPlayer(e);
                            });
                            prepareStart.setVisible(true);
                        }
                        if (startDownTime >1) {
                            startDownTime--;
                            prepareStart.setProgress(startDownTime/10f);
                        }else if (startDownTime==1){
                            prepareStart.setVisible(false);
                            startDownTime--;
                        }
                    }
                    if (inGame()) {





                      // 新增游戏结束判断逻辑
                        if ( checkLastTeamStanding(scoreboard)) {
                            currentServer.getPlayerList().broadcastSystemMessage(
                                    Component.literal("§6游戏结束！获胜队伍：")
                                            .append(getWinningTeamName(scoreboard))
                                            .withStyle(ChatFormatting.GOLD),
                                    false
                            );
                            over(scoreboard); // 需要传递 ServerLevel 参数，需在方法参数中添加
                        }

                        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
                        int next_game_event_time = getScore(scoreboard, "next_game_event", "system");
                        ESMGameEvent event1 = ESMGameEventHandle.findEvent(GameEventManager.getCurrentGameEvent(level));
                        if (next_game_event_time>0){
                            int cutTime = next_game_event_time - 1;
                            setScore(scoreboard, "next_game_event", "system", cutTime);
                            if (gameEventBossBar==null) {
                                gameEventBossBar = new ServerBossEvent(
                                        Component.literal("§b当前游戏事件: " + event1.name.getString() + " " + cutTime + "秒"),
                                        ServerBossEvent.BossBarColor.BLUE,
                                        ServerBossEvent.BossBarOverlay.PROGRESS
                                );
                            }
                                gameEventBossBar.setProgress((float) cutTime / event1.time);
                                gameEventBossBar.setName(
                                        Component.literal("§b当前游戏事件: "+ event1.name.getString()+" "+cutTime+"秒")
                                );
                                players.forEach(
                                        e -> {
                                            gameEventBossBar.addPlayer(e);
                                        }
                                );


                        }
                        if (next_game_event_time<=0){
                            List<String> list = ESMGameEventHandle.events.keySet().stream().toList();
                            ESMGameEvent event2= ESMGameEventHandle.events.get(list.get(level.getRandom().nextInt(ESMGameEventHandle.events.size())));
                            setScore(scoreboard, "next_game_event", "system", event2.time);
                            GameEventManager.setCurrentGameEvent(level,event2.id);
                            players.forEach(
                                    e->{
                                        sendTitle("§b游戏事件已更新");
                                        e.sendSystemMessage(
                                                Component.literal("§7------------------------")

                                        );
                                        e.sendSystemMessage(
                                                Component.literal("§b游戏事件已更新")
                                                        .withStyle(ChatFormatting.GOLD)
                                        );
                                        e.sendSystemMessage(
                                                Component.literal("§6当前游戏事件: "+event2.name.getString()).withStyle(ChatFormatting.GOLD)
                                        );
                                        e.sendSystemMessage(
                                                Component.literal("§7介绍: "+event2.describe.getString()).withStyle(ChatFormatting.GRAY)
                                        );
                                        e.playNotifySound(SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1, 1);

                                    }
                            );

                        }
                        if (getLastNextDoTime(scoreboard) > 0){
                            setLastNextDoTime(scoreboard, getLastNextDoTime(scoreboard) - 1);

                            int lastNextDoTime = getLastNextDoTime(scoreboard);
                            if (nextDontDo==null){
                                nextDontDo = new ServerBossEvent(
                                        Component.literal("§c下一个禁止事件 "+getLastNextDoTime(level)+"秒"),
                                        ServerBossEvent.BossBarColor.RED,
                                        ServerBossEvent.BossBarOverlay.PROGRESS
                                );


                            }
                            nextDontDo.setProgress((float) lastNextDoTime /DONTDOROUND.get());
                            nextDontDo.setName(
                                    Component.literal("§7下一个禁止事件: §a" + lastNextDoTime)
                            );
                            players.forEach(
                                    e -> {
                                        nextDontDo.addPlayer(e);
                                    }
                            );
                        }
                        if (getNextQuestTime(scoreboard) > 0){
                            setNextQuestTime(scoreboard, getNextQuestTime(scoreboard) - 1);

                            int nextQuestTime = getNextQuestTime(scoreboard);
                            if (nextDo==null){
                                nextDo = new ServerBossEvent(
                                        Component.literal("§a下一个任务 "+getNextQuestTime(level.getScoreboard())+"秒"),
                                        ServerBossEvent.BossBarColor.GREEN,
                                        ServerBossEvent.BossBarOverlay.PROGRESS
                                );

                            }
                            nextDo.setProgress((float) nextQuestTime /NEXT_QUEST_TIME.get());

                            nextDo.setName(
                                    Component.literal("§7下一个任务: §a" + nextQuestTime +" §7当前:"+ nowQuest)
                            );
                            players.forEach(
                                    e -> {
                                        nextDo.addPlayer(e);
                                    }
                            );
                        }
                        if (getLastNextDoTime(scoreboard) <= 0){
                            setDondoRound(
                                    scoreboard,
                                    getDondoRound(scoreboard)+1
                            );
                            setLastNextDoTime(scoreboard, DONTDOROUND.get());
                            sendTitle("§a禁止事件已更新");
                            players.forEach(
                                    e->{
                                        e.sendSystemMessage(Component.literal(
                                                "§7------------------------"
                                        ));

                                        e.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 1, 1);
                                        e.sendSystemMessage(
                                                Component.literal("§b当前禁止事件回合: "+getDondoRound(scoreboard))
                                        );
                                        changeDontChallenge(e);


                                    }
                            );
                            playerTell.clear();
                            displayOtherPeopleInfo(true,false);
                            playerTell.clear();
                        }


                        if (getNextQuestTime(scoreboard) <= 0){
                            setQuestRound(
                                    scoreboard,
                                    getQuestRound(scoreboard)+1
                            );
                            setNextQuestTime(scoreboard, NEXT_QUEST_TIME.get());
                            var strings = GameChallengeHandle.challenges.keySet().stream().filter(e-> !Objects.equals(e, "NONE")).toList();
                            var challenge = strings.get(new Random().nextInt(strings.size()));
                            players.forEach(
                                    e->{
                                        e.sendSystemMessage(Component.literal(
                                                "§7------------------------"
                                        ));
                                    }
                            );
                            List<String> finishQuest= new ArrayList<>();
                            List<String> failQuest= new ArrayList<>();
                            sendTitle("§a任务事件已更新");
                            players.forEach(
                                    e->{

                                        PlayerGameProfiler profiler = getProfiler(e);
                                        e.sendSystemMessage(
                                                Component.literal("§b当前任务事件回合: "+getDondoRound(scoreboard))
                                        );

                                        e.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 1, 1);
                                        if (!profiler.gameOver) {
                                            if (!profiler.finishQuest) {
                                                profiler.shinkHeath(3, e);
                                                failQuest.add(e.getScoreboardName());

                                            } else {
                                                finishQuest.add(e.getScoreboardName());
                                            }
                                            changeQuestChallenge(e, challenge);
                                        }
                                    }
                            );

                            players.forEach(
                                    e->{
                                            if (!finishQuest.isEmpty())        e.displayClientMessage(Component.literal("§l"+String.join("§l,",finishQuest)).append(" §a完成了任务 §7->").append(nowQuest), false);
                                            if (!failQuest.isEmpty())        e.displayClientMessage(Component.literal("§7"+String.join("§7,",failQuest)).append(" §4未完成任务 §7->").append(nowQuest), false);

                                    }


                            );
                            nowQuest = GameChallengeHandle.challenges.get(challenge).name.getString();
                            MinecraftForge.EVENT_BUS.post(new QuestUpdateEvent(scoreboard));
                            playerTell.clear();
                            displayOtherPeopleInfo(false,true);
                            playerTell.clear();


                            if (getQuestRound(scoreboard) >= PVP_ROUND.get() && !startFire){
                                startFire = true;
                                players.forEach( e->{
                                    e.sendSystemMessage(
                                            Component.literal("§7------------------------")
                                    );
                                    e.sendSystemMessage(
                                            Component.literal("§4战斗开始")
                                    );
                                    e.sendSystemMessage(
                                            Component.literal("§6每死亡一次 扣三点点数")
                                    );
                                    e.sendSystemMessage(
                                            Component.literal("§6边界开始缩小")
                                    );
                                    WorldBorder worldBorder = level.getWorldBorder();

                                    worldBorder.setWarningTime(1000);
                                    event.getServer().getCommands().performPrefixedCommand(source, "worldborder set 20 1000");
                                    worldBorder.lerpSizeBetween(worldBorder.getSize(),20, 1000000);
                                    e.playNotifySound(SoundEvents.ENDER_DRAGON_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);
                                        }

                                );
                                sendTitle("§4§l战斗开始");

                            }
                        }
                    }
                }
            }
        }
        // 检查是否只剩一个存活队伍
        private static boolean checkLastTeamStanding(Scoreboard scoreboard) {
            Map<PlayerTeam, Integer> aliveCount = new HashMap<>();

            // 统计各队伍存活玩家数
            currentServer.getPlayerList().getPlayers().forEach(player -> {
                if (PlayerGameProfiler.getHealth(scoreboard, player.getScoreboardName()) > 0) {
                    PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
                    aliveCount.put(team, aliveCount.getOrDefault(team, 0) + 1);
                }
            });

            // 过滤掉无存活玩家的队伍
            long activeTeams = aliveCount.values().stream()
                    .filter(count -> count > 0)
                    .count();

            return activeTeams <= 1;
        }

        // 获取胜利队伍名称
        private static Component getWinningTeamName(Scoreboard scoreboard) {
            return currentServer.getPlayerList().getPlayers().stream()
                    .filter(p -> PlayerGameProfiler.getHealth(scoreboard, p.getScoreboardName()) > 0)
                    .findFirst()
                    .map(p -> {
                        Component displayName = scoreboard.getPlayersTeam(p.getScoreboardName()).getDisplayName();
                        String string = displayName.getString();
                        MutableComponent literal = getPlayerTeamColor(string);
                        if (literal != null) return literal;
                        return displayName;

                    })
                    .orElse(Component.literal("无人幸存"));
        }

        public static void serverLoad() {
        currentServer = ServerLifecycleHooks.getCurrentServer();
        currentServer.getPlayerList().getPlayers().forEach(
                e ->{
                    e.sendSystemMessage(
                            Component.literal("§7---------------------")
                    );
                    e.sendSystemMessage(

                            Component.literal("§6欢迎游玩: §b控制我")
                    );
                    e.sendSystemMessage(
                            Component.literal("§a开始游戏").withStyle(
                                    style ->
                                    {

                                        style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§7管理员点击后，十秒后游戏开启")));
                                        style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/esm game start"));

                                        return style;
                                    }
                            )
                    );
                    e.sendSystemMessage(
                            Component.literal("§7设置")
                    );
                    e.sendSystemMessage(
                            Component.literal("§7---------------------")
                    );
                }
        );

    }
}

    private static @Nullable MutableComponent getPlayerTeamColor(String string) {
        switch (string) {
          case "r1" -> {
              return Component.literal("§c红队");
          }
          case "r2" -> {
              return Component.literal("§b蓝队");
          }
          case "r3" -> {
              return Component.literal("§a绿队");
          }
          case "r4" -> {
              return Component.literal("§e黄队");
          }
          case "r5" -> {
              return Component.literal("§e青队");
          }
          case "r6" -> {
              return Component.literal("§e橙队");
          }
          case "r7" -> {
              return Component.literal("§e紫队");
          }
          case "r8" -> {
              return Component.literal("§e白队");
          }
      }
        return null;
    }

    public static void sendTitle(String content) {
        if (GameProcess.source!=null){
            GameProcess.currentServer.getCommands().performPrefixedCommand(
                    GameProcess.source,
                    "title @a title \""+content+" \" "
            );
        }
    }
private static final List<ServerPlayer> playerTell = new ArrayList<>();
    private static final Random RANDOM = new SecureRandom();
    private static void displayOtherPeopleInfo(boolean isDonDo,boolean isQuest) {
        currentServer.getPlayerList().getPlayers().forEach(e -> {
            PlayerGameProfiler profiler = getProfiler(e);
            if (isQuest){
                GameChallenge gameChallenge = profiler.getChallenge();
                e.sendSystemMessage(
                        Component.literal("任务已更新:").append(gameChallenge.name).withStyle(ChatFormatting.GOLD)
                );
                e.sendSystemMessage(
                        Component.empty().append(gameChallenge.describe).withStyle(ChatFormatting.GRAY)
                );
            }
            if (isDonDo) {
                List<? extends Player> players = e.level().players();

                if (profiler.gameOver || !SEE_ONLY_ONE_DONDO.get()) {
                    players.stream().filter(a -> a != e && a instanceof ServerPlayer serverPlayer && !getProfiler(serverPlayer).gameOver).forEach(
                            a -> {

                                if (a instanceof ServerPlayer player) {
                                    playerDonDoInfo(e, player);

                                }
                            }

                    );
                }
                if (SEE_ONLY_ONE_DONDO.get() && !profiler.gameOver) {
                    List<ServerPlayer> candidates = players.stream()
                            .filter(p -> p instanceof ServerPlayer
                                    && p != e
                                    && !getProfiler((ServerPlayer) p).gameOver
                                    && !playerTell.contains(p))
                            .map(p -> (ServerPlayer) p)
                            .collect(Collectors.toCollection(ArrayList::new)); // 使用可修改的集合

                    if (!candidates.isEmpty()) {
                        // 使用更可靠的随机选择方式
                        Collections.shuffle(candidates, RANDOM); // 先打乱顺序
                        ServerPlayer selected = candidates.get(0); // 取第一个

                        // 优化添加逻辑
                        playerTell.add(selected);
                        playerDonDoInfo(e, selected);
                    } else {
                        // 优化日志输出
                        LOGGER.debug("没有可用的玩家进行选择");
                        // 可以添加恢复逻辑（如果需要）
                      //  playerTell.clear(); // 清空已选列表以便重新开始
                    }

                }
            }


        });
    }

    private static void playerDonDoInfo(ServerPlayer e, ServerPlayer player) {
        GameDontDoChallenge dontDoChallenge = GameProcess.getProfiler(player).getDontDoChallenge();
        if (dontDoChallenge != null) {
            ServerScoreboard scoreboard = currentServer.getScoreboard();
            ChatFormatting chatFormatting = ChatFormatting.GRAY;
            PlayerTeam playersTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
            if (playersTeam != null) {
                chatFormatting = playersTeam.getColor();
            }
            e.sendSystemMessage(
                    Component.literal(player.getScoreboardName()).withStyle(chatFormatting).append(Component.literal("§b的禁止事件为: ")).append(dontDoChallenge.name)
            );
            e.sendSystemMessage(
                    Component.empty().append(dontDoChallenge.describe).withStyle(ChatFormatting.GRAY)
            );
        }
    }

    private static void changeDontChallenge(ServerPlayer e) {
        PlayerGameProfiler profiler = getProfiler(e);
        if (profiler.gameOver)return;
        profiler.failRule =false;
        var strings = GameDontDoChallengeHandle.challenges.keySet().stream().filter(a -> !Objects.equals(a, GameDontDoChallengeHandle.NONE.id)).toList();
        profiler.dontDoChallenge = strings.get(new Random().nextInt(strings.size()));
        PlayerNBTManager.saveToNBT(
                e,
                profiler
        );

    }
    private static void changeQuestChallenge(ServerPlayer e,String challenge) {
        PlayerGameProfiler profiler = getProfiler(e);
        if (profiler.gameOver)return;

        profiler.finishQuest =false;

        profiler.challenge = challenge;
        PlayerNBTManager.saveToNBT(
                e,
                profiler
        );

    }
}
