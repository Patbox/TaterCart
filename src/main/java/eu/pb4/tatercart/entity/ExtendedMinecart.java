package eu.pb4.tatercart.entity;

import eu.pb4.tatercart.entity.minecart.storage.DispenserMinecartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public interface ExtendedMinecart {
    static boolean isYawFlipped(DispenserMinecartEntity minecart) {
        return of(minecart).tatercart_isYawFlipped();
    }
    /*AbstractMinecartEntity getNextLinked();
    void setNextLinked(AbstractMinecartEntity minecart);
    AbstractMinecartEntity getPreviousLinked();
    void setPreviousLinked(AbstractMinecartEntity minecart);

    boolean canLink();*/

    BlockState tatercart_getRailBlock();

    boolean tatercart_isEnchanced();
    boolean tatercart_isYawFlipped();
    void tatercart_setEnchanced(boolean value);

    static ExtendedMinecart of(AbstractMinecartEntity minecart) {
        return (ExtendedMinecart) minecart;
    }
}
