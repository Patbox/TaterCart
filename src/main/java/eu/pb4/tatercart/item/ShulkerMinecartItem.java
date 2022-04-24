package eu.pb4.tatercart.item;

import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.entity.minecart.storage.ShulkerMinecartEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ShulkerMinecartItem extends CustomMinecartItem {
    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
            World world = pointer.getWorld();
            double d = pointer.getX() + (double) direction.getOffsetX() * 1.125D;
            double e = Math.floor(pointer.getY()) + (double) direction.getOffsetY();
            double f = pointer.getZ() + (double) direction.getOffsetZ() * 1.125D;
            BlockPos blockPos = pointer.getPos().offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double g;
            if (blockState.isIn(BlockTags.RAILS)) {
                if (railShape.isAscending()) {
                    g = 0.6D;
                } else {
                    g = 0.1D;
                }
            } else {
                if (!blockState.isAir() || !world.getBlockState(blockPos.down()).isIn(BlockTags.RAILS)) {
                    return this.defaultBehavior.dispense(pointer, stack);
                }

                BlockState blockState2 = world.getBlockState(blockPos.down());
                RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock ? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && railShape2.isAscending()) {
                    g = -0.4D;
                } else {
                    g = -0.9D;
                }
            }

            var abstractMinecartEntity = ShulkerMinecartEntity.create(world, (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.0625D + d, (double) blockPos.getZ() + 0.5D);
            if (stack.hasCustomName()) {
                abstractMinecartEntity.setCustomName(stack.getName());
            }


            if (stack.isOf(TcItems.SHULKER_MINECART) && stack.hasNbt() && stack.getNbt().contains("ShulkerBox")) {
                abstractMinecartEntity.setShulkerBox(ItemStack.fromNbt(stack.getSubNbt("ShulkerBox")));
            }

            world.spawnEntity(abstractMinecartEntity);
            stack.decrement(1);
            return stack;
        }

        protected void playSound(BlockPointer pointer) {
            pointer.getWorld().syncWorldEvent(1000, pointer.getPos(), 0);
        }
    };

    public ShulkerMinecartItem() {
        super(CustomMinecartType.SHULKER, Items.CHEST_MINECART);
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("ShulkerBox", NbtElement.COMPOUND_TYPE)) {
            var item = Registry.ITEM.get(Identifier.tryParse(stack.getSubNbt("ShulkerBox").getString("id")));

            return new TranslatableText("item.tatercart.minecart_with_x", item.getName());
        }

        return this.getName();
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        } else {
            ItemStack itemStack = context.getStack();
            if (!world.isClient) {
                RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                double d = 0.0D;
                if (railShape.isAscending()) {
                    d = 0.5D;
                }

                var abstractMinecartEntity = ShulkerMinecartEntity.create(world, (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.0625D + d, (double) blockPos.getZ() + 0.5D);
                if (itemStack.hasCustomName()) {
                    abstractMinecartEntity.setCustomName(itemStack.getName());
                }

                if (itemStack.isOf(TcItems.SHULKER_MINECART) && itemStack.hasNbt() && itemStack.getNbt().contains("ShulkerBox")) {
                    abstractMinecartEntity.setShulkerBox(ItemStack.fromNbt(itemStack.getSubNbt("ShulkerBox")));
                }

                world.spawnEntity(abstractMinecartEntity);
                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
            }

            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(new ItemStack(this));

            for (var color : DyeColor.values()) {
                var stack = new ItemStack(this);
                stack.getOrCreateSubNbt("ShulkerBox").putString("id", color.getName() + "_shulker_box");
                stack.getOrCreateSubNbt("ShulkerBox").putInt("Count", 1);
                stacks.add(stack);
            }
        }
    }

    public ItemStack from(ItemStack shulker) {
        var stack = new ItemStack(this);
        stack.getOrCreateNbt().put("ShulkerBox", shulker.writeNbt(new NbtCompound()));
        return stack;
    }
}
