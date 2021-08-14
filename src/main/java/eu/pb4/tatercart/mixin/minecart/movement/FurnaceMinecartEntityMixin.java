package eu.pb4.tatercart.mixin.minecart.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends AbstractMinecartEntity {
    public FurnaceMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
    private void changeMaxSpeedToNormal(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(super.getMaxOffRailSpeed());
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        /*if (!this.world.isClient) {
            if (entity instanceof AbstractMinecartEntity minecart) {
                if (!entity.noClip && !this.noClip) {
                    if (!this.hasPassenger(entity)) {
                        entity.addVelocity(this.getVelocity().x * 0.5, this.getVelocity().y * 0.5, this.getVelocity().z * 0.5);
                        this.addVelocity(-this.getVelocity().x * 0.5, -this.getVelocity().y * 0.5, -this.getVelocity().z * 0.5);
                    }
                }
            }
        } else {
        }*/
        super.pushAwayFrom(entity);
    }
}
