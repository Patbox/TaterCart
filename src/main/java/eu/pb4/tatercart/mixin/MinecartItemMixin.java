package eu.pb4.tatercart.mixin;

import eu.pb4.tatercart.entity.Directional;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecartItem.class)
@SuppressWarnings({"InvalidInjectorMethodSignature"})
public class MinecartItemMixin {
    @ModifyVariable(method = "useOnBlock", at = @At("STORE"))
    private AbstractMinecartEntity tatercart_setValues(AbstractMinecartEntity minecart, ItemUsageContext context) {
        if (minecart instanceof Directional directional) {
            if (context.getPlayer() != null && Math.abs(context.getPlayer().getPitch()) > 70) {
                directional.setFacingDirection(context.getPlayer().getPitch() > 0 ? Direction.UP : Direction.DOWN);
            } else {
                directional.setFacingDirection(context.getPlayerFacing().getOpposite());
            }
        }
        return minecart;
    }
}
