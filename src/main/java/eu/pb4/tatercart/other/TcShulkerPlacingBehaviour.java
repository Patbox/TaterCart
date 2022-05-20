package eu.pb4.tatercart.other;

import eu.pb4.tatercart.block.rail.CustomDetectorRail;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.entity.minecart.storage.ShulkerMinecartEntity;
import eu.pb4.tatercart.mixin.DispenserBlockAccessor;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.registry.Registry;

public class TcShulkerPlacingBehaviour extends FallibleItemDispenserBehavior {
    private final DispenserBehavior originalBehaviour;

    public TcShulkerPlacingBehaviour(Item shulkerBox) {
        this.originalBehaviour = DispenserBlockAccessor.getBEHAVIORS().get(shulkerBox);
    }

    public static void register() {
        DispenserBlock.registerBehavior(Items.SHULKER_BOX, new TcShulkerPlacingBehaviour(Items.SHULKER_BOX));

        for (var color : DyeColor.values()) {
            var item = Registry.ITEM.get(new Identifier(color.getName() + "_shulker_box"));
            DispenserBlock.registerBehavior(item, new TcShulkerPlacingBehaviour(item));
        }
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(false);
        if (stack.isEmpty()) {
            return stack;
        }

        var dir = pointer.getBlockState().get(DispenserBlock.FACING);

        var pos = pointer.getPos().offset(dir);

        var entities = pointer.getWorld().getEntitiesByClass(MinecartEntity.class, CustomDetectorRail.getCartDetectionBox(pos), TcShulkerPlacingBehaviour::isEmpty);

        if (entities.size() > 0) {
            var oldMinecart = entities.get(0);

            var nbtCopy = oldMinecart.writeNbt(new NbtCompound());
            oldMinecart.remove(Entity.RemovalReason.DISCARDED);

            var minecart = new ShulkerMinecartEntity(TcEntities.SHULKER_MINECART, pointer.getWorld());
            minecart.readNbt(nbtCopy);
            this.setSuccess(true);
            minecart.setShulkerBox(stack.copy());
            stack.decrement(1);
            pointer.getWorld().tryLoadEntity(minecart);

            return stack;
        }

        return this.originalBehaviour.dispense(pointer, stack);
    }

    private static <T extends Entity> boolean isEmpty(T t) {
        return t instanceof MinecartEntity minecart && minecart.getType() == EntityType.MINECART && minecart.getPassengerList().size() == 0;
    }
}
