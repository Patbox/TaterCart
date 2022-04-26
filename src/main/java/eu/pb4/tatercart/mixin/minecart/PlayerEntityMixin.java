package eu.pb4.tatercart.mixin.minecart;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.item.TcItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
    private void tatercart_enchanceMinecart(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (this.world instanceof ServerWorld serverWorld && entity instanceof ExtendedMinecart minecart) {
            var stack = this.getStackInHand(hand);
            if (!stack.isEmpty() && stack.isOf(TcItems.MINECART_ENHANCER)) {
                var isEnhanced = minecart.tatercart_customPhysics();
                minecart.tatercart_setPhysics(!isEnhanced);
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0, 0, 0);
                serverWorld.spawnParticles(new DustParticleEffect(isEnhanced ? new Vec3f(0.9f, 0, 0) : new Vec3f(0, 0.9f, 0), 1) , entity.getX(), entity.getY(), entity.getZ(), 20, 0.3, 0.3, 0.3, 0);

                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
