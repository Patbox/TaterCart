package eu.pb4.tatercart.mixin;

import eu.pb4.tatercart.entity.Directional;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.util.math.BlockPointer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings({"InvalidInjectorMethodSignature"})
@Mixin(targets = "net/minecraft/item/MinecartItem$1")
public class MinecartItemBehaviorMixin {
    @ModifyVariable(method = "dispenseSilently", at = @At("STORE"))
    private AbstractMinecartEntity tatercart_setValues(AbstractMinecartEntity minecart, BlockPointer pointer) {
        if (minecart instanceof Directional directional) {
            directional.setFacingDirection(pointer.getBlockState().get(DispenserBlock.FACING).getOpposite());
        }
        return minecart;
    }
}
