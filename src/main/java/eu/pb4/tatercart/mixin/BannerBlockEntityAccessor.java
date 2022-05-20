package eu.pb4.tatercart.mixin;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BannerBlockEntity.class)
public interface BannerBlockEntityAccessor {
    @Accessor
    NbtList getPatternListNbt();
}
