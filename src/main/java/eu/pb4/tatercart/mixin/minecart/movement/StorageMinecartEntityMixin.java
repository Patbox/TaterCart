package eu.pb4.tatercart.mixin.minecart.movement;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartEntityMixin extends AbstractMinecartEntity implements ExtendedMinecart {
    public StorageMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "applySlowdown", at = @At("HEAD"), cancellable = true)
    private void tatercart_changeMaxSpeedToNormal(CallbackInfo ci) {
        if (this.tatercart_hasCustomPhysics() && (this.tatercart_getLinked()[0] != null || this.tatercart_getLinked()[1] != null)) {
            super.applySlowdown();
            ci.cancel();
        }
    }
}
