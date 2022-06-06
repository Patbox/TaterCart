package eu.pb4.tatercart.entity.minecart.other;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.entity.minecart.CustomMinecartEntity;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.item.TcItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PocketMinecartEntity extends CustomMinecartEntity {
    public PlayerEntity owner = null;

    public PocketMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        ExtendedMinecart.of(this).tatercart_setCanLink(false);
    }


    @Override
    protected Item getItem() {
        return TcItems.POCKET_MINECART;
    }

    @Override
    public Type getMinecartType() {
        return CustomMinecartType.POCKET;
    }

    @Override
    public void tick() {
        if (!this.hasPassengers()) {
            this.remove(RemovalReason.KILLED);
            if (this.owner != null) {
                if (!this.owner.isCreative()) {
                    this.owner.giveItemStack(new ItemStack(TcItems.POCKET_MINECART));
                }
            } else {
                this.dropItems(DamageSource.GENERIC);
            }
        } else if (this.owner == null) {
            this.owner = this.getFirstPassenger() instanceof PlayerEntity player ? player : null;
        }
        super.tick();
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        if (powered) {
            if (this.hasPassengers()) {
                this.removeAllPassengers();
            }

            this.remove(RemovalReason.KILLED);

            if (this.owner != null) {
                this.owner.giveItemStack(new ItemStack(TcItems.POCKET_MINECART));
            } else {
                this.dropItems(DamageSource.GENERIC);
            }
        }
    }
}
