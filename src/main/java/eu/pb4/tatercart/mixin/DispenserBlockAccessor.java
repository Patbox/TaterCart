package eu.pb4.tatercart.mixin;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(DispenserBlock.class)
public interface DispenserBlockAccessor {
    @Accessor
    static Map<Item, DispenserBehavior> getBEHAVIORS() {
        throw new UnsupportedOperationException();
    }
}
