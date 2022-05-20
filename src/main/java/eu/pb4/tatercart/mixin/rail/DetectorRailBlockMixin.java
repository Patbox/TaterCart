package eu.pb4.tatercart.mixin.rail;

import eu.pb4.tatercart.block.rail.CustomDetectorRail;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;


@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {

    @Shadow protected abstract Box getCartDetectionBox(BlockPos pos);

    @Inject(method = "getCarts", at = @At(value = "HEAD"), cancellable = true)
    public void tatercart_replaceCheck(World world, BlockPos pos, Class<?> entityClass, Predicate<Entity> entityPredicate, CallbackInfoReturnable<List<?>> cir) {
        if (this instanceof CustomDetectorRail customDetectorRail) {
            cir.setReturnValue(customDetectorRail.getCartsCustom(world, pos, entityClass, entityPredicate));
        }
    }
}
