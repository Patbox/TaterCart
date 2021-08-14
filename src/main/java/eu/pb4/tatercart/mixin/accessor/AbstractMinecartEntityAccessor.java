package eu.pb4.tatercart.mixin.accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractMinecartEntity.class)
public interface AbstractMinecartEntityAccessor {
    @Accessor("CUSTOM_BLOCK_ID")
    static TrackedData<Integer> CUSTOM_BLOCK_ID() {
        throw new UnsupportedOperationException();
    }

    @Accessor("CUSTOM_BLOCK_OFFSET")
    static TrackedData<Integer> CUSTOM_BLOCK_OFFSET() {
        throw new UnsupportedOperationException();
    }

    @Accessor("CUSTOM_BLOCK_PRESENT")
    static TrackedData<Boolean> CUSTOM_BLOCK_PRESENT() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    boolean isYawFlipped();
}
