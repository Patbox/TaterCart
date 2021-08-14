package eu.pb4.tatercart.mixin.accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PassiveEntity.class)
public interface PassiveEntityAccessor {
    @Accessor("CHILD")
    static TrackedData<Boolean> CHILD() {
        throw new UnsupportedOperationException();
    }
}
