package eu.pb4.tatercart.block.rail;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.tatercart.entity.Colorable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ColoredDetectorRailBlock extends DetectorRailBlock implements PolymerBlock, CustomDetectorRail {
    private static Map<DyeColor, Predicate<? super Entity>> ENTITY_COLOR_PREDICATE = new Object2ObjectOpenHashMap<>();
    private static Map<Block, DyeColor> COLORS = new Object2ObjectOpenHashMap<>();

    public ColoredDetectorRailBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    public static DyeColor getColorOf(BlockState blockState) {
        var block = blockState.getBlock();
        if (!COLORS.containsKey(block)) {
            var id = Registry.BLOCK.getId(block).getPath();
            COLORS.put(block, null);

            for (var color : DyeColor.values()) {
                if (id.startsWith(color.getName())) {
                    COLORS.put(block, color);
                    break;
                }
            }
        }

        return COLORS.get(block);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.DETECTOR_RAIL;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.DETECTOR_RAIL.getDefaultState().with(Properties.POWERED, state.get(Properties.POWERED)).with(DetectorRailBlock.SHAPE, state.get(DetectorRailBlock.SHAPE)).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
    }

    public static Predicate<? super Entity> isColor(DyeColor color) {
        return ENTITY_COLOR_PREDICATE.computeIfAbsent(color, ColoredDetectorRailBlock::createPredicate);
    }

    private static Predicate<? super Entity> createPredicate(DyeColor dyeColor) {
        return (e) -> e instanceof Colorable entity && entity.getColor() == dyeColor;
    }

    @Override
    public List<?> getCartsCustom(World world, BlockPos pos, Class<?> entityClass, Predicate<Entity> entityPredicate) {
        if (entityClass == AbstractMinecartEntity.class) {
            var color = ColoredDetectorRailBlock.getColorOf(world.getBlockState(pos.down()));

            return world.getEntitiesByClass(AbstractMinecartEntity.class, CustomDetectorRail.getCartDetectionBox(pos), entityPredicate.and(ColoredDetectorRailBlock.isColor(color)));
        } else {
            return Collections.emptyList();
        }
    }
}
