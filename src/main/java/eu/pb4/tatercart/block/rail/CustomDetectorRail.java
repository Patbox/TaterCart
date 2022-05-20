package eu.pb4.tatercart.block.rail;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public interface CustomDetectorRail {
    List<?> getCartsCustom(World world, BlockPos pos, Class<?> entityClass, Predicate<Entity> entityPredicate);

    static Box getCartDetectionBox(BlockPos pos) {
        return new Box((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }
}
