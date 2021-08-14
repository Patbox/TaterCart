package eu.pb4.tatercart.mixin.minecart;

import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.DyeColor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(AbstractMinecartEntity.Type.class)
public class AbstractMinecartEntityTypeMixin {
    @Shadow(aliases = "field_7673")
    @Mutable
    private static AbstractMinecartEntity.Type[] values;

    @SuppressWarnings({"InvokerTarget", "unused", "SameParameterValue"})
    @Invoker("<init>")
    private static AbstractMinecartEntity.Type newType(String internalName, int internalId) {
        throw new AssertionError();
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At(value = "FIELD",
            opcode = Opcodes.PUTSTATIC,
            target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity$Type;field_7673:[Lnet/minecraft/entity/vehicle/AbstractMinecartEntity$Type;",
            shift = At.Shift.AFTER))
    private static void addCustomType(CallbackInfo ci) {
        var types = new ArrayList<>(Arrays.asList(values));
        var last = types.get(types.size() - 1);
        var lastId = last.ordinal() + 1;

        for (var dyeColor : DyeColor.values()) {
            types.add(CustomMinecartType.COLORED.put(dyeColor, newType(dyeColor.name(), lastId++)));
        }

        types.add(CustomMinecartType.SLIME = newType("slime_minecart", lastId++));
        types.add(CustomMinecartType.BARREL = newType("barrel_minecart", lastId++));

        values = types.toArray(AbstractMinecartEntity.Type[]::new);
    }
}
