package eu.pb4.tatercart.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ZombieEntity.class)
public interface ZombieEntityAccessor {
    @Accessor("BABY")
    static TrackedData<Boolean> BABY() {
        throw new UnsupportedOperationException();
    }
}
