package net.exmo.esm.content;

import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Objects;

public class ScoreboardManager {


    // 计分板目标定义
    public enum ScoreboardObjective {
        HEALTH("esm_health", "§e顺从值", ObjectiveCriteria.RenderType.INTEGER),
        DONT_DO("esm_dontdo", "禁止事件倒计时", ObjectiveCriteria.RenderType.INTEGER),
        QUEST("esm_quest", "任务倒计时", ObjectiveCriteria.RenderType.INTEGER),
        QUEST_ROUND("esm_quest_round", "任务回合", ObjectiveCriteria.RenderType.INTEGER),
        DONDO_ROUND("esm_dondo_round", "禁止事件回合", ObjectiveCriteria.RenderType.INTEGER),
        GAME_STATE("game_state", "游戏状态", ObjectiveCriteria.RenderType.INTEGER);

        final String name;
        final Component displayName;
        final ObjectiveCriteria.RenderType renderType;

        ScoreboardObjective(String name, String displayName, ObjectiveCriteria.RenderType renderType) {
            this.name = name;
            this.displayName = Component.literal(displayName);
            this.renderType = renderType;
        }
    }


    public static void handleHealthObjective(ServerScoreboard scoreboard) {
        Objective existing = scoreboard.getObjective("esm_health");
        if (existing != null) scoreboard.removeObjective(existing);

        ScoreboardObjective health = ScoreboardObjective.HEALTH;
        Objective objective = scoreboard.addObjective(
                health.name,
                ObjectiveCriteria.DUMMY,
                health.displayName,
                health.renderType
        );
        scoreboard.setDisplayObjective(1, objective);
    }

    public static void createIfAbsent(ServerScoreboard scoreboard, ScoreboardObjective obj) {
        if (!scoreboard.hasObjective(obj.name)) {
            scoreboard.addObjective(
                    obj.name,
                    ObjectiveCriteria.DUMMY,
                    obj.displayName,
                    obj.renderType
            );
        }
    }

    // 通用分数操作
    public static int getScore(Scoreboard scoreboard, String objectiveName, String player) {
        if (!scoreboard.hasObjective(objectiveName)) return -1;
        return scoreboard.getOrCreatePlayerScore(
                player,
                Objects.requireNonNull(scoreboard.getObjective(objectiveName))
        ).getScore();
    }

    public static void setScore(Scoreboard scoreboard, String objectiveName, String player, int value) {
        scoreboard.getOrCreatePlayerScore(
                player,
                Objects.requireNonNull(scoreboard.getObjective(objectiveName))
        ).setScore(value);
    }

    // 专用方法保持兼容性
    public static int getLastNextDoTime(Level level) {
        return getScore(level.getScoreboard(), "esm_dontdo", "system");
    }

    public static void setLastNextDoTime(Level level, int time) {
        setScore(level.getScoreboard(), "esm_dontdo", "system", time);
    }

    public static int getLastNextDoTime(Scoreboard scoreboard) {
        return getScore(scoreboard, "esm_dontdo", "system");
    }

    public static void setLastNextDoTime(Scoreboard scoreboard, int time) {
        setScore(scoreboard, "esm_dontdo", "system", time);
    }

    public static int getNextQuestTime(Scoreboard sc) {
        return getScore(sc, "esm_quest", "system");
    }

    public static void setNextQuestTime(Scoreboard sc, int time) {
        setScore(sc, "esm_quest", "system", time);
    }

    public static int getQuestRound(Scoreboard sc) {
        return getScore(sc, "esm_quest_round", "system");
    }

    public static void setQuestRound(Scoreboard sc, int round) {
        setScore(sc, "esm_quest_round", "system", round);
    }

    public static int getDondoRound(Scoreboard sc) {
        return getScore(sc, "esm_dondo_round", "system");
    }

    public static void setDondoRound(Scoreboard sc, int round) {
        setScore(sc, "esm_dondo_round", "system", round);
    }
}
