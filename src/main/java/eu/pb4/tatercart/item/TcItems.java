package eu.pb4.tatercart.item;

import eu.pb4.polymer.api.item.PolymerItemGroup;
import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.other.TcDataGenerator;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static eu.pb4.tatercart.TaterCart.id;

public final class TcItems {
    public static final PolymerItemGroup ITEM_GROUP = PolymerItemGroup.create(id("general"), Text.literal("TaterCart")).setIcon(() -> new ItemStack(Items.MINECART));

    public static final Item MINECART_CONFIGURATION_TOOL = new MinecartConfigurationToolItem(new Item.Settings().maxCount(1).group(ITEM_GROUP));

    public static final Item MINECART_CONNECTOR = new ConnectorChainItem(new Item.Settings().maxCount(64));

    public static final Item ALWAYS_POWERED_RAIL = new GlowingBlockItem(TcBlocks.ALWAYS_POWERED_RAIL, Items.POWERED_RAIL, new Item.Settings().group(ITEM_GROUP));
    public static final Item COLORED_DETECTOR_RAIL = new GlowingBlockItem(TcBlocks.COLORED_DETECTOR_RAIL, Items.DETECTOR_RAIL, new Item.Settings().group(ITEM_GROUP));
    public static final Item PATTERN_DETECTOR_RAIL = new GlowingBlockItem(TcBlocks.PATTERN_DETECTOR_RAIL, Items.DETECTOR_RAIL, new Item.Settings().group(ITEM_GROUP));
    public static final Item CONFIGURING_RAIL = new GlowingBlockItem(TcBlocks.CONFIGURING_RAIL, Items.ACTIVATOR_RAIL, new Item.Settings().group(ITEM_GROUP));

    public static final CustomMinecartItem SLIME_MINECART = new CustomMinecartItem(CustomMinecartType.SLIME, Items.MINECART);
    public static final CustomMinecartItem POCKET_MINECART = new PocketMinecartItem();
    public static final CustomMinecartItem BARREL_MINECART = new CustomMinecartItem(CustomMinecartType.BARREL, Items.CHEST_MINECART);
    public static final CustomMinecartItem DISPENSER_MINECART = new CustomMinecartItem(CustomMinecartType.DISPENSER, Items.FURNACE_MINECART);
    public static final CustomMinecartItem DROPPER_MINECART = new CustomMinecartItem(CustomMinecartType.DROPPER, Items.FURNACE_MINECART);
    public static final ShulkerMinecartItem SHULKER_MINECART = new ShulkerMinecartItem();
    public static final Map<DyeColor, CustomMinecartItem> COLORED_MINECART = new HashMap<>();

    public static final Item SPEEDOMETER = new SpeedometerItem(new Item.Settings().group(ITEM_GROUP).maxCount(1));

    public static void register() {
        // Legacy id, good idea to change it with 1.19
        register("minecart_enhancer", MINECART_CONFIGURATION_TOOL);

        register("minecart_connecting_chain", MINECART_CONNECTOR);
        register("always_powered_rail", ALWAYS_POWERED_RAIL);
        register("colored_detector_rail", COLORED_DETECTOR_RAIL);
        register("pattern_detector_rail", PATTERN_DETECTOR_RAIL);
        register("configuring_rail", CONFIGURING_RAIL);

        register("slime_minecart", SLIME_MINECART);
        register("pocket_minecart", POCKET_MINECART);
        register("barrel_minecart", BARREL_MINECART);
        register("dispenser_minecart", DISPENSER_MINECART);
        register("dropper_minecart", DROPPER_MINECART);
        register("shulker_minecart", SHULKER_MINECART);

        for (var dyeColor : DyeColor.values()) {
            COLORED_MINECART.put(
                    dyeColor,
                    register(dyeColor.getName() + "_colored_minecart", new CustomMinecartItem(CustomMinecartType.COLORED.get(dyeColor), Items.MINECART))
            );
        }

        register("speedometer", SPEEDOMETER);
    }

    private static <T extends Item> T register(String path, T item) {
        Registry.register(Registry.ITEM, id(path), item);
        return item;
    }

    public static void createRecipes(TcDataGenerator.RecipeBuilder builder) {
        Consumer<CraftingRecipeJsonBuilder> hasMinecart = (b) -> b.criterion("has_minecart", InventoryChangedCriterion.Conditions.items(Items.MINECART));

        builder.createShapeless(MINECART_CONFIGURATION_TOOL, 1, new Object[]{ Items.STICK, Items.RAIL, Items.REDSTONE }, hasMinecart);
        builder.createShapeless(ALWAYS_POWERED_RAIL, 1, new Object[]{ Items.POWERED_RAIL, Items.REDSTONE_TORCH }, hasMinecart);

        builder.createShaped(COLORED_DETECTOR_RAIL, 1, (b) -> {
            b.pattern("e ");
            b.pattern("s ");
            b.pattern("r ");

            b.input('e', Items.SPIDER_EYE);
            b.input('s', Items.REDSTONE);
            b.input('r', Items.RAIL);

            hasMinecart.accept(b);
        });

        builder.createShaped(PATTERN_DETECTOR_RAIL, 1, (b) -> {
            b.pattern(" c ");
            b.pattern("ara");
            b.pattern(" c ");

            b.input('c', Items.COPPER_INGOT);
            b.input('a', Items.AMETHYST_SHARD);
            b.input('r', COLORED_DETECTOR_RAIL);

            hasMinecart.accept(b);
        });

        builder.createShaped(CONFIGURING_RAIL, 1, (b) -> {
            b.pattern(" c ");
            b.pattern("trl");
            b.pattern(" c ");

            b.input('c', Items.COPPER_INGOT);
            b.input('t', Items.REDSTONE_TORCH);
            b.input('l', Items.LEVER);
            b.input('r', Items.RAIL);

            hasMinecart.accept(b);
        });

        builder.createSimpleMinecart(SLIME_MINECART, 1, Items.SLIME_BLOCK, hasMinecart);
        builder.createSimpleMinecart(BARREL_MINECART, 1, Items.BARREL, hasMinecart);
        builder.createSimpleMinecart(DISPENSER_MINECART, 1, Items.DISPENSER, hasMinecart);
        builder.createSimpleMinecart(DROPPER_MINECART, 1, Items.DROPPER, hasMinecart);
        builder.createSimpleMinecart(POCKET_MINECART, 1, Items.LEATHER, hasMinecart);


        for (var entry : COLORED_MINECART.entrySet()) {
            builder.createSimpleMinecart(entry.getValue(), 1, Registry.ITEM.get(new Identifier(entry.getKey().getName() + "_wool")), hasMinecart);
        }
    }
}
