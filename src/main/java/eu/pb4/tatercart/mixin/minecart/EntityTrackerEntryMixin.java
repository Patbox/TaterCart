package eu.pb4.tatercart.mixin.minecart;

import eu.pb4.tatercart.entity.minecart.CustomMinecartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow
    @Final
    private Entity entity;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 3))
    private <T> T tatercart_onPositionUpdate(T packet) {
        if (this.entity instanceof CustomMinecartEntity customMinecartEntity) {
            if (packet instanceof EntityPositionS2CPacket || packet instanceof EntityS2CPacket) {
                customMinecartEntity.setInterpolationSteps(3);
            }
        }

        return packet;
    }

}
