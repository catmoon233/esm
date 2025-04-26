package net.exmo.esm.content;

import java.util.HashMap;
import java.util.Map;

public class GameChallengeHandle {
    public static Map<String,GameChallenge> challenges =new HashMap<>();

    public static GameChallenge getChallenge(String id) {
        return challenges.get(id);
    }

    public static final GameChallenge CHALLENGE_IRON_OR_TOOL = new GameChallenge("CHALLENGE_IRON_OR_TOOL")
            .name("老铁").describe("需要拥有铁锭或者任意铁工具");
    public static final GameChallenge RIDE_HOUSE = new GameChallenge("CHALLENGE_IRON_OR_TOOL")
            .name("老马").describe("骑到一只马上，会在地图中心刷新");
    public static final GameChallenge NONE = new GameChallenge("NONE")
            .name("无").describe("当前事件回合没有事件");
    //一定要拥有铁锭或者任意铁工具
    public static final GameChallenge RANDOM_ITEMS = new GameChallenge("RANDOM_ITEMS")
            .name("寻觅者").describe("手持一样物品");
    //获取随机物品
     public static final GameChallenge RANDOM_FOOD_CHALLENGE = new GameChallenge("RANDOM_FOOD_CHALLENGE")
            .name("大胃王").describe("吃一样物品").time(90).weight(11f);
    //吃随机东西
     public static final GameChallenge RANDOM_BLOCK_CHALLENGE = new GameChallenge("RANDOM_BLOCK_CHALLENGE")
            .name("MOUSE!").describe("挖一样东西").time(10);
    //挖随机方块
     public static final GameChallenge RANDOM_ARMOR_CHALLENGE = new GameChallenge("RANDOM_ARMOR_CHALLENGE")
            .name("时尚!").describe("装备一样东西").time(120);
    //挖随机方块
    public static final GameChallenge CHALLENGE_NEAR_PLAYER = new GameChallenge("CHALLENGE_NEAR_PLAYER")
            .name("集合！").describe("需要周围内有其他玩家").weight(11f);
    //一定要周围内有其他玩家
    public static final GameChallenge CHALLENGE_HAS_FOOD = new GameChallenge("CHALLENGE_HAS_FOOD")
            .name("饿啊~").describe("需要拥有任意食物").time(20);
    //一定要拥有任意食物
    public static final GameChallenge CHALLENGE_HAS_MEAT = new GameChallenge("CHALLENGE_HAS_MEAT")
            .name("优质蛋白质").describe("需要拥有任意肉类食物").time(20);
    //一定要拥有任意肉类食物
    public static final GameChallenge CHALLENGE_HAS_VEGGIE = new GameChallenge("CHALLENGE_HAS_VEGGIE")
            .name("优质植物纤维").describe("需要拥有任意素类食物").time(20);
    //一定要拥有任意素类食物
    public static final GameChallenge CHALLENGE_HOLD_W = new GameChallenge("CHALLENGE_HOLD_W")
            .name("不要停下来啊！").describe("需要一直前进（按住w）").time(10);
    //一定要前进（按住w）
    public static final GameChallenge CHALLENGE_HAS_DIAMOND = new GameChallenge("CHALLENGE_HAS_DIAMOND")
            .name("闪闪！").describe("需要拥有一颗钻石").time(150).weight(7f);
    //一定要拥有一颗钻石
    public static final GameChallenge CHALLENGE_HAS_GOLD = new GameChallenge("CHALLENGE_HAS_GOLD")
            .name("亮亮！").describe("需要拥有一个金锭").time(150).weight(7f);
    //一定要拥有一个金锭
    public static final GameChallenge CHALLENGE_HAS_TOOL = new GameChallenge("CHALLENGE_HAS_TOOL")
            .name("手拿把掐").describe("需要拥有任意工具").time(180);
    //一定要拥有任意工具
    public static final GameChallenge CHALLENGE_HAS_ARMOR = new GameChallenge("CHALLENGE_HAS_ARMOR")
            .name("武装！").describe("需要拥有任意盔甲").time(90);
    //一定要拥有任意盔甲
    public static final GameChallenge CHALLENGE_EAT_FOOD = new GameChallenge("CHALLENGE_EAT_FOOD")
            .name("饿！").describe("需要食用任意食物").time(30);
    //一定要食用任意食物
    public static final GameChallenge CHALLENGE_HEALTH_HALF = new GameChallenge("CHALLENGE_HEALTH_HALF")
            .name("安心的血量").describe("需要当前生命值大于一半").time(10);
    //一定要当前生命值大于一半
    public static final GameChallenge CHALLENGE_HEALTH_LESS_HALF = new GameChallenge("CHALLENGE_HEALTH_LESS_HALF")
            .name("令人不安的血量").describe("需要当前生命值小于一半").time(20);
    //一定要当前生命值小于一半
    public static final GameChallenge CHALLENGE_HAS_SATURATION = new GameChallenge("CHALLENGE_HAS_SATURATION")
            .name("嗝").describe("需要拥有饱和度").time(15);
    //一定要拥有饱和度
    public static final GameChallenge CHALLENGE_HOLD_WOOD = new GameChallenge("CHALLENGE_HOLD_WOOD")
            .name("区区原木").describe("需要手持任意木头").time(15);
    //一定要手持任意木头
    public static final GameChallenge CHALLENGE_NEAR_BLOCK = new GameChallenge("CHALLENGE_NEAR_BLOCK")
            .name("中途休息").describe("需要周围有任意功能方块").time(20);
    //一定要周围有任意功能方块
    public static final GameChallenge CHALLENGE_TAKE_DAMAGE = new GameChallenge("CHALLENGE_TAKE_DAMAGE")
            .name("呃啊~").describe("需要损失任意自身血量").time(10);
    //一定要掉血
    public static final GameChallenge CHALLENGE_PLACE_ITEM = new GameChallenge("CHALLENGE_PLACE_ITEM")
            .name("放放放！").describe("需要放置任意物品（包括按钮一类）").time(10);
    //一定要放置任意物品（包括按钮一类）
    public static final GameChallenge CHALLENGE_NEAR_WATER = new GameChallenge("CHALLENGE_NEAR_WATER")
            .name("吨吨吨~").describe("需要周围有水").time(85);
    //一定要周围有水
    public static final GameChallenge CHALLENGE_KILL_MOB = new GameChallenge("CHALLENGE_KILL_MOB")
            .name("黑鲨~").describe("需要击杀一个生物").time(90);
    //一定要击杀一个生物
    public static final GameChallenge CHALLENGE_NEAR_PORTAL = new GameChallenge("CHALLENGE_NEAR_PORTAL")
            .name("（发出地狱门的声音）").describe("需要周围有地狱门").time(180).weight(4f);
    //一定要周围有地狱门
    public static final GameChallenge CHALLENGE_DROP_ITEM = new GameChallenge("CHALLENGE_DROP_ITEM")
            .name("我！不！要！啦！").describe("需要丢出一个物品").time(10);
    //一定要丢出一个物品
    public static final GameChallenge CHALLENGE_HAS_APPLE = new GameChallenge("CHALLENGE_HAS_APPLE")
            .name("亚当の果").describe("需要拥有一个苹果").time(80);
    //一定要拥有一个苹果
    public static final GameChallenge CHALLENGE_NO_CEILING = new GameChallenge("CHALLENGE_NO_CEILING")
            .name("头顶空荡荡，玩家在人间").describe("需要头顶没有方块").time(60).weight(9f);
    //一定要头顶没有方块
    public static final GameChallenge CHALLENGE_ATTACK_PLAYER = new GameChallenge("CHALLENGE_ATTACK_PLAYER")
            .name("以牙还牙！").describe("需要攻击一次其他玩家").time(90);
    //一定要攻击一次其他玩家
    public static final GameChallenge CHALLENGE_HAS_EMERALD = new GameChallenge("CHALLENGE_HAS_EMERALD")
            .name("哦，闪亮").describe("需要拥有一颗绿宝石").time(180);
    //一定要拥有一颗绿宝石
    public static final GameChallenge CHALLENGE_NEAR_MOB = new GameChallenge("CHALLENGE_NEAR_MOB")
            .name("非无人之境").describe("需要周围有任意生物").time(30).weight(7);
    //一定要周围有任意生物
    public static final GameChallenge CHALLENGE_NEAR_LAVA = new GameChallenge("CHALLENGE_NEAR_LAVA")
            .name("烫烫烫").describe("需要周围有岩浆").time(100);
    //一定要周围有岩浆
    public static final GameChallenge CHALLENGE_HOLD_S = new GameChallenge("CHALLENGE_HOLD_S")
            .name("我的眼睛长在了屁股上").describe("需要一直后退（按住s）").time(5);
    //一定要一直后退（按住s）
    public static final GameChallenge CHALLENGE_HOLD_WEAPON = new GameChallenge("CHALLENGE_HOLD_WEAPON")
            .name("※~※~※~").describe("需要拿着武器").time(12);
    //一定要拿着武器
    public static final GameChallenge CHALLENGE_BELOW_Y30 = new GameChallenge("CHALLENGE_BELOW_Y30")
            .name("资深矿工").describe("需要在y轴低于30的地方").time(55);
    //一定要在y轴低于45的地方
    public static final GameChallenge CHALLENGE_DONW_JIYAN = new GameChallenge("CHALLENGE_DONW_JIYAN")
            .name("聚众淫乱").describe("需要周围至少有两个除自己以外的玩家").time(75);
    //一定要周围至少有两个除自己以外的玩家
    public static final GameChallenge CHALLENGE_CC_Y30 = new GameChallenge("CHALLENGE_CC_PUM")
            .name("我也会飞啦").describe("需要在y轴高于100的地方").time(80);
    //一定要在y轴高于120的地方
    public static final GameChallenge CHALLENGE_CAR = new GameChallenge("CHALLENGE_CAR")
            .name("深入").describe("需要站在基岩上").time(120).weight(8f);
    //一定要站在基岩上
    public static final GameChallenge CHALLENGE_PPIC = new GameChallenge("CHALLENGE_PPIC")
            .name("亮！").describe("需要站在亮度大于11的地方").time(25);
    //一定要站在亮度大于11的地方
    public static final GameChallenge CHALLENGE_CUMOP = new GameChallenge("CHALLENGE_CUMOP")
            .name("喝！为什么不喝！").describe("需要饮用水瓶").time(75);
    //一定要饮用水瓶
    public static final GameChallenge CHALLENGE_CCCCCC = new GameChallenge("CHALLENGE_CCCCCC")
            .name("撒勒倪！").describe("需要击杀一名玩家").time(180).weight(9f);
    //一定要击杀一名玩家
    public static final GameChallenge CHALLENGE_AAAAAAA = new GameChallenge("CHALLENGE_AAAAAAA")
            .name("啊啊啊啊啊啊啊！").describe("需要被其他玩家攻击5次").time(120).weight(6f);
    //一定要被其他玩家攻击5次
    public static final GameChallenge CHALLENGE_HHHHHHH = new GameChallenge("CHALLENGE_HHHHHHH")
            .name("贴贴！！！").describe("需要和其他玩家贴贴").time(80);
    //一定要其他玩家贴贴
    public static final GameChallenge CHALLENGE_XXXHHHH = new GameChallenge("CHALLENGE_XXXHHHH")
            .name("贪死怕生").describe("需要单次掉血大于10点").time(95);
    //一定要单次掉血大于10点
    public static final GameChallenge CHALLENGE_AS = new GameChallenge("CHALLENGE_AS")
            .name("齐步，走！").describe("需要向前行走八十格").time(40);
    //一定要向前行走八十格
    public static final GameChallenge CHALLENGE_AC = new GameChallenge("CHALLENGE_AC")
            .name("我拿了mvp！").describe("需要击杀三只任意生物").time(120);
    //一定要击杀三只任意生物
    public static final GameChallenge CHALLENGE_AF = new GameChallenge("CHALLENGE_AF")
            .name("就解决你啊").describe("需要死亡一次").time(50).weight(6f);
    //一定要死亡一次
    public static final GameChallenge CHALLENGE_AG = new GameChallenge("CHALLENGE_AG")
            .name("").describe("需要在聊天栏发言20次").time(20);
    //一定要发言20次
    public static final GameChallenge CHALLENGE_AH = new GameChallenge("CHALLENGE_AH")
            .name("将大局逆转吧！开！").describe("需要一击必杀一只任意生物").time(65).weight(9f);
    //一定要一击必杀一只任意生物
    public static final GameChallenge CHALLENGE_AR = new GameChallenge("CHALLENGE_AR")
            .name("吃我一发阿姆斯特朗回旋加速喷气式阿姆斯特朗炮！").describe("需要在聊天栏发送:阿姆斯特朗回旋加速喷气式阿姆斯特朗炮").time(15);
    //一定要在聊天栏发送:阿姆斯特朗回旋加速喷气式阿姆斯特朗炮
    public static final GameChallenge CHALLENGE_CY_TY_ZZ_YX_DD_CC = new GameChallenge("CHALLENGE_CY_TY_ZZ_YX_DD_CC")
            .name("哎呀，吓到你啦？！").describe("需要在聊天栏发送:“残月美貌无双”，或者“天衣沉鱼落雁”，“早早是大铸币”，“你们都是大铸币”等，任意其一").time(10).weight(16f);
    //一定要在聊天栏发送一些固定文字
}
