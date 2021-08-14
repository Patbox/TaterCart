package eu.pb4.tatercart.item;

import eu.pb4.tatercart.TcDataPack;
import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import net.minecraft.block.BannerBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

import static eu.pb4.tatercart.TaterCartMod.id;

public final class TcItems {
    public static Item ALWAYS_POWERED_RAIL = new GlowingBlockItem(TcBlocks.ALWAYS_POWERED_RAIL, Items.POWERED_RAIL, new Item.Settings());
    public static Item COLORED_DETECTOR_RAIL = new GlowingBlockItem(TcBlocks.COLORED_DETECTOR_RAIL, Items.DETECTOR_RAIL, new Item.Settings());

    public static CustomMinecartItem SLIME_MINECART = new CustomMinecartItem(CustomMinecartType.SLIME);
    public static CustomMinecartItem BARREL_MINECART = new CustomMinecartItem(CustomMinecartType.BARREL);
    public static Map<DyeColor, CustomMinecartItem> COLORED_MINECART = new HashMap<>();

    public static void register() {
        register("always_powered_rail", ALWAYS_POWERED_RAIL);
        register("colored_detector_rail", COLORED_DETECTOR_RAIL);

        register("slime_minecart", SLIME_MINECART);
        register("barrel_minecart", BARREL_MINECART);

        for (var dyeColor : DyeColor.values()) {
            COLORED_MINECART.put(
                    dyeColor,
                    register(dyeColor.getName() + "_colored_minecart", new CustomMinecartItem(CustomMinecartType.COLORED.get(dyeColor)))
            );
        }
    }

    private static <T extends Item> T register(String path, T item) {
        Registry.register(Registry.ITEM, id(path), item);
        return item;
    }

    public static void registerData() {
        TcDataPack.createCraftingShapeless(ALWAYS_POWERED_RAIL, 1, Items.RAIL, Items.REDSTONE_BLOCK);
        TcDataPack.createCraftingShapeless(COLORED_DETECTOR_RAIL, 1, Items.RAIL, Items.REDSTONE, Items.SPIDER_EYE);

        TcDataPack.createCraftingShapeless(SLIME_MINECART, 1, Items.MINECART, Items.SLIME_BLOCK);
        TcDataPack.createCraftingShapeless(BARREL_MINECART, 1, Items.MINECART, Items.BARREL);

        for (var entry : COLORED_MINECART.entrySet()) {
            TcDataPack.createCraftingShapeless(entry.getValue(), 1, Items.MINECART, BannerBlock.getForColor(entry.getKey()).asItem());
        }
    }
}
