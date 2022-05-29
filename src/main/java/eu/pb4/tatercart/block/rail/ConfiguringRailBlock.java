package eu.pb4.tatercart.block.rail;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.tatercart.blockentity.ConfiguringRailBlockEntity;
import eu.pb4.tatercart.entity.ExtendedMinecart;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings({"deprecation"})
public class ConfiguringRailBlock extends AbstractRailBlock implements PolymerBlock, BlockEntityProvider {
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;

    public ConfiguringRailBlock(Settings settings) {
        super(false, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(WATERLOGGED, false));
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient) {
            if (entity instanceof ExtendedMinecart minecart && world.getBlockEntity(pos) instanceof ConfiguringRailBlockEntity blockEntity) {
                blockEntity.applyChanges(minecart);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.MAIN_HAND
                && player instanceof ServerPlayerEntity serverPlayer
                && world.canPlayerModifyAt(player, pos)
                && world.getBlockEntity(pos) instanceof ConfiguringRailBlockEntity blockEntity
        ) {
            blockEntity.openGui(serverPlayer);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return Blocks.ACTIVATOR_RAIL.rotate(state, rotation);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED);
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return Properties.STRAIGHT_RAIL_SHAPE;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.ACTIVATOR_RAIL;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.ACTIVATOR_RAIL.getDefaultState().with(Properties.POWERED, true).with(DetectorRailBlock.SHAPE, state.get(DetectorRailBlock.SHAPE)).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConfiguringRailBlockEntity(pos, state);
    }
}
