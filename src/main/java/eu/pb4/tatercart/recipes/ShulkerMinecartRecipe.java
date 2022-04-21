package eu.pb4.tatercart.recipes;

import com.google.gson.JsonObject;
import eu.pb4.polymer.api.item.PolymerRecipe;
import eu.pb4.tatercart.item.TcItems;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ShulkerMinecartRecipe extends SpecialCraftingRecipe implements PolymerRecipe {
    private static final ItemStack DEFAULT_OUTPUT = new ItemStack(TcItems.SHULKER_MINECART);

    public ShulkerMinecartRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        var size = inventory.size();

        boolean hasMinecart = false, hasShulker = false;

        for (int i = 0; i < size; i++) {
            var stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.isOf(Items.MINECART)) {
                    if (hasMinecart) {
                        return false;
                    } else {
                        hasMinecart = true;
                    }
                } else if (Registry.ITEM.getId(stack.getItem()).getPath().endsWith("shulker_box")) {
                    if (hasShulker) {
                        return false;
                    } else {
                        hasShulker = true;
                    }
                }
            }
        }
        return hasMinecart && hasShulker;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack out = new ItemStack(TcItems.SHULKER_MINECART);

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (Registry.ITEM.getId(stack.getItem()).getPath().endsWith("shulker_box")) {
                out.getOrCreateNbt().put("ShulkerBox", stack.writeNbt(new NbtCompound()));
                break;
            }
        }

        return out;
    }


    @Override
    public ItemStack getOutput() {
        return DEFAULT_OUTPUT;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height > 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static final RecipeSerializer<ShulkerMinecartRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public ShulkerMinecartRecipe read(Identifier id, JsonObject json) {
            return new ShulkerMinecartRecipe(id);
        }

        @Override
        public ShulkerMinecartRecipe read(Identifier id, PacketByteBuf buf) {
            return new ShulkerMinecartRecipe(id);
        }

        @Override
        public void write(PacketByteBuf buf, ShulkerMinecartRecipe recipe) {
            buf.writeIdentifier(recipe.getId());
        }
    };
}
