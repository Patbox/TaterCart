package eu.pb4.tatercart.mixin.minecart.drops;


import eu.pb4.tatercart.other.TcGameRules;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HopperMinecartEntity.class)
public abstract class HopperMinecartEntityMixin extends AbstractMinecartEntity {
    protected HopperMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "dropItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/HopperMinecartEntity;dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;"))
    private ItemConvertible tatercart_dropsChange(ItemConvertible par1) {
        if (this.world.getGameRules().getBoolean(TcGameRules.SPLIT_ITEMS)) {
            return par1;
        }
        return Items.HOPPER;
    }
}