package eu.pb4.tatercart.mixin.minecart;

import eu.pb4.holograms.api.elements.SpacingHologramElement;
import eu.pb4.holograms.api.holograms.EntityHologram;
import eu.pb4.tatercart.TaterCart;
import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.entity.minecart.other.ColoredMinecartEntity;
import eu.pb4.tatercart.entity.minecart.other.PocketMinecartEntity;
import eu.pb4.tatercart.entity.minecart.other.SlimeMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.BarrelMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.DispenserMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.DropperMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.ShulkerMinecartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {
    @Unique
    private EntityHologram tatercart_hologram;

    public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void tatercart_onCreate(World world, double x, double y, double z, AbstractMinecartEntity.Type type, CallbackInfoReturnable<AbstractMinecartEntity> cir) {
        var dyeColor = CustomMinecartType.COLORED.inverse().getOrDefault(type, null);
        AbstractMinecartEntity entity;
        if (dyeColor != null) {
            entity = new ColoredMinecartEntity(TcEntities.COLORED_MINECART.get(dyeColor), world);
        } else if (type == CustomMinecartType.SLIME) {
            entity = new SlimeMinecartEntity(TcEntities.SLIME_MINECART, world);
        } else if (type == CustomMinecartType.BARREL) {
            entity = new BarrelMinecartEntity(TcEntities.BARREL_MINECART, world);
        } else if (type == CustomMinecartType.SHULKER) {
            entity = new ShulkerMinecartEntity(TcEntities.SHULKER_MINECART, world);
        } else if (type == CustomMinecartType.DISPENSER) {
            entity = new DispenserMinecartEntity(TcEntities.DISPENSER_MINECART, world);
        } else if (type == CustomMinecartType.DROPPER) {
            entity = new DropperMinecartEntity(TcEntities.DROPPER_MINECART, world);
        } else if (type == CustomMinecartType.POCKET) {
            entity = new PocketMinecartEntity(TcEntities.POCKET_MINECART, world);
        } else {
            entity = null;
        }

        if (entity != null) {
            entity.setPosition(x, y, z);
            cir.setReturnValue(entity);
        }
    }

    @Shadow
    public abstract AbstractMinecartEntity.Type getMinecartType();

    @Inject(method = "tick", at = @At("TAIL"))
    private void tatercart_tick(CallbackInfo ci) {
        if (TaterCart.SHOW_MARKER) {
            if (this.tatercart_hologram == null) {
                this.tatercart_hologram = new EntityHologram(this, Vec3d.ZERO);
                this.tatercart_hologram.setText(0, Text.literal("" + Registry.ENTITY_TYPE.getId(this.getType())), true);
                this.tatercart_hologram.setText(1, Text.literal(""), true);
                this.tatercart_hologram.setText(2, Text.literal(""), true);
                this.tatercart_hologram.setText(3, Text.literal(""), true);
                this.tatercart_hologram.setElement(4, new SpacingHologramElement(1.5));
                this.tatercart_hologram.setItemStack(5, Items.TORCH.getDefaultStack(), true);
                this.tatercart_hologram.show();
            }

            this.tatercart_hologram.setText(1, Text.literal((((ExtendedMinecart) this).tatercart_hasCustomPhysics() ? "TaterCart" : "Vanilla") + " Physics"), true);
            this.tatercart_hologram.setText(2, Text.literal("Pos: " + this.getPos().toString()), true);
            this.tatercart_hologram.setText(3, Text.literal("Vel: " + this.getVelocity().toString()), true);
        }
    }
}
