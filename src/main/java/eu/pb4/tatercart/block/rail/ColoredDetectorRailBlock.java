package eu.pb4.tatercart.block.rail;

import com.google.common.collect.MoreCollectors;
import eu.pb4.polymer.block.VirtualBlock;
import eu.pb4.tatercart.block.TcBlockTags;
import eu.pb4.tatercart.entity.minecart.ColoredMinecartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ColoredDetectorRailBlock extends DetectorRailBlock implements VirtualBlock {
    private static Map<Block, DyeColor> MAP;

    public ColoredDetectorRailBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getVirtualBlock() {
        return Blocks.DETECTOR_RAIL;
    }

    @Override
    public BlockState getVirtualBlockState(BlockState state) {
        return getDefaultVirtualBlockState().with(Properties.POWERED, state.get(Properties.POWERED)).with(DetectorRailBlock.SHAPE, state.get(DetectorRailBlock.SHAPE)).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
    }

    public Predicate<Entity> getEntityPredicate(World world, BlockPos pos) {
        if (MAP == null) {
            MAP = new HashMap<>();

            for (var concrete : TcBlockTags.CONCRETE.values()) {
                MAP.put(concrete, Arrays.stream(DyeColor.values()).filter(x -> x.getMapColor().equals(concrete.getDefaultMapColor())).collect(MoreCollectors.onlyElement()));
            }
        }

        var state = world.getBlockState(pos.down());

        if (!MAP.containsKey(state.getBlock())) {
            return entity -> false;
        }

        var color = MAP.get(state.getBlock());

        return entity -> entity instanceof ColoredMinecartEntity coloredMinecart && coloredMinecart.getColor() == color;
    }
}
