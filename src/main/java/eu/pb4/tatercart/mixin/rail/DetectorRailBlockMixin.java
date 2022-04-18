package eu.pb4.tatercart.mixin.rail;

import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.block.rail.ColoredDetectorRailBlock;
import eu.pb4.tatercart.entity.minecart.ColoredMinecartEntity;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;


@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {

    @Shadow protected abstract Box getCartDetectionBox(BlockPos pos);

    @Inject(method = "getCarts", at = @At(value = "HEAD"), cancellable = true)
    public void tatercart_replaceCheck(World world, BlockPos pos, Class<?> entityClass, Predicate<Entity> entityPredicate, CallbackInfoReturnable<List<?>> cir) {
        if ((Object) this == TcBlocks.COLORED_DETECTOR_RAIL) {
            if (entityClass == AbstractMinecartEntity.class) {
                var color = ColoredDetectorRailBlock.getColorOf(world.getBlockState(pos.down()));

                cir.setReturnValue(world.getEntitiesByClass(ColoredMinecartEntity.class, this.getCartDetectionBox(pos), entityPredicate.and(ColoredDetectorRailBlock.isColor(color))));
            } else {
                cir.setReturnValue(Collections.emptyList());
            }
        }
    }
}
