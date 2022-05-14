package eu.pb4.tatercart.item;

import eu.pb4.polymer.api.item.PolymerItemGroup;
import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.other.TcDataGenerator;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.BannerBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static eu.pb4.tatercart.TaterCart.id;

public final class TcItems {
    public static final PolymerItemGroup ITEM_GROUP = PolymerItemGroup.create(id("general"), new LiteralText("TaterCart")).setIcon(() -> new ItemStack(Items.MINECART));

    public static final Item MINECART_ENHANCER = new GlowingItem(new Item.Settings().maxCount(1).group(ITEM_GROUP), Items.REDSTONE)  {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(new TranslatableText("item.tatercart.minecart_enhancer.desc.1").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("item.tatercart.minecart_enhancer.desc.2").formatted(Formatting.GRAY));
        }
    };

    public static final Item MINECART_CONNECTOR = new ConnectorChainItem(new Item.Settings().maxCount(64));

    public static final Item ALWAYS_POWERED_RAIL = new GlowingBlockItem(TcBlocks.ALWAYS_POWERED_RAIL, Items.POWERED_RAIL, new Item.Settings().group(ITEM_GROUP));
    public static final Item COLORED_DETECTOR_RAIL = new GlowingBlockItem(TcBlocks.COLORED_DETECTOR_RAIL, Items.DETECTOR_RAIL, new Item.Settings().group(ITEM_GROUP));

    public static final CustomMinecartItem SLIME_MINECART = new CustomMinecartItem(CustomMinecartType.SLIME, Items.MINECART);
    public static final CustomMinecartItem BARREL_MINECART = new CustomMinecartItem(CustomMinecartType.BARREL, Items.CHEST_MINECART);
    public static final CustomMinecartItem DISPENSER_MINECART = new CustomMinecartItem(CustomMinecartType.DISPENSER, Items.FURNACE_MINECART);
    public static final CustomMinecartItem DROPPER_MINECART = new CustomMinecartItem(CustomMinecartType.DROPPER, Items.FURNACE_MINECART);
    public static final ShulkerMinecartItem SHULKER_MINECART = new ShulkerMinecartItem();
    public static final Map<DyeColor, CustomMinecartItem> COLORED_MINECART = new HashMap<>();

    public static void register() {
        register("minecart_enhancer", MINECART_ENHANCER);
        register("minecart_connecting_chain", MINECART_CONNECTOR);
        register("always_powered_rail", ALWAYS_POWERED_RAIL);
        register("colored_detector_rail", COLORED_DETECTOR_RAIL);

        register("slime_minecart", SLIME_MINECART);
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
    }

    private static <T extends Item> T register(String path, T item) {
        Registry.register(Registry.ITEM, id(path), item);
        return item;
    }

    public static void createRecipes(TcDataGenerator.RecipeBuilder builder) {
        Consumer<ShapelessRecipeJsonBuilder> hasMinecart = (b) -> b.criterion("has_minecart", InventoryChangedCriterion.Conditions.items(Items.MINECART));

        builder.createShapeless(MINECART_ENHANCER, 1, new Object[]{ Items.REDSTONE, Items.RAIL }, hasMinecart);
        builder.createShapeless(ALWAYS_POWERED_RAIL, 1, new Object[]{ Items.RAIL, Items.REDSTONE_BLOCK }, hasMinecart);
        builder.createShapeless(COLORED_DETECTOR_RAIL, 1, new Object[]{ Items.RAIL, Items.REDSTONE, Items.SPIDER_EYE }, hasMinecart);

        builder.createShapeless(SLIME_MINECART, 1, new Object[]{ Items.MINECART, Items.SLIME_BLOCK }, hasMinecart);
        builder.createShapeless(BARREL_MINECART, 1, new Object[]{ Items.MINECART, Items.BARREL }, hasMinecart);
        builder.createShapeless(DISPENSER_MINECART, 1, new Object[]{ Items.MINECART, Items.DISPENSER }, hasMinecart);
        builder.createShapeless(DROPPER_MINECART, 1, new Object[]{ Items.MINECART, Items.DROPPER }, hasMinecart);

        for (var entry : COLORED_MINECART.entrySet()) {
            builder.createShapeless(entry.getValue(), 1, new Object[]{ Items.MINECART, BannerBlock.getForColor(entry.getKey()).asItem() }, hasMinecart);
        }
    }
}
