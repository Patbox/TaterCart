package eu.pb4.tatercart.mixin;

import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FurnaceMinecartEntity.class)
public interface FurnaceMinecartEntityAccessor {
    @Invoker
    boolean callIsLit();
}
