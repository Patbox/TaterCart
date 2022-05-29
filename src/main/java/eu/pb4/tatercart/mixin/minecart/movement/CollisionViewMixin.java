package eu.pb4.tatercart.mixin.minecart.movement;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.other.MinecartBlockCollisionSpliterator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CollisionView.class)
public interface CollisionViewMixin {
    @Inject(method = "getBlockCollisions", at = @At("HEAD"), cancellable = true)
    default void tatercart_customCollisionsForMinecarts(Entity entity, Box box, CallbackInfoReturnable<Iterable<VoxelShape>> cir) {
        if (entity instanceof ExtendedMinecart minecart && minecart.tatercart_hasCustomPhysics()) {
            cir.setReturnValue(MinecartBlockCollisionSpliterator.create((CollisionView) this, entity, box));
        }
    }
}
