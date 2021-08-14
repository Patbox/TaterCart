package eu.pb4.tatercart.item;

import eu.pb4.polymer.item.VirtualItem;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import net.minecraft.server.network.ServerPlayerEntity;

public class CustomMinecartItem extends MinecartItem implements VirtualItem {
    private static final Item.Settings MINECART_SETTINGS = new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION);

    public CustomMinecartItem(AbstractMinecartEntity.Type type) {
        super(type, MINECART_SETTINGS);
    }

    @Override
    public Item getVirtualItem() {
        return Items.MINECART;
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack itemStack, ServerPlayerEntity player) {
        var out = VirtualItem.super.getVirtualItemStack(itemStack, player);
        out.addEnchantment(Enchantments.BINDING_CURSE, 0);
        return out;
    }
}
