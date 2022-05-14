package eu.pb4.tatercart.item;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class ConnectorChainItem extends GlowingItem {
    public ConnectorChainItem(Settings settings) {
        super(settings, Items.CHAIN);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player && (
                player.getInventory().selectedSlot != slot
                        || !stack.hasNbt()
                        || !(world.getEntityById(stack.getNbt().getInt("TargetMinecart")) instanceof AbstractMinecartEntity minecart
                                && ExtendedMinecart.of(minecart).tatercart_canLink()
                                && minecart.distanceTo(entity) < 6.5
                ))) {
            player.getInventory().setStack(slot, new ItemStack(Items.CHAIN, stack.getCount()));
        }
    }
}
