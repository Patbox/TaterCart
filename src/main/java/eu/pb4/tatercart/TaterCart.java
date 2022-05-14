package eu.pb4.tatercart;

import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.other.TcGameRules;
import eu.pb4.tatercart.recipes.ShulkerMinecartRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TaterCart implements ModInitializer {
    public static final String ID = "tatercart";
    public static final boolean SHOW_MARKER = false && FabricLoader.getInstance().isDevelopmentEnvironment();
    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        TcBlocks.register();
        TcItems.register();
        TcEntities.register();
        TcGameRules.register();

        Registry.register(Registry.RECIPE_SERIALIZER, id("shulker_minecart_recipe"), ShulkerMinecartRecipe.SERIALIZER);
    }
}
