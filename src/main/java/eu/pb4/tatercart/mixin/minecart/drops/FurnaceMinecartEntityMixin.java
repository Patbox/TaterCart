package eu.pb4.tatercart.mixin.minecart.drops;

import eu.pb4.tatercart.TaterCartMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartEntityMixin extends Entity {
    public FurnaceMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyArg(method = "dropItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/FurnaceMinecartEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;"))
    private ItemConvertible tatercart_dropsChange(ItemConvertible par1) {
        if (this.world.getGameRules().getBoolean(TaterCartMod.SPLIT_ITEMS)) {
            return par1;
        }
        return Items.FURNACE_MINECART;
    }
}
