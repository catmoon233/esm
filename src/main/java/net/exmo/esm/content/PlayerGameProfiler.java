package net.exmo.esm.content;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.Objects;

public class PlayerGameProfiler {
    public String challenge ="";
    public String dontDoChallenge ="";
    public boolean failRule = false;
    public boolean gameOver=  false;
    public boolean finishQuest = false;
    public static int getHealth(Scoreboard scoreboard,String playerName){
        return scoreboard.getOrCreatePlayerScore(playerName, (scoreboard.getObjective("esm_health"))).getScore();
    }
    public static void setHealth(int i,Scoreboard scoreboard,String playerName){
        scoreboard.getOrCreatePlayerScore(playerName, (scoreboard.getObjective("esm_health"))).setScore(i);
    }
    public GameDontDoChallenge getDontDoChallenge() {
        GameDontDoChallenge gameDontDoChallenge = GameDontDoChallengeHandle.challenges.get(dontDoChallenge);
        if (gameDontDoChallenge ==null)return GameDontDoChallengeHandle.NONE;
        return gameDontDoChallenge;
    }
    public String getActiveDontChallenge(){
        return dontDoChallenge;
    }
    public String getActiveChallenge(){
        return challenge;
    }
    public GameChallenge getChallenge() {

        GameChallenge gameChallenge = GameChallengeHandle.challenges.get(challenge);
        if (gameChallenge ==null)return GameChallengeHandle.NONE;
        return gameChallenge;
    }

    public void recordViolation(String string, ServerPlayer player){
        getDontDoChallenge().fail(Component.literal(string),player);
    }
    public void finishQuest(String string, ServerPlayer player){
        getChallenge().finnish(Component.literal(string),player);
    }

    public void addHeath(int i,Player player) {
        setHealth(Math.min(getHealth(player.getScoreboard(),player.getScoreboardName())+i,GameConfig.MAX_HEALTH.get()),player.getScoreboard(),player.getScoreboardName());
        int health = getHealth(player.getScoreboard(), player.getScoreboardName());
        GameProcess.currentServer.getPlayerList().getPlayers().forEach(
                player1 -> {

                    player1.sendSystemMessage(
                            Component.literal(player.getScoreboardName()+" §7增加§a"+i+"§7点数").append(
                                    Component.literal(" §7("+health+"/"+GameConfig.MAX_HEALTH.get()+")")
                            )


                    );
                    if (health >= GameConfig.MAX_HEALTH.get()) {
//                       player1.sendSystemMessage(
//                                Component.literal("\\u00a7a"+player.getScoreboardName() + " 超出!")
//                    );
                    }
                }
        );
    }
    public void shinkHeath(int i,Player player) {
        if (gameOver) return;
        setHealth(getHealth(player.getScoreboard(),player.getScoreboardName())-i,player.getScoreboard(),player.getScoreboardName());
        int health = getHealth(player.getScoreboard(), player.getScoreboardName());
        GameProcess.currentServer.getPlayerList().getPlayers().forEach(
                player1 -> {
                    PlayerTeam playersTeam = player.getScoreboard().getPlayersTeam(player1.getScoreboardName());
                    ChatFormatting color = ChatFormatting.GRAY;
                    if (playersTeam!=null){
                        color = playersTeam.getColor();
                    }
                    player1.sendSystemMessage(
                            Component.literal(player.getScoreboardName()).withStyle(color).append(Component.literal(" §7扣除§4"+i+"§7点数")).append(
                                    Component.literal(" §7("+health+"/"+GameConfig.MAX_HEALTH.get()+")")
                            )


                    );
                    if (health <= 0) {
                        String a = "§4" + player.getScoreboardName() + " OUT!";
                        if (GameProcess.source!=null){
                            GameProcess.currentServer.getCommands().performPrefixedCommand(
                                    GameProcess.source,
                                    "title @a title \"\\u00a74"+a+" \" "
                            );
                        }

                        player1.sendSystemMessage(
                                Component.literal(a)
                        );
                    }
                }
        );
        if (health<=0 && !gameOver){
            if (player instanceof ServerPlayer serverPlayer){
                serverPlayer.setGameMode(GameType.SPECTATOR);
                this.gameOver =true;
                PlayerNBTManager.saveToNBT(
                        serverPlayer,
                        GameProcess.getProfiler(serverPlayer)
                );
            }
        }

    }
}
