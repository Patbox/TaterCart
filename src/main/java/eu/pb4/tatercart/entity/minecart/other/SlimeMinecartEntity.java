package eu.pb4.tatercart.entity.minecart.other;

import eu.pb4.tatercart.entity.minecart.CustomMinecartEntity;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.mixin.accessor.EntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SlimeMinecartEntity extends CustomMinecartEntity {
    private boolean isSlimeVisible = false;

    public SlimeMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.setVisualBlockState(Blocks.MOSS_CARPET.getDefaultState());
        this.setVisualBlockOffset(4);
    }

    @Override
    protected Text getDefaultName() {
        return TcItems.SLIME_MINECART.getName();
    }

    @Override
    protected @Nullable Item getDropItem() {
        return Items.SLIME_BLOCK;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return TcItems.SLIME_MINECART.getDefaultStack();
    }

    @Override
    protected void moveOnRail(BlockPos pos, BlockState state) {
        if (this.isSlimeVisible) {
            this.setVisualBlockState(Blocks.MOSS_CARPET.getDefaultState());
            this.setVisualBlockOffset(4);
            this.isSlimeVisible = false;
        }
        super.moveOnRail(pos, state);
    }

    @Override
    protected void moveOffRail() {
        if (!this.isSlimeVisible) {
            this.setVisualBlockOffset(-8);
            this.setVisualBlockState(Blocks.SLIME_BLOCK.getDefaultState());
            this.isSlimeVisible = true;
        }

        var movement = this.getVelocity();
        Vec3d ad = ((EntityAccessor) this).callAdjustMovementForCollisions(movement);

        if (ad.x != movement.x) {
            this.setVelocity(this.getVelocity().multiply(-0.6, 1, 1));
        }

        if (Math.abs(ad.y - movement.y) > 0.05) {
            this.setVelocity(this.getVelocity().multiply(1, -0.8, 1));
        }

        if (ad.z != movement.z) {
            this.setVelocity(this.getVelocity().multiply(1, 1, -0.6));
        }

        super.moveOffRail();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        } else if (this.hasPassengers()) {
            return ActionResult.PASS;
        } else if (!this.world.isClient) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            return ActionResult.SUCCESS;
        }
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        if (powered) {
            if (this.hasPassengers()) {
                this.removeAllPassengers();
            }

            if (this.getDamageWobbleTicks() == 0) {
                this.setDamageWobbleSide(-this.getDamageWobbleSide());
                this.setDamageWobbleTicks(10);
                this.setDamageWobbleStrength(50.0F);
                this.scheduleVelocityUpdate();
            }
        }
    }

    @Override
    public Type getMinecartType() {
        return CustomMinecartType.SLIME;
    }
}
