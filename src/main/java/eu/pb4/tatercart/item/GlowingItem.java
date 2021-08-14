package eu.pb4.tatercart.item;

import eu.pb4.polymer.item.BasicVirtualItem;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class GlowingItem extends BasicVirtualItem {
    public GlowingItem(Settings settings, Item virtualItem) {
        super(settings, virtualItem);
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack itemStack, ServerPlayerEntity player) {
        ItemStack out = super.getVirtualItemStack(itemStack, player);
        out.addEnchantment(Enchantments.BINDING_CURSE, 0);
        return out;
    }
}