package eu.pb4.tatercart.block;

import eu.pb4.tatercart.block.rail.AlwaysPoweredRailBlock;
import eu.pb4.tatercart.block.rail.ColoredDetectorRailBlock;
import eu.pb4.tatercart.other.TcDataGenerator;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

import static eu.pb4.tatercart.TaterCart.id;

public final class TcBlocks {
    private static final AbstractBlock.Settings RAIL_SETTINGS = AbstractBlock.Settings.copy(Blocks.RAIL);

    public static final ColoredDetectorRailBlock COLORED_DETECTOR_RAIL = new ColoredDetectorRailBlock(RAIL_SETTINGS);
    public static final AlwaysPoweredRailBlock ALWAYS_POWERED_RAIL = new AlwaysPoweredRailBlock(RAIL_SETTINGS);

    public static void register() {
        register("colored_detector_rail", COLORED_DETECTOR_RAIL);
        register("always_powered_rail", ALWAYS_POWERED_RAIL);
    }

    private static Block register(String path, Block block) {
        Registry.register(Registry.BLOCK, id(path), block);
        return block;
    }

    public static void createDrops(TcDataGenerator.BlockLootTableProvider provider) {
        provider.addDrop(COLORED_DETECTOR_RAIL);
        provider.addDrop(ALWAYS_POWERED_RAIL);
    }

    public static void createTags(BiConsumer<TagKey<Block>, Block[]> tagBuilder) {
        tagBuilder.accept(BlockTags.RAILS, new Block[]{ COLORED_DETECTOR_RAIL, ALWAYS_POWERED_RAIL });

    }
}
