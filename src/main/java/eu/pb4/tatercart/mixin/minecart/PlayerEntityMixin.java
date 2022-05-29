package eu.pb4.tatercart.mixin.minecart;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.item.MinecartConfigurationToolItem;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.other.TcGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract boolean isCreative();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
    private void tatercart_enchanceMinecart(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        var stack = this.getStackInHand(hand);
        if (this.world instanceof ServerWorld serverWorld && !stack.isEmpty() && entity instanceof ExtendedMinecart minecart) {
            if (stack.isOf(TcItems.MINECART_CONFIGURATION_TOOL)) {
                ((MinecartConfigurationToolItem) TcItems.MINECART_CONFIGURATION_TOOL).openGui((ServerPlayerEntity) (Object) this, minecart);

                //var isEnhanced = minecart.tatercart_hasCustomPhysics();
                //minecart.tatercart_setPhysics(!isEnhanced);
                //serverWorld.spawnParticles(ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0, 0, 0);
                //serverWorld.spawnParticles(new DustParticleEffect(isEnhanced ? new Vec3f(0.9f, 0, 0) : new Vec3f(0, 0.9f, 0), 1) , entity.getX(), entity.getY(), entity.getZ(), 20, 0.3, 0.3, 0.3, 0);

                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (this.world.getGameRules().getBoolean(TcGameRules.MINECART_LINKING) && stack.isOf(Items.CHAIN)) {
                if (entity instanceof ExtendedMinecart ext && ext.tatercart_canLink()) {
                    var newStack = new ItemStack(TcItems.MINECART_CONNECTOR, stack.getCount());
                    newStack.getOrCreateNbt().putInt("TargetMinecart", entity.getId());
                    this.setStackInHand(hand, newStack);
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            } else if (this.world.getGameRules().getBoolean(TcGameRules.MINECART_LINKING) && stack.isOf(TcItems.MINECART_CONNECTOR)) {
                if (stack.getOrCreateNbt().contains("TargetMinecart")) {
                    var target = entity.world.getEntityById(stack.getNbt().getInt("TargetMinecart"));
                    if (target instanceof ExtendedMinecart ext && ext.tatercart_canLink() && entity instanceof ExtendedMinecart ent && ent.tatercart_canLink()) {
                        var newStack = new ItemStack(Items.CHAIN, this.isCreative() ? stack.getCount() : stack.getCount() - 1);
                        this.setStackInHand(hand, newStack);

                        ext.tatercart_setLinked((AbstractMinecartEntity) entity);
                        ent.tatercart_setLinked((AbstractMinecartEntity) target);

                        cir.setReturnValue(ActionResult.SUCCESS);
                    }
                }
            }
        }
    }
}
