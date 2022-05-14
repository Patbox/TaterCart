package eu.pb4.tatercart.other;

import eu.pb4.tatercart.TaterCart;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class TcGameRules {
    public static GameRules.Key<GameRules.BooleanRule> DEFAULT_ENHANCED
            = GameRuleRegistry.register(TaterCart.ID + ":default_enhanced", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> MINECART_HIGH_SPEED_DAMAGE
            = GameRuleRegistry.register(TaterCart.ID + ":minecart_high_speed_damage", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> EXTENDED_RAILS_PLACEMENT
            = GameRuleRegistry.register(TaterCart.ID + ":extended_rails_placement", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> MINECART_LINKING
            = GameRuleRegistry.register(TaterCart.ID + ":minecart_linking", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> SPLIT_ITEMS
            = GameRuleRegistry.register(TaterCart.ID + ":split_items", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

    public static void register() {
    }
}
