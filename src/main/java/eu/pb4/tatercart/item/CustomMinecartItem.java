package eu.pb4.tatercart.item;

import eu.pb4.polymer.api.item.PolymerItem;
import eu.pb4.polymer.api.item.PolymerItemUtils;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import net.minecraft.server.network.ServerPlayerEntity;

public class CustomMinecartItem extends MinecartItem implements PolymerItem {
    private static final Item.Settings MINECART_SETTINGS = new Item.Settings().maxCount(1).group(TcItems.ITEM_GROUP);

    public CustomMinecartItem(AbstractMinecartEntity.Type type) {
        super(type, MINECART_SETTINGS);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, ServerPlayerEntity player) {
        return Items.MINECART;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, ServerPlayerEntity player) {
        var out = PolymerItemUtils.createItemStack(itemStack, player);
        out.addEnchantment(Enchantments.BINDING_CURSE, 0);
        return out;
    }
}
