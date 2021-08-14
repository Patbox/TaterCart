package eu.pb4.tatercart.block;

import eu.pb4.tatercart.TcDataPack;
import eu.pb4.tatercart.block.rail.AlwaysPoweredRailBlock;
import eu.pb4.tatercart.block.rail.ColoredDetectorRailBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static eu.pb4.tatercart.TaterCartMod.id;

public final class TcBlocks {
    private static final AbstractBlock.Settings RAIL_SETTINGS = AbstractBlock.Settings.copy(Blocks.RAIL);

    public static final ColoredDetectorRailBlock COLORED_DETECTOR_RAIL = new ColoredDetectorRailBlock(RAIL_SETTINGS);
    public static final AlwaysPoweredRailBlock ALWAYS_POWERED_RAIL = new AlwaysPoweredRailBlock(RAIL_SETTINGS);

    public static final Block[] RAILS = new Block[]{
            COLORED_DETECTOR_RAIL,
            ALWAYS_POWERED_RAIL
    };

    public static void register() {
        register("colored_detector_rail", COLORED_DETECTOR_RAIL);
        register("always_powered_rail", ALWAYS_POWERED_RAIL);
    }

    private static Block register(String path, Block block) {
        Registry.register(Registry.BLOCK, id(path), block);
        return block;
    }

    public static void registerData() {
        TcDataPack.createSimpleDrop(COLORED_DETECTOR_RAIL);
        TcDataPack.createSimpleDrop(ALWAYS_POWERED_RAIL);

        TcDataPack.createTag(new Identifier("minecraft", "blocks/rails"), RAILS);
    }
}
