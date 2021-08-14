package eu.pb4.tatercart.mixin.minecart;

import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.entity.minecart.BarrelMinecartEntity;
import eu.pb4.tatercart.entity.minecart.ColoredMinecartEntity;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.entity.minecart.SlimeMinecartEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void onCreate(World world, double x, double y, double z, AbstractMinecartEntity.Type type, CallbackInfoReturnable<AbstractMinecartEntity> cir) {
        var dyeColor = CustomMinecartType.COLORED.inverse().getOrDefault(type, null);
        if (dyeColor != null) {
            var entity = new ColoredMinecartEntity(TcEntities.COLORED_MINECART.get(dyeColor), world);
            entity.setPosition(x, y, z);
            cir.setReturnValue(entity);
        } else if (type == CustomMinecartType.SLIME) {
            var entity = new SlimeMinecartEntity(TcEntities.SLIME_MINECART, world);
            entity.setPosition(x, y, z);
            cir.setReturnValue(entity);
        } else if (type == CustomMinecartType.BARREL) {
            var entity = new BarrelMinecartEntity(TcEntities.BARREL_MINECART, world);
            entity.setPosition(x, y, z);
            cir.setReturnValue(entity);
        }
    }
}
