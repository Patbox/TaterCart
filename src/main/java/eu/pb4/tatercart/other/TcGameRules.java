package eu.pb4.tatercart.other;

import eu.pb4.tatercart.TaterCart;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class TcGameRules {
    public static GameRules.Key<GameRules.BooleanRule> DEFAULT_ENHANCED
            = GameRuleRegistry.register(TaterCart.ID + ":default_enhanced", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> MINECART_HIGH_SPEED_DAMAGE
            = GameRuleRegistry.register(TaterCart.ID + ":minecart_high_speed_damage", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> EXTENDED_RAILS_PLACEMENT
            = GameRuleRegistry.register(TaterCart.ID + ":extended_rails_placement", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> MINECART_LINKING
            = GameRuleRegistry.register(TaterCart.ID + ":minecart_linking", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<DoubleRule> MAX_MINECART_SPEED
            = GameRuleRegistry.register(TaterCart.ID + ":max_speed", GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(24, 1, 100));

    public static GameRules.Key<DoubleRule> DEFAULT_MINECART_SPEED
            = GameRuleRegistry.register(TaterCart.ID + ":default_speed", GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(22, 1, 100));

    public static GameRules.Key<DoubleRule> UNDERWATER_SPEED_PERCENTAGE
            = GameRuleRegistry.register(TaterCart.ID + ":underwater_speed_percentage", GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(40, 1, 100));

    public static GameRules.Key<DoubleRule> POWERED_BOOST_VALUE
            = GameRuleRegistry.register(TaterCart.ID + ":powered_rails_boost", GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(0.03, 0.0, 10));

    public static void register() {
    }

    public static double getUnderwaterSpeedPercentage(World world) {
        return world.getGameRules().get(TcGameRules.UNDERWATER_SPEED_PERCENTAGE).get() / 100;
    }
}
