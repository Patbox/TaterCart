package eu.pb4.tatercart.entity;

import eu.pb4.tatercart.entity.minecart.storage.DispenserMinecartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public interface ExtendedMinecart {
    static boolean isYawFlipped(DispenserMinecartEntity minecart) {
        return of(minecart).tatercart_isYawFlipped();
    }
    AbstractMinecartEntity[] tatercart_getLinked();
    void tatercart_setLinked(AbstractMinecartEntity minecart);
    void tatercart_removeLinked(AbstractMinecartEntity minecart);

    boolean tatercart_canLink();

    BlockState tatercart_getRailBlock();

    boolean tatercart_hasCustomPhysics();
    boolean tatercart_isYawFlipped();
    void tatercart_setPhysics(boolean value);

    static ExtendedMinecart of(AbstractMinecartEntity minecart) {
        return (ExtendedMinecart) minecart;
    }

    void tatercart_setCanLink(boolean value);
    double tatercart_getSpeed();
    void tatercart_setSpeed(double value);
    boolean tatercart_getBrakes();
    void tatercart_setBrakes(boolean value);

    default AbstractMinecartEntity tatercart_asEntity() {
        return (AbstractMinecartEntity) this;
    }
}
