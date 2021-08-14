package eu.pb4.tatercart;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.devtech.arrp.json.recipe.JIngredient;
import net.devtech.arrp.json.recipe.JIngredients;
import net.devtech.arrp.json.recipe.JRecipe;
import net.devtech.arrp.json.recipe.JResult;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.devtech.arrp.api.RuntimeResourcePack.id;
import static net.devtech.arrp.json.loot.JLootTable.*;

public class TcDataPack {
    public static final RuntimeResourcePack PACK = new RuntimeResourcePackImpl(TaterCartMod.id("main"));

    public static void create() {
        RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
    }

    public static void createSimpleDrop(Block block) {
        PACK.addLootTable(id(block.getLootTableId().toString()),
                loot("minecraft:block")
                        .pool(pool()
                                .rolls(1)
                                .entry(entry()
                                        .type("minecraft:item")
                                        .name(Registry.ITEM.getId(block.asItem()).toString()))
                                .condition(predicate("minecraft:survives_explosion")))
        );
    }

    public static void createTag(Identifier id, Block... blocks) {
        var tag = JTag.tag();

        for (var block : blocks) {
            tag.add(Registry.BLOCK.getId(block));
        }

        PACK.addTag(id, tag);
    }

    public static void createCraftingShapeless(Item item, int count, Object... ingredients) {
        var jIngredients = JIngredients.ingredients();

        for (var obj : ingredients) {
            var ingredient = JIngredient.ingredient();

            if (obj instanceof Item) {
                ingredient.item((Item) obj);
            } else {
                ingredient.tag((String) obj);
            }

            jIngredients.add(ingredient);
        }

        var identifier = Registry.ITEM.getId(item);

        PACK.addRecipe(new Identifier(identifier.getNamespace(), "craft_" + identifier.getPath()),
                JRecipe.shapeless(
                        jIngredients,
                        JResult.item(item)
                ));
    }
}
