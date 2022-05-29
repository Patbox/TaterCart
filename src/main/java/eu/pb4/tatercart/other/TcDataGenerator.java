package eu.pb4.tatercart.other;

import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.item.TcItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.TagKey;

import java.util.function.Consumer;

/**
 * Switch to fabric datagen
 */
public class TcDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(RecipeProvider::new);
        fabricDataGenerator.addProvider(BlockTagProvider::new);
        fabricDataGenerator.addProvider(BlockLootTableProvider::new);

        /*fabricDataGenerator.addProvider(ItemTagProvider::new);
        fabricDataGenerator.addProvider(BlockLootTableProvider::new);*/
    }

    private static class RecipeProvider extends FabricRecipeProvider {
        private RecipeProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
            TcItems.createRecipes(new RecipeBuilder(exporter));
        }
    }

    public record RecipeBuilder(Consumer<RecipeJsonProvider> exporter) {
        public void createSimpleMinecart(Item result, int count, Object mainItem, Consumer<CraftingRecipeJsonBuilder> modifier) {
            this.createShaped(result, count, (b) -> {
                b.pattern("i ");
                b.pattern("m ");

                if (mainItem instanceof ItemConvertible item) {
                    b.input('i', item);
                } else if (mainItem instanceof TagKey item) {
                    b.input('i', item);
                } else if (mainItem instanceof Ingredient item) {
                    b.input('i', item);
                }

                b.input('m', Items.MINECART);

                modifier.accept(b);
            });
        }

        public void createShapeless(Item result, int count, Object[] items, Consumer<CraftingRecipeJsonBuilder> modifier) {
            var b = new ShapelessRecipeJsonBuilder(result, count);

            for (var obj : items) {
                if (obj instanceof ItemConvertible item) {
                    b.input(item);
                } else if (obj instanceof TagKey item) {
                    b.input(item);
                } else if (obj instanceof Ingredient item) {
                    b.input(item);
                }
            }

            modifier.accept(b);

            b.offerTo(exporter);
        }

        public void createShaped(Item result, int count, Consumer<ShapedRecipeJsonBuilder> modifier) {
            var b = new ShapedRecipeJsonBuilder(result, count);
            modifier.accept(b);
            b.offerTo(exporter);
        }
    }

    private static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
        private BlockTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            TcBlocks.createTags((tagKey, blocks) -> {
                this.getOrCreateTagBuilder(tagKey).add(blocks);
            });
        }
    }

    public static class BlockLootTableProvider extends FabricBlockLootTableProvider {
        private BlockLootTableProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateBlockLootTables() {
            TcBlocks.createDrops(this);
        }
    }


    /*public static void createSimpleDrop(Block block) {
        PACK.addLootTable(id(block.getLootTableId().toString()),
                loot("minecraft:block")
                        .pool(pool()
                                .rolls(1)
                                .entry(entry()
                                        .type("minecraft:item")
                                        .name(Registry.ITEM.getId(block.asItem()).toString()))
                                .condition(predicate("minecraft:survives_explosion")))
        );
    }*/
}
