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
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow
    @Final
    private Entity entity;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 3))
    public <T> void onPositionUpdate(Consumer<T> consumer, T t) {
        if (entity instanceof CustomMinecartEntity customMinecartEntity) {
            if (t instanceof EntityPositionS2CPacket || t instanceof EntityS2CPacket) {
                customMinecartEntity.setInterpolationSteps(3);
            }
        }

        consumer.accept(t);
    }
}
