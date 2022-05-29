package eu.pb4.tatercart.mixin.minecart.movement;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeBlock.class)
public class SlimeBlockMixin {
    @Inject(method = "bounce", at = @At("HEAD"), cancellable = true)
    private void minecartBounce(Entity entity, CallbackInfo ci) {
        var vec3d = entity.getVelocity();
        if (vec3d.y < 0.0D && entity instanceof ExtendedMinecart minecart && minecart.tatercart_hasCustomPhysics()) {
            entity.setVelocity(vec3d.x, -vec3d.y, vec3d.z);
            ci.cancel();
        }
    }
}
