package eu.pb4.tatercart.block.rail;

import eu.pb4.polymer.block.VirtualBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AlwaysPoweredRailBlock extends PoweredRailBlock implements VirtualBlock {
    public AlwaysPoweredRailBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getVirtualBlock() {
        return Blocks.POWERED_RAIL;
    }

    @Override
    public BlockState getVirtualBlockState(BlockState state) {
        return getDefaultVirtualBlockState().with(PoweredRailBlock.POWERED, state.get(PoweredRailBlock.POWERED)).with(PoweredRailBlock.SHAPE, state.get(PoweredRailBlock.SHAPE)).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
    }

    @Override
    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
        boolean current = state.get(POWERED);
        if (!current) {
            world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_ALL);
            world.updateNeighborsAlways(pos.down(), this);
            if (state.get(SHAPE).isAscending()) {
                world.updateNeighborsAlways(pos.up(), this);
            }
        }
    }
}
