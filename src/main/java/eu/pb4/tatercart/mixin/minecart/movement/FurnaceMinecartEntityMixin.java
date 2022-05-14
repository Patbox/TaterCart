package eu.pb4.tatercart.mixin.minecart.movement;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends AbstractMinecartEntity implements ExtendedMinecart {
    public FurnaceMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
    private void tatercart_changeMaxSpeedToNormal(CallbackInfoReturnable<Double> cir) {
        if (this.tatercart_customPhysics()) {
            cir.setReturnValue(super.getMaxOffRailSpeed());
        }
    }
}
