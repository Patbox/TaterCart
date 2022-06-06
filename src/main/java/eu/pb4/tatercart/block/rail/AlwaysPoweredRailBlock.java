package eu.pb4.tatercart.block.rail;

import eu.pb4.polymer.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AlwaysPoweredRailBlock extends PoweredRailBlock implements PolymerBlock {
    public AlwaysPoweredRailBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.POWERED_RAIL;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.POWERED_RAIL.getDefaultState().with(PoweredRailBlock.POWERED, state.get(PoweredRailBlock.POWERED)).with(PoweredRailBlock.SHAPE, state.get(PoweredRailBlock.SHAPE)).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
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

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        world.spawnParticles(DustParticleEffect.DEFAULT, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1, 0.01, 0.01, 0.01, 0.1);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }
}
