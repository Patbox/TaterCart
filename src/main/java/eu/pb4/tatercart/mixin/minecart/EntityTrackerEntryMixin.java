package eu.pb4.tatercart.mixin.minecart;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.entity.minecart.CustomMinecartEntity;
import eu.pb4.tatercart.mixin.accessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow
    @Final
    private Entity entity;
    /*@Unique
    private Vec3d tatercart_tempPos;
*/
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 3))
    private <T> T tatercart_onPositionUpdate(T packet) {
        if (this.entity instanceof CustomMinecartEntity customMinecartEntity) {
            if (packet instanceof EntityPositionS2CPacket || packet instanceof EntityS2CPacket) {
                customMinecartEntity.setInterpolationSteps(3);
            }
        }

        return packet;
    }


    /*@Inject(method = "tick", at = @At("HEAD"))
    private void tatercart_storePos(CallbackInfo ci) {
        if (this.entity instanceof ExtendedMinecart minecart && minecart.tatercart_isEnchanced()) {
            this.tatercart_tempPos = entity.getPos();
            ((EntityAccessor) this.entity).setPos(this.tatercart_tempPos.add(this.entity.getVelocity()));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tatercart_restorePos(CallbackInfo ci) {
        if (this.tatercart_tempPos != null) {
            ((EntityAccessor) this.entity).setPos(this.tatercart_tempPos);
        }
    }*/
}
