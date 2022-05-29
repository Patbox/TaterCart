package eu.pb4.tatercart.item;

import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.entity.minecart.other.PocketMinecartEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PocketMinecartItem extends CustomMinecartItem {
    public PocketMinecartItem() {
        super(CustomMinecartType.POCKET, Items.MINECART);
        DispenserBlock.registerBehavior(this, new ItemDispenserBehavior());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() instanceof ServerPlayerEntity player) {
            World world = context.getWorld();
            BlockPos blockPos = context.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isIn(BlockTags.RAILS)) {
                return ActionResult.FAIL;
            } else {
                ItemStack itemStack = context.getStack();
                RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                double d = 0.0D;
                if (railShape.isAscending()) {
                    d = 0.5D;
                }

                var minecart = (PocketMinecartEntity) AbstractMinecartEntity.create(world, (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.0625D + d, (double) blockPos.getZ() + 0.5D, CustomMinecartType.POCKET);
                if (itemStack.hasCustomName()) {
                    minecart.setCustomName(itemStack.getName());
                }
                itemStack.decrement(1);

                world.spawnEntity(minecart);
                player.startRiding(minecart);
                minecart.owner = player;

                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);


                return ActionResult.success(true);
            }
        }

        return ActionResult.FAIL;
    }
}
