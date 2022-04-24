package eu.pb4.tatercart.item;

import eu.pb4.polymer.api.item.PolymerItem;
import eu.pb4.polymer.api.item.PolymerItemUtils;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CustomMinecartItem extends MinecartItem implements PolymerItem {
    private static final Item.Settings MINECART_SETTINGS = new Item.Settings().maxCount(1).group(TcItems.ITEM_GROUP);
    private final Item polymerItem;

    public CustomMinecartItem(AbstractMinecartEntity.Type type, Item polymerItem) {
        super(type, MINECART_SETTINGS);
        this.polymerItem = polymerItem;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, ServerPlayerEntity player) {
        return this.polymerItem;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, ServerPlayerEntity player) {
        var out = PolymerItemUtils.createItemStack(itemStack, player);
        out.addEnchantment(Enchantments.BINDING_CURSE, 0);
        return out;
    }
}
