package eu.pb4.tatercart.block.rail;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.tatercart.entity.minecart.other.ColoredMinecartEntity;
import eu.pb4.tatercart.mixin.BannerBlockEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class PatternDetectorRailBlock extends DetectorRailBlock implements PolymerBlock, CustomDetectorRail {
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] { Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH };
    public PatternDetectorRailBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.DETECTOR_RAIL;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.DETECTOR_RAIL.getDefaultState().with(Properties.POWERED, state.get(Properties.POWERED)).with(DetectorRailBlock.SHAPE, state.get(DetectorRailBlock.SHAPE)).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
    }

    private static Predicate<? super Entity> createPredicate(BlockPos pos, World world) {
        return (e) -> e instanceof ColoredMinecartEntity entity && isCorrectBanner(entity.getBannerItemStack(), pos, world);
    }

    private static boolean isCorrectBanner(ItemStack stack, BlockPos pos, World world) {
        var mutable = new BlockPos.Mutable();
        for (var dir : HORIZONTAL_DIRECTIONS) {
            var optional = world.getBlockEntity(mutable.set(pos).add(dir.getOffsetX(), 0, dir.getOffsetZ()), BlockEntityType.BANNER);

            if (optional.isPresent()) {
                var be = optional.get();

                if (
                        stack.getItem() instanceof BannerItem bannerItem && bannerItem.getColor() == be.getColorForState()
                ) {
                    var patterns = ((BannerBlockEntityAccessor) be).getPatternListNbt();
                    var stackPatterns = stack.hasNbt() && stack.getNbt().contains("BlockEntityTag", NbtElement.COMPOUND_TYPE)
                            ? stack.getNbt().getCompound("BlockEntityTag").getList("Patterns", NbtElement.COMPOUND_TYPE) : null;

                    if (((patterns == null || patterns.isEmpty()) && (stackPatterns == null || stackPatterns.isEmpty())) || (patterns != null && patterns.equals(stackPatterns))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public List<?> getCartsCustom(World world, BlockPos pos, Class<?> entityClass, Predicate<Entity> entityPredicate) {
        if (entityClass == AbstractMinecartEntity.class) {
            return world.getEntitiesByClass(ColoredMinecartEntity.class, CustomDetectorRail.getCartDetectionBox(pos), entityPredicate.and(createPredicate(pos, world)));
        } else {
            return Collections.emptyList();
        }
    }
}
