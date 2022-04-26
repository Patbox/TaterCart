package eu.pb4.tatercart.mixin.minecart.movement;

import eu.pb4.tatercart.TaterCartMod;
import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.mixin.accessor.EntityAccessor;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements ExtendedMinecart {
    @Unique
    private AbstractMinecartEntity nextLinkedMinecart = null;
    @Unique
    private AbstractMinecartEntity previousLinkedMinecart = null;
    @Unique
    private BlockState tatercart_lastRailBlockState = null;
    @Unique
    private BlockPos tatercart_lastRailBlockPos = null;
    @Unique
    private double tatercart_currentOffset = 0;

    @Unique
    private boolean tatercart_isEnchanced = false;

    public AbstractMinecartEntityMixin(net.minecraft.entity.EntityType<?> type, net.minecraft.world.World world) {
        super(type, world);
    }

    @Shadow
    protected abstract double getMaxOffRailSpeed();

    @Shadow
    public abstract Direction getMovementDirection();

    @Shadow private boolean yawFlipped;

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    private void tatercraft_setDefaultEnhanced(EntityType entityType, World world, CallbackInfo ci) {
        this.tatercart_isEnchanced = world.getGameRules().getBoolean(TaterCartMod.DEFAULT_ENHANCED);
    }

    @Inject(method = "getMaxOffRailSpeed", at = @At("HEAD"), cancellable = true)
    private void tatercart_changeMaxOffRailSpeed(CallbackInfoReturnable<Double> cir) {
        if (this.tatercart_isEnchanced) {
            var isOnCurved = false;
            if (this.tatercart_lastRailBlockState != null && this.tatercart_lastRailBlockState.getBlock() instanceof AbstractRailBlock block) {
                var shape = this.tatercart_lastRailBlockState.get(block.getShapeProperty());

                if (shape == RailShape.NORTH_EAST || shape == RailShape.NORTH_WEST || shape == RailShape.SOUTH_EAST || shape == RailShape.SOUTH_WEST) {
                    isOnCurved = true;
                }
            }

            cir.setReturnValue((this.asEntity().isTouchingWater() ? 4.0 : 8.0) / (isOnCurved ? 24 : 16.0));
        }
    }

    @Inject(method = "moveOnRail", at = @At("HEAD"))
    private void tatercart_setWasOnRail(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (this.tatercart_isEnchanced && (this.tatercart_lastRailBlockState != state || !pos.equals(this.tatercart_lastRailBlockPos))) {
            this.tatercart_lastRailBlockState = state;
            this.tatercart_lastRailBlockPos = pos;
            try {
                var world = asEntity().world;
                var underPos = pos.down();
                var blockState = world.getBlockState(underPos);

                var nextUnderPos = underPos.offset(this.asEntity().getMovementDirection());
                var blockState2 = world.getBlockState(nextUnderPos);

                var collisionShape1 = blockState.getCollisionShape(world, underPos);
                var collisionShape2 = blockState2.getCollisionShape(world, nextUnderPos);

                var maxY = Math.max(collisionShape1.isEmpty() ? 0 : collisionShape1.getBoundingBox().maxY, collisionShape2.isEmpty() ? 0 : collisionShape2.getBoundingBox().maxY);

                if (maxY > 1) {
                    this.tatercart_currentOffset = maxY - 1;
                } else {
                    this.tatercart_currentOffset = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Redirect(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean tatercart_checkForCustomPoweredRails(BlockState blockState, Block block) {
        return blockState.getBlock() instanceof PoweredRailBlock;
    }

    @Redirect(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private void tatercart_bounceBeforeMoving(AbstractMinecartEntity abstractMinecartEntity, MovementType movementType, Vec3d movement) {
        if (this.tatercart_isEnchanced) {
            var ad = ((EntityAccessor) this.asEntity()).callAdjustMovementForCollisions(movement);
            var pos = this.asEntity().getPos();
            var deltaX = this.asEntity().getPos().x + ad.x;
            var deltaY = this.asEntity().getPos().y + ad.y;
            var deltaZ = this.asEntity().getPos().z + ad.z;

            if (ad.x != movement.x) {
                var state = this.asEntity().world.getBlockState(new BlockPos(deltaX, deltaY, pos.z).offset(this.asEntity().getMovementDirection()));
                if (state.getBlock() instanceof SlimeBlock) {
                    this.asEntity().setVelocity(this.asEntity().getVelocity().multiply(-0.95, 1, 1));
                    movement = movement.multiply(-0.95, 1, 1);
                }
            }

            if (ad.z != movement.z) {
                var state = this.asEntity().world.getBlockState(new BlockPos(pos.x, deltaY, deltaZ).offset(this.asEntity().getMovementDirection()));
                if (state.getBlock() instanceof SlimeBlock) {
                    this.asEntity().setVelocity(this.asEntity().getVelocity().multiply(1, 1, -0.95));
                    movement = movement.multiply(1, 1, -0.95);
                }
            }
        }
        this.asEntity().move(movementType, movement);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setPitch(F)V", ordinal = 1))
    private void tatercart_changePitch(AbstractMinecartEntity minecartEntity, float pitch) {
        if (this.tatercart_isEnchanced) {
            var i = this.getMovementDirection().getDirection().offset();

            var value = (float) (i * this.getVelocity().y * 90 * -this.getHorizontalFacing().rotateYClockwise().getDirection().offset());
            this.setPitch(MathHelper.clamp(value, -80.0F, 80.0F) % 360.0F);
        }
    }

    @Inject(method = "moveOffRail", at = @At("HEAD"), cancellable = true)
    public void tatercart_customMoveOffRail(CallbackInfo ci) {
        if (this.tatercart_isEnchanced) {
            var max = this.getMaxOffRailSpeed() * 2.5;
            var movement = this.asEntity().getVelocity();
            this.tatercart_currentOffset = 0;

            if (this.tatercart_lastRailBlockState == null) {
                this.asEntity().setVelocity(MathHelper.clamp(movement.x, -max, max), movement.y, MathHelper.clamp(movement.z, -max, max));
            } else {
                var railShape = this.tatercart_lastRailBlockState.get(((AbstractRailBlock) this.tatercart_lastRailBlockState.getBlock()).getShapeProperty());

                var multiplier = 0.316;
                var power = movement.horizontalLength() * multiplier;

                var yVel = switch (railShape) {
                    case ASCENDING_EAST -> movement.x > 0 ? power : -power;
                    case ASCENDING_WEST -> movement.x < 0 ? power : -power;
                    case ASCENDING_NORTH -> movement.z < 0 ? power : -power;
                    case ASCENDING_SOUTH -> movement.z > 0 ? power : -power;
                    default -> 0;
                };

                this.asEntity().setVelocity(MathHelper.clamp(movement.x * multiplier, -max, max), movement.y + yVel, MathHelper.clamp(movement.z * multiplier, -max, max));
                this.tatercart_lastRailBlockState = null;
            }

            if (this.asEntity().isOnGround()) {
                this.asEntity().setVelocity(this.asEntity().getVelocity().multiply(0.85d));
            } else {
                this.asEntity().setVelocity(this.asEntity().getVelocity().multiply(0.995d));
            }

            var ad = ((EntityAccessor) this.asEntity()).callAdjustMovementForCollisions(movement);
            var pos = this.asEntity().getPos();
            var deltaX = this.asEntity().getPos().x + ad.x;
            var deltaY = this.asEntity().getPos().y + ad.y;
            var deltaZ = this.asEntity().getPos().z + ad.z;


            if (ad.x != movement.x) {
                var state = this.asEntity().world.getBlockState(new BlockPos(deltaX, deltaY, pos.z).offset(this.getMovementDirection()));
                if (state.getBlock() instanceof SlimeBlock) {
                    this.asEntity().setVelocity(this.asEntity().getVelocity().multiply(-0.9, 1, 1));
                }
            }

            if (ad.z != movement.z) {
                var state = this.asEntity().world.getBlockState(new BlockPos(pos.x, deltaY, deltaZ).offset(this.getMovementDirection()));
                if (state.getBlock() instanceof SlimeBlock) {
                    this.asEntity().setVelocity(this.asEntity().getVelocity().multiply(1, 1, -0.9));
                }
            }

            this.asEntity().move(MovementType.SELF, this.asEntity().getVelocity());
            ci.cancel();
        }
    }

    @Override
    protected Box calculateBoundingBox() {
        var box = super.calculateBoundingBox();
        return box.withMinY(this.tatercart_currentOffset + box.minY);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void tatercart_saveData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("tatercart:enchanced", this.tatercart_isEnchanced);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void tatercart_readData(NbtCompound nbt, CallbackInfo ci) {
        this.tatercart_isEnchanced = nbt.getBoolean("tatercart:enchanced");
    }

    /*@Inject(method = "tick", at = @At("HEAD"))
    private void handleLinkedCarts(CallbackInfo ci) {
        var nextLinked = this.getNextLinked();
        var previousLinked = this.getPreviousLinked();

        AbstractMinecartEntity cart = nextLinked;

        if (previousLinked != null && !previousLinked.isAlive()) {
            this.setPreviousLinked(null);
            previousLinked = null;
        }

        if (nextLinked != null && previousLinked != null
                && nextLinked.getVelocity().lengthSquared() < previousLinked.getVelocity().lengthSquared()) {
            this.setNextLinked(previousLinked);
            this.setPreviousLinked(nextLinked);
            cart = previousLinked;
        }

        if (cart != null) {
            var relVec = this.getPos().relativize(cart.getPos());

            if (!cart.isAlive() || relVec.lengthSquared() > 16) {
                this.setNextLinked(null);
            } else {
                CartUtil.setVelocityForLink(this.asEntity(), cart);
            }
        }
    }*/

    @Override
    public BlockState tatercart_getRailBlock() {
        return this.tatercart_lastRailBlockState;
    }

    @Override
    public boolean tatercart_customPhysics() {
        return this.tatercart_isEnchanced;
    }

    @Override
    public void tatercart_setPhysics(boolean value) {
        this.tatercart_isEnchanced = value;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void customPushing(CallbackInfo ci) {
        if (this.tatercart_isEnchanced && this.world.getGameRules().getBoolean(TaterCartMod.MINECART_HIGH_SPEED_DAMAGE)) {
            var speed = this.getVelocity().horizontalLengthSquared();
            if (speed < 1.4) {
                return;
            }

            var list = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.5F, 0.5D, 0.5F));
            for (var entity : list) {
                if (entity instanceof LivingEntity && !entity.noClip && !this.noClip && !this.getPassengerList().contains(entity) && !entity.hasVehicle()) {
                    entity.damage(DamageSource.FLY_INTO_WALL, (float) speed);
                    entity.addVelocity(this.getVelocity().x, this.getVelocity().y + 0.5, this.getVelocity().z);
                }
            }
        }
    }

    /*@Override
    public AbstractMinecartEntity getNextLinked() {
        return this.nextLinkedMinecart;
    }

    @Override
    public void setNextLinked(AbstractMinecartEntity minecart) {
        this.nextLinkedMinecart = minecart;
    }

    @Override
    public AbstractMinecartEntity getPreviousLinked() {
        return this.previousLinkedMinecart;
    }

    @Override
    public void setPreviousLinked(AbstractMinecartEntity minecart) {
        this.previousLinkedMinecart = minecart;
    }

    @Override
    public boolean canLink() {
        return true;
    }*/

    @Override
    public boolean tatercart_isYawFlipped() {
        return this.yawFlipped;
    }

    @Unique
    private AbstractMinecartEntity asEntity() {
        return (AbstractMinecartEntity) (Object) this;
    }
}
