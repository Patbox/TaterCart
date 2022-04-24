package eu.pb4.tatercart.entity;

import net.minecraft.util.math.Direction;

public interface Directional {
    void setFacingDirection(Direction direction);
    Direction getFacingDirection();
}
