package eu.pb4.tatercart.mixin.minecart.movement;

import eu.pb4.tatercart.TaterCart;
import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.mixin.FurnaceMinecartEntityAccessor;
import eu.pb4.tatercart.mixin.accessor.EntityAccessor;
import eu.pb4.tatercart.other.TcGameRules;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements ExtendedMinecart {
    @Unique
    private BlockState tatercart_lastRailBlockState = null;
    @Unique
    private BlockPos tatercart_lastRailBlockPos = null;
    @Unique
    private double tatercart_currentOffset = 0;
    @Unique
    private boolean tatercart_isEnchanced = false;
    @Unique
    private final AbstractMinecartEntity[] tatercart_linkedMinecart = new AbstractMinecartEntity[2];
    @Unique
    private UUID[] tatercart_linkedMinecartUuidTemp = null;

    @Shadow
    private boolean yawFlipped;

    public AbstractMinecartEntityMixin(net.minecraft.entity.EntityType<?> type, net.minecraft.world.World world) {
        super(type, world);
    }

    @Shadow
    protected abstract double getMaxOffRailSpeed();

    @Shadow
    public abstract Direction getMovementDirection();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    private void tatercraft_setDefaultEnhanced(EntityType entityType, World world, CallbackInfo ci) {
        this.tatercart_isEnchanced = world.getGameRules().getBoolean(TcGameRules.DEFAULT_ENHANCED);
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

            cir.setReturnValue((this.asEntity().isTouchingWater() ? 4.0 : 8.0) / (isOnCurved ? 24 : 15.0));
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
        if (this.tatercart_isEnchanced && !this.world.isClient) {
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
        if (this.tatercart_isEnchanced && !this.world.isClient) {
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
        nbt.putBoolean(TaterCart.ID + ":enchanced_physics", this.tatercart_isEnchanced);
        if (this.tatercart_linkedMinecart[0] != null) {
            nbt.putUuid(TaterCart.ID + ":linked_carts/0", this.tatercart_linkedMinecart[0].getUuid());
        } else if (this.tatercart_linkedMinecart[1] != null) {
            nbt.putUuid(TaterCart.ID + ":linked_carts/1", this.tatercart_linkedMinecart[1].getUuid());
        } else if (this.tatercart_linkedMinecartUuidTemp != null) {
            if (this.tatercart_linkedMinecartUuidTemp.length > 0 && this.tatercart_linkedMinecartUuidTemp[0] != null) {
                nbt.putUuid(TaterCart.ID + ":linked_carts/0", this.tatercart_linkedMinecartUuidTemp[0]);
            } else if (this.tatercart_linkedMinecartUuidTemp.length > 1 && this.tatercart_linkedMinecartUuidTemp[1] != null) {
                nbt.putUuid(TaterCart.ID + ":linked_carts/1", this.tatercart_linkedMinecartUuidTemp[1]);
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void tatercart_readData(NbtCompound nbt, CallbackInfo ci) {
        this.tatercart_isEnchanced = nbt.getBoolean(TaterCart.ID + ":enchanced_physics");
        this.tatercart_linkedMinecartUuidTemp = new UUID[2];

        if (nbt.contains(TaterCart.ID + ":linked_carts/0")) {
            this.tatercart_linkedMinecartUuidTemp[0] = nbt.getUuid(TaterCart.ID + ":linked_carts/0");
        }

        if (nbt.contains(TaterCart.ID + ":linked_carts/1")) {
            this.tatercart_linkedMinecartUuidTemp[1] = nbt.getUuid(TaterCart.ID + ":linked_carts/1");
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tatercart_handleLinkedCarts(CallbackInfo ci) {
        if (!(this.world instanceof ServerWorld world)) {
            return;
        }

        if (this.tatercart_linkedMinecartUuidTemp != null) {
            if (this.tatercart_linkedMinecartUuidTemp[0] != null && world.getEntity(this.tatercart_linkedMinecartUuidTemp[0]) instanceof AbstractMinecartEntity minecart) {
                this.tatercart_linkedMinecart[0] = minecart;
            }

            if (this.tatercart_linkedMinecartUuidTemp[1] != null && world.getEntity(this.tatercart_linkedMinecartUuidTemp[1]) instanceof AbstractMinecartEntity minecart) {
                this.tatercart_linkedMinecart[1] = minecart;
            }

            this.tatercart_linkedMinecartUuidTemp = null;
        }

        if (this.world.getGameRules().getBoolean(TcGameRules.MINECART_LINKING) && this.tatercart_isEnchanced) {
            AbstractMinecartEntity mainCart = null;

            for (var cart : this.tatercart_linkedMinecart) {
                if (cart != null) {
                    if (cart.distanceTo(this) > 5 || cart.isRemoved()) {
                        ExtendedMinecart.of(cart).tatercart_removeLinked(this.asEntity());
                        this.tatercart_removeLinked(cart);
                        this.dropItem(Items.CHAIN);
                        continue;
                    }
                    if (((mainCart == null && this.getVelocity().horizontalLengthSquared() <= cart.getVelocity().horizontalLengthSquared())
                            || (mainCart != null && mainCart.getVelocity().horizontalLengthSquared() < cart.getVelocity().horizontalLengthSquared()))
                            && !(mainCart instanceof FurnaceMinecartEntity)
                    ) {
                        mainCart = cart;
                    }
                }
            }

            if (mainCart != null) {
                var distance = this.distanceTo(mainCart);
                var dir = mainCart.getPos().subtract(this.getPos()).add(this.getVelocity()).normalize();

                if (distance > 1.6) {
                    if (!(this.asEntity() instanceof FurnaceMinecartEntity furnaceMinecart && ((FurnaceMinecartEntityAccessor) furnaceMinecart).callIsLit())) {
                        if (mainCart.getVelocity().horizontalLengthSquared() != 0) {
                            var vel = dir.multiply(mainCart.getVelocity().horizontalLength()).multiply(distance - 0.8);
                            this.setVelocity(vel.x, this.getVelocity().y, vel.z);
                        } else {
                            var vel = dir.multiply(0.05);
                            this.setVelocity(vel.x, this.getVelocity().y, vel.z);
                        }
                    }
                } else if (this.getBoundingBox().intersects(mainCart.getBoundingBox()) || distance < 1.4) {
                    var vel = dir.multiply(-1.4 + distance).multiply(0.1);
                    this.setVelocity(vel.x, this.getVelocity().y, vel.z);
                } else {
                    this.setVelocity(this.getVelocity().multiply(0.9, 1, 0.9));
                }
            }
        }
    }

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

        if (!value) {
            for (var cart : this.tatercart_linkedMinecart) {
                if (cart != null) {
                    ExtendedMinecart.of(cart).tatercart_removeLinked(this.asEntity());
                }
            }
        }
    }

    @Inject(method = "collidesWith", at = @At("HEAD"), cancellable = true)
    private void tatercart_collidesWith(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (this.tatercart_isEnchanced && other instanceof AbstractMinecartEntity) {
            cir.setReturnValue(!(this.tatercart_linkedMinecart[0] == other || this.tatercart_linkedMinecart[1] == other));
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tatercart_customPushing(CallbackInfo ci) {
        if (this.tatercart_isEnchanced && this.world.getGameRules().getBoolean(TcGameRules.MINECART_HIGH_SPEED_DAMAGE)) {
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

    @Inject(method = "applySlowdown", at = @At("HEAD"), cancellable = true)
    private void tatercart_changeMaxSpeedToNormal(CallbackInfo ci) {
        if (this.tatercart_isEnchanced && (this.tatercart_linkedMinecart[0] != null || this.tatercart_linkedMinecart[1] != null)) {
            Vec3d vec3d = this.getVelocity();
            vec3d = vec3d.multiply(0.997D, 0.0D, 0.997D);
            if (this.isTouchingWater()) {
                vec3d = vec3d.multiply(0.949999988079071D);
            }

            this.setVelocity(vec3d);
            ci.cancel();
        }
    }

    @Override
    public AbstractMinecartEntity[] tatercart_getLinked() {
        return this.tatercart_linkedMinecart;
    }

    @Override
    public void tatercart_setLinked(AbstractMinecartEntity minecart) {
        if (minecart == this.asEntity()) {
            return;
        }
        if (this.tatercart_linkedMinecart[0] == null) {
            this.tatercart_linkedMinecart[0] = minecart;
        } else if (this.tatercart_linkedMinecart[1] == null) {
            this.tatercart_linkedMinecart[1] = minecart;
        }
    }

    @Override
    public void tatercart_removeLinked(AbstractMinecartEntity minecart) {
        if (this.tatercart_linkedMinecart[0] == minecart) {
            this.tatercart_linkedMinecart[0] = null;
        } else if (this.tatercart_linkedMinecart[1] == minecart) {
            this.tatercart_linkedMinecart[1] = null;
        }
    }

    @Override
    public boolean tatercart_canLink() {
        return this.tatercart_isEnchanced && (this.tatercart_linkedMinecart[0] == null || this.tatercart_linkedMinecart[1] == null);
    }

    @Override
    public boolean tatercart_isYawFlipped() {
        return this.yawFlipped;
    }

    @Unique
    private AbstractMinecartEntity asEntity() {
        return (AbstractMinecartEntity) (Object) this;
    }
}
