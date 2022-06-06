package eu.pb4.tatercart.item;

import eu.pb4.polymer.api.item.PolymerItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpeedometerItem extends GlowingItem {
    public SpeedometerItem(Settings settings) {
        super(settings, Items.CLOCK);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected) {
            var currentPos = entity.getPos();
            if (stack.hasNbt() && entity instanceof ServerPlayerEntity player) {
                var list = stack.getNbt().getList("lastPos", NbtElement.DOUBLE_TYPE);
                var vec = new Vec3d(list.getDouble(0), list.getDouble(1), list.getDouble(2)).relativize(currentPos);

                player.sendMessage(Text.translatable("text.tatercart.speedometer_info",
                        Text.translatable("text.tatercart.blocks_per_second", Math.round(vec.length() * 20 * 100) / 100d)
                ), true);
            }

            var list = new NbtList();
            list.add(NbtDouble.of(currentPos.x));
            list.add(NbtDouble.of(currentPos.y));
            list.add(NbtDouble.of(currentPos.z));
            stack.getOrCreateNbt().put("lastPos", list);
        } else if (stack.hasNbt() && stack.getNbt().contains("lastPos")) {
            stack.getNbt().remove("lastPos");
            if (entity instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.empty(), true);
            }
        }
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, ServerPlayerEntity player) {
        var stack = super.getPolymerItemStack(itemStack, player);

        if (itemStack.hasNbt() && itemStack.getNbt().contains("lastPos")) {
            stack.getNbt().getCompound(PolymerItemUtils.REAL_TAG).remove("lastPos");
        }
        return stack;
    }
}
