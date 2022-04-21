package eu.pb4.tatercart.entity.minecart.storage;

import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class BarrelMinecartEntity extends CustomStorageMinecartEntity {
    private final DefaultedList<ItemStack> inventory;

    public BarrelMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
        this.setVisualBlockState(Blocks.BARREL.getDefaultState().with(Properties.FACING, Direction.UP));
        this.setVisualBlockOffset(6);
    }

    @Override
    protected DefaultedList<ItemStack> getItems() {
        return this.inventory;
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
}
