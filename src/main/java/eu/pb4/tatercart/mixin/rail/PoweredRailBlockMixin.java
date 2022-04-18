package eu.pb4.tatercart.mixin.rail;

import eu.pb4.tatercart.block.TcBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin {
    @Redirect(method = "isPoweredByOtherRails(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ZILnet/minecraft/block/enums/RailShape;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean tatercart_allowCustomPoweredRailBlocks(BlockState blockState, Block block) {
        return blockState.getBlock() instanceof PoweredRailBlock;
    }

    @Inject(method = "isPoweredByOtherRails(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ZILnet/minecraft/block/enums/RailShape;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z", shift = At.Shift.BEFORE, ordinal = 0), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void tatercart_alwaysPoweredIsAlsoASource(World world, BlockPos pos, boolean bl, int distance, RailShape shape, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        if (blockState.isOf(TcBlocks.ALWAYS_POWERED_RAIL)) {
            cir.setReturnValue(true);
        }
    }
}
