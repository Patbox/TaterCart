package eu.pb4.tatercart;

import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.item.TcItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class TaterCartMod implements ModInitializer {
    public static final String ID = "tatercart";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    public static GameRules.Key<GameRules.BooleanRule> DEFAULT_ENHANCED
            = GameRuleRegistry.register("tatercart:default_enhanced", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    @Override
    public void onInitialize() {


        TcBlocks.register();
        TcItems.register();
        TcEntities.register();

        TcBlocks.registerData();
        TcItems.registerData();

        TcDataPack.create();
    }
}
