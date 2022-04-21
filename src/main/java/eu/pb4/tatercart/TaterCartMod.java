package eu.pb4.tatercart;

import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.recipes.ShulkerMinecartRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;

import java.util.ArrayList;

public class TaterCartMod implements ModInitializer {
    public static final String ID = "tatercart";
    public static final boolean SHOW_MARKER = true && FabricLoader.getInstance().isDevelopmentEnvironment();

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    public static GameRules.Key<GameRules.BooleanRule> DEFAULT_ENHANCED
            = GameRuleRegistry.register("tatercart:default_enhanced", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static GameRules.Key<GameRules.BooleanRule> EXTENDED_RAILS_PLACEMENT
            = GameRuleRegistry.register("tatercart:extended_rails_placement", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    @Override
    public void onInitialize() {


        TcBlocks.register();
        TcItems.register();
        TcEntities.register();

        Registry.register(Registry.RECIPE_SERIALIZER, id("shulker_minecart_recipe"), ShulkerMinecartRecipe.SERIALIZER);

        TcBlocks.registerData();
        TcItems.registerData();

        TcDataPack.create();
    }
}
