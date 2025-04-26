package net.exmo.esm.content;

import java.util.HashMap;
import java.util.Map;

public class GameDontDoChallengeHandle {
    public static Map<String,GameDontDoChallenge> challenges =new HashMap<>();
    public static GameDontDoChallenge DON_JUMP = new GameDontDoChallenge("DON_JUMP")
            .name("禁止跳跃").describe("玩家不能跳跃");
    public static GameDontDoChallenge NONE = new GameDontDoChallenge("NONE")
            .name("无").describe("故障时使用");
    //禁止玩家跳跃
    public static GameDontDoChallenge DON_USE_WORK_BLOCKS = new GameDontDoChallenge("DON_USE_WORK_BLOCKS")
            .name("不！许！用！").describe("不能使用工作方块");
    //禁止使用工作方块
    public static GameDontDoChallenge DON_TAKE_DAMAGE = new GameDontDoChallenge("DON_TAKE_DAMAGE")
            .name("脆骨症").describe("不能受到任何伤害");
    //禁止受到伤害
    public static GameDontDoChallenge DON_HURT_FRIENDLY_MOBS = new GameDontDoChallenge("DON_HURT_FRIENDLY_MOBS")
            .name("圣母之心").describe("不能攻击友善动物");
    //禁止攻击友善生物
    public static GameDontDoChallenge DON_HURT_HOSTILE_MOBS = new GameDontDoChallenge("DON_HURT_HOSTILE_MOBS")
            .name("战斗狂魔").describe("不能攻击非友善生物");
    //禁止攻击非友善生物
    public static GameDontDoChallenge DON_PUNCH_BLOCKS = new GameDontDoChallenge("DON_PUNCH_BLOCKS")
            .name("无力症").describe("不能撸方块");
    //禁止撸方块
    public static GameDontDoChallenge DON_USE_TOOLS = new GameDontDoChallenge("DON_USE_TOOLS")
            .name("有气无力症").describe("不能使用工具");
    //禁止使用工具
    public static GameDontDoChallenge DON_EAT_MEAT = new GameDontDoChallenge("DON_EAT_MEAT")
            .name("素食者").describe("不能食用肉类食物");
//    //禁止食用肉类食物
//    public static GameDontDoChallenge DON_EAT_VEGETARIAN_FOOD = new GameDontDoChallenge("DON_EAT_VEGETARIAN_FOOD")
//            .name("肉食者").describe("");
    //禁止使用素类食物
    public static GameDontDoChallenge DON_GET_BUFFS = new GameDontDoChallenge("DON_GET_BUFFS")
            .name("高敏体").describe("不能获得任意buff");
    //禁止获得任意buff
    public static GameDontDoChallenge DON_ATTACK_PLAYERS = new GameDontDoChallenge("DON_ATTACK_PLAYERS")
            .name("诚信").describe("不能攻击玩家");
    //禁止攻击玩家
    public static GameDontDoChallenge DON_HURT_NON_PLAYER_MOBS = new GameDontDoChallenge("DON_HURT_NON_PLAYER_MOBS")
            .name("忘本").describe("不能攻击除玩家外的所有生物");
    //禁止攻击除玩家外的所有生物
    public static GameDontDoChallenge DON_NEARBY_DEATH = new GameDontDoChallenge("DON_NEARBY_DEATH")
            .name("存护").describe("不能有玩家在周围死亡");
    //禁止玩家在周围死亡
    public static GameDontDoChallenge DON_HP_BELOW_HALF = new GameDontDoChallenge("DON_HP_BELOW_HALF")
            .name("稳一点，稳一点...").describe("自己生命值不能低于一半");
    //禁止自己生命值低于一半
    public static GameDontDoChallenge DON_MELEE_WEAPON = new GameDontDoChallenge("DON_MELEE_WEAPON")
            .name("七步之内枪又准又快").describe("不能使用武器（仅近战）");
    //禁止使用武器（近战）
    public static GameDontDoChallenge DON_RANGED_WEAPON = new GameDontDoChallenge("DON_RANGED_WEAPON")
            .name("七步之内刀快").describe("不能使用武器（仅远程）");
    //禁止使用武器（远程）
    public static GameDontDoChallenge DON_PLAYER_CLOSE = new GameDontDoChallenge("DON_PLAYER_CLOSE")
            .name("洁癖").describe("不能其他玩家贴贴（靠近）");
    //禁止和其他玩家贴贴（靠近）
    public static GameDontDoChallenge DON_TOUCH_WATER = new GameDontDoChallenge("DON_TOUCH_WATER")
            .name("干旱者").describe("不能接触水");
    //禁止接触水
    public static GameDontDoChallenge DON_SWIM = new GameDontDoChallenge("DON_SWIM")
            .name("我不会游.....咕噜咕噜~").describe("不能游泳");
    //禁止游泳
    public static GameDontDoChallenge DON_SPRINT = new GameDontDoChallenge("DON_SPRINT")
            .name("身娇体弱").describe("不能疾跑");
    //禁止疾跑
//    public static GameDontDoChallenge DON_PLACE_FULL_BLOCK = new GameDontDoChallenge("DON_PLACE_FULL_BLOCK")
//            .name("我不放！").describe("不能放置完整方块（半砖除外）");
    //禁止放置完整方块（半砖除外）
    public static GameDontDoChallenge DON_WEAR_ARMOR = new GameDontDoChallenge("DON_WEAR_ARMOR")
            .name("自信").describe("不能穿戴护甲");
    public static GameDontDoChallenge DONT_HAND_ITEM = new GameDontDoChallenge("DON_WEAR_ARMOR")
            .name("手无寸铁").describe("副手不能拿东西");
    //禁止穿戴护甲
    public static GameDontDoChallenge DON_USE_SHIELD = new GameDontDoChallenge("DON_USE_SHIELD")
            .name("只攻不防").describe("不能使用盾牌格挡");
    //禁止使用盾牌格挡
    public static GameDontDoChallenge DON_SNEAK = new GameDontDoChallenge("DON_SNEAK")
            .name("潜行-100级").describe("不能潜行");
//    //禁止潜行
//    public static GameDontDoChallenge DON_LOOK_UP = new GameDontDoChallenge("DON_LOOK_UP")
//            .name("不要抬头").describe("不能看向天空（视角朝向天空）");
//    //禁止看向天空（视角朝向天空）
//    public static GameDontDoChallenge DON_LOOK_DOWN = new GameDontDoChallenge("DON_LOOK_DOWN")
//            .name("不要低头").describe("不能看向地板（视角朝向底部）");
    //禁止看向地板（视角朝向底部）
    public static GameDontDoChallenge DON_GET_RAINED = new GameDontDoChallenge("DON_GET_RAINED")
            .name("避水").describe("不能淋雨");
    //禁止淋雨（但每天会50%的概率降雨）
    public static GameDontDoChallenge DON_OPEN_INVENTORY = new GameDontDoChallenge("DON_OPEN_INVENTORY")
            .name("我的手足以").describe("不能使用背包（仅限于玩家自主打开，工作方块界面等除外）");
    //禁止使用背包（仅限于玩家自主打开，工作方块界面等除外）
    public static GameDontDoChallenge DON_DROP_ITEMS = new GameDontDoChallenge("DON_DROP_ITEMS")
            .name("不给不给！").describe("不能丢出物品");
    //禁止丢出物品
    public static GameDontDoChallenge DON_USE_CONTAINERS = new GameDontDoChallenge("DON_USE_CONTAINERS")
            .name("我的背包足够大！").describe("不能使用容器");
    //禁止使用容器
    public static GameDontDoChallenge DON_PLACE_LIGHT = new GameDontDoChallenge("DON_PLACE_LIGHT")
            .name("惧光者").describe("不能使用光源");
    //禁止使用光源
    public static GameDontDoChallenge DON_BRIGHT_LIGHT_10 = new GameDontDoChallenge("DON_BRIGHT_LIGHT_10")
            .name("惧光者之子").describe("不能被大于10点的光源照到");
    //禁止被大于10点的光源照到
    public static GameDontDoChallenge DON_DIM_LIGHT_8 = new GameDontDoChallenge("DON_DIM_LIGHT_8")
            .name("喜光者").describe("不能走进小于8点的光源的位置");
    //禁止走进小于8点的光源的位置
    public static GameDontDoChallenge DON_FULL_HUNGER = new GameDontDoChallenge("DON_FULL_HUNGER")
            .name("刚刚好").describe("不能恢复满饱食度");
    //禁止恢复满饱食度

    public static GameDontDoChallenge DON_ENTER_3X3_SPACE = new GameDontDoChallenge("DON_ENTER_3X3_SPACE")
            .name("幽秘恐惧症").describe("不能进入小于3*3的空间");
    //禁止进入小于3*3的空间
    public static GameDontDoChallenge DON_NIGHT_MOVE = new GameDontDoChallenge("DON_NIGHT_MOVE").name("")
            .describe("在夜晚行动（在夜晚时头顶没有方块）");
    //禁止在夜晚行动（在夜晚时头顶没有方块）
    public static GameDontDoChallenge DON_NON_WEAPON_ATTACK = new GameDontDoChallenge("DON_NON_WEAPON_ATTACK")
            .name("我只会用这个").describe("不能使用除武器外其他方式攻击生物");
    //禁止使用除武器外其他方式攻击生物
    public static GameDontDoChallenge DON_ANY_WEAPON_ATTACK = new GameDontDoChallenge("DON_ANY_WEAPON_ATTACK")
            .name("偏科").describe("使用任意武器攻击生物（比如斧头一类的除外）");
    //禁止使用任意武器攻击生物（比如斧头一类的除外）
    public static GameDontDoChallenge DON_USE_OVER6_DAMAGE = new GameDontDoChallenge("DON_USE_OVER6_DAMAGE")
            .name("攻速快才是真的快").describe("不能使用伤害大于6点的武器或工具");
    //禁止使用伤害大于6点的武器或工具
    public static GameDontDoChallenge DON_FULL_INVENTORY = new GameDontDoChallenge("DON_FULL_INVENTORY")
            .name("裤兜满啦").describe("不能让当前物品栏栏位满格");
    //禁止当前物品栏栏位满格
    public static GameDontDoChallenge DON_USE_CRAFTING_GRID = new GameDontDoChallenge("DON_USE_CRAFTING_GRID")
            .name("糟糕...忘带了").describe("不能使用背包合成栏");
    //禁止使用背包合成栏
    public static GameDontDoChallenge DON_PLACE_ANY_BLOCK = new GameDontDoChallenge("DON_PLACE_ANY_BLOCK")
            .name("不可以！").describe("不能放置所有方块");
    //禁止放置所有方块
    public static GameDontDoChallenge DON_TOOL_MINING = new GameDontDoChallenge("DON_TOOL_MINING")
            .name("有力无气症").describe("不能使用工具挖掘方块");
    //禁止使用工具挖掘方块
    public static GameDontDoChallenge DON_NEAR_ATTACKING = new GameDontDoChallenge("DON_NEAR_ATTACKING")
            .name("二极管").describe("周围不能有玩家在攻击生物");
    //禁止周围玩家在攻击生物
    public static GameDontDoChallenge DON_NEAR_RAINED = new GameDontDoChallenge("DON_NEAR_RAINED")
            .name("带着我的伞").describe("周围不能有玩家淋雨");
    //禁止周围玩家淋雨
    public static GameDontDoChallenge DON_NEAR_WORKING = new GameDontDoChallenge("DON_NEAR_WORKING")
            .name("自私").describe("周围不能有玩家使用工作方块");
    //禁止周围玩家使用工作方块
    public static GameDontDoChallenge DON_NEAR_EATING = new GameDontDoChallenge("DON_NEAR_EATING")
            .name("看的我都饿了").describe("不能让周围玩家吃食物");
    //禁止周围玩家吃食物
//    public static GameDontDoChallenge DON_NEAR_PUNCHING = new GameDontDoChallenge("DON_NEAR_PUNCHING")
//            .name("这是我的地盘！").describe("");
//    //禁止周围玩家撸方块
    public static GameDontDoChallenge DON_SELF_BURN = new GameDontDoChallenge("DON_SELF_BURN")
            .name("hero死啦！").describe("自身不能受到燃烧");
    //禁止自身燃烧
    public static GameDontDoChallenge DON_HIGH_Y_AXIS = new GameDontDoChallenge("DON_HIGH_Y_AXIS")
            .name("恐高症").describe("不能前往y轴过高的地方");
    //禁止前往y轴过高的地方
    public static GameDontDoChallenge DON_HIGH_YYYYY = new GameDontDoChallenge("DON_HIGH_YYYYY")
            .name("恐人症").describe("5*5范围内不能有其他玩家");
    //禁止5*5范围内有其他玩家
    public static GameDontDoChallenge DON_HIGH_YACGF = new GameDontDoChallenge("DON_HIGH_YACGF")
            .name("绿色恐惧").describe("不能站在草地上");
    //禁止站在草地上
    public static GameDontDoChallenge DON_HIGH_HHHHHHH = new GameDontDoChallenge("DON_HIGH_HHHHHHH")
            .name("贪生怕死").describe("周围不能有非友善生物");
    //禁止周围有非友善生物
    public static GameDontDoChallenge DON_HIGH_XXXXXXXX = new GameDontDoChallenge("DON_HIGH_XXXXXXXX")
            .name("贪死贪生").describe("不能有一定幅度的血量变动");
    //禁止有血量变动
    public static GameDontDoChallenge DON_HIGH_CCCCCCCCCC = new GameDontDoChallenge("DON_HIGH_CCCCCCCCCC")
            .name("w~co！").describe("不能使用左键与右键");
    //禁止左键与右键
}
