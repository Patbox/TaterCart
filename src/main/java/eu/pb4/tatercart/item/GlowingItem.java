package eu.pb4.tatercart.item;

import eu.pb4.polymer.api.item.SimplePolymerItem;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class GlowingItem extends SimplePolymerItem {
    public GlowingItem(Settings settings, Item virtualItem) {
        super(settings, virtualItem);
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, ServerPlayerEntity player) {
        ItemStack out = super.getPolymerItemStack(itemStack, player);
        out.addEnchantment(Enchantments.BINDING_CURSE, 0);
        return out;
    }
}