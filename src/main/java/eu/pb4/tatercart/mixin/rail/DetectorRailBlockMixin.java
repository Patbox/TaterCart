package eu.pb4.tatercart.mixin.rail;

import eu.pb4.tatercart.block.rail.ColoredDetectorRailBlock;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {
    @Shadow
    protected abstract <T extends AbstractMinecartEntity> List<T> getCarts(World world, BlockPos pos, Class<T> entityClass, Predicate<Entity> entityPredicate);

    @Redirect(method = "updatePoweredStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DetectorRailBlock;getCarts(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/lang/Class;Ljava/util/function/Predicate;)Ljava/util/List;"))
    public <T extends AbstractMinecartEntity> List<T> redirectEntityPredicate(DetectorRailBlock detectorRailBlock, World world, BlockPos pos, Class<T> entityClass, Predicate<Entity> entityPredicate) {
        if (((DetectorRailBlock) (Object) this) instanceof ColoredDetectorRailBlock coloredDetectorRailBlock) {
            entityPredicate = coloredDetectorRailBlock.getEntityPredicate(world, pos);
        }

        return getCarts(world, pos, entityClass, entityPredicate);
    }
}
