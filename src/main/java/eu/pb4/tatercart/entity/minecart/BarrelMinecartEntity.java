package eu.pb4.tatercart.entity.minecart;

import eu.pb4.polymer.entity.VirtualEntity;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.mixin.accessor.EntityAccessor;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class BarrelMinecartEntity extends StorageMinecartEntity implements VirtualEntity {
    public BarrelMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.setCustomBlockOffset(4);
        this.setCustomBlock(Blocks.BARREL.getDefaultState().with(BarrelBlock.FACING, Direction.UP));
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(Items.BARREL);
        }
    }

    @Override
    protected ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public Type getMinecartType() {
        return CustomMinecartType.BARREL;
    }

    @Override
    public EntityType<?> getVirtualEntityType() {
        return EntityType.MINECART;
    }

    @Override
    public int size() {
        return 27;
    }
}
