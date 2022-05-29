package eu.pb4.tatercart.other;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import org.jetbrains.annotations.Nullable;

public class CartUtil {
    public static final int PHYSICS_VERSION = 1;

    @Nullable
    public static RailShape getShape(BlockState railBlock) {
        if (railBlock.getBlock() instanceof AbstractRailBlock abstractRailBlock) {
            return railBlock.get(abstractRailBlock.getShapeProperty());
        }

        return null;
    }

    public static boolean isShapeAscending(BlockState blockState) {
        var state = CartUtil.getShape(blockState);

        return state != null ? state.isAscending() : false;
    }
}
