package eu.pb4.tatercart.entity.minecart.storage;

import eu.pb4.tatercart.item.TcItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DropperMinecartEntity extends DispenserMinecartEntity {
    private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

    public DropperMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.dispenseState = Blocks.DROPPER.getDefaultState().with(DispenserBlock.FACING, Direction.UP);
        this.setVisualBlockState(this.dispenseState);
    }

    @Override
    protected @Nullable Item getDropItem() {
        return Items.DROPPER;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return TcItems.DROPPER_MINECART.getDefaultStack();
    }

    @Override
    protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
        return BEHAVIOR;
    }

    protected void dispense(ServerWorld world, BlockPos pos) {
        int i = this.chooseNonEmptySlot();
        if (i < 0) {
            world.syncWorldEvent(1001, pos, 0);
        } else {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) {
                Direction direction = this.getFacingDirection();
                Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
                ItemStack itemStack2;
                if (inventory == null) {
                    itemStack2 = BEHAVIOR.dispense(this.getPointer(), itemStack);
                } else {
                    itemStack2 = HopperBlockEntity.transfer(this, inventory, itemStack.copy().split(1), direction.getOpposite());
                    if (itemStack2.isEmpty()) {
                        itemStack2 = itemStack.copy();
                        itemStack2.decrement(1);
                    } else {
                        itemStack2 = itemStack.copy();
                    }
                }

                this.setStack(i, itemStack2);
            }
        }
    }
}
