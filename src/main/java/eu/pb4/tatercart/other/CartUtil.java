package eu.pb4.tatercart.other;

import eu.pb4.tatercart.entity.ExtendedMinecart;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3d;

public class CartUtil {
    public static void setVelocityForLink(AbstractMinecartEntity minecart, AbstractMinecartEntity linked) {
        var rail = ((ExtendedMinecart) minecart).tatercart_getRailBlock();
        if (rail != null) {
            double distance = minecart.getPos().distanceTo(linked.getPos());
            //if (distance > 0.3) {
                double deltaX = minecart.getX() - linked.getX();
                double deltaZ = minecart.getZ() - linked.getZ();
                //double velocity = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 0.05;

                var shape = rail.get(((AbstractRailBlock) rail.getBlock()).getShapeProperty());
                Vec3d vec = switch (shape) {
                    case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> new Vec3d(1, 0, 0);
                    case EAST_WEST, ASCENDING_WEST, ASCENDING_EAST -> new Vec3d(0, 0, 1);
                    case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST -> new Vec3d(0.707, 0, 0.707);
                };
                minecart.addVelocity(deltaX * vec.x, 0, deltaZ * vec.z);
           // }
        } else {
            //minecart.addVelocity();
        }
    }
}
