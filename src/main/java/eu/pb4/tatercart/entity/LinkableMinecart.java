package eu.pb4.tatercart.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

public interface LinkableMinecart {
    AbstractMinecartEntity getNextLinked();
    void setNextLinked(AbstractMinecartEntity minecart);
    AbstractMinecartEntity getPreviousLinked();
    void setPreviousLinked(AbstractMinecartEntity minecart);

    boolean canLink();


    BlockState getRailBlock();
}
