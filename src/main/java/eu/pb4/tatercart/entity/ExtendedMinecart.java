package eu.pb4.tatercart.entity;

import net.minecraft.block.BlockState;

public interface ExtendedMinecart {
    /*AbstractMinecartEntity getNextLinked();
    void setNextLinked(AbstractMinecartEntity minecart);
    AbstractMinecartEntity getPreviousLinked();
    void setPreviousLinked(AbstractMinecartEntity minecart);

    boolean canLink();*/

    BlockState tatercart_getRailBlock();

    boolean tatercart_isEnchanced();
    void tatercart_setEnchanced(boolean value);
}
