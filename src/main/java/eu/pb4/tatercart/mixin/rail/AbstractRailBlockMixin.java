package eu.pb4.tatercart.mixin.rail;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRailBlock.class)
public abstract class AbstractRailBlockMixin{
    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void changePlaceCheck(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(Block.sideCoversSmallSquare(world, pos.down(), Direction.UP));
    }

    @Inject(method = "shouldDropRail", at = @At("HEAD"), cancellable = true)
    private static void changeDropCheck(BlockPos pos, World world, RailShape shape, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!Block.sideCoversSmallSquare(world, pos.down(), Direction.UP));
    }
}
