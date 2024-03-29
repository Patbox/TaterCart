package eu.pb4.tatercart.other;

import com.google.common.collect.AbstractIterator;
import eu.pb4.tatercart.entity.ExtendedMinecart;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class MinecartBlockCollisionSpliterator extends AbstractIterator<VoxelShape> {
    private final Box box;
    private final ShapeContext context;
    private final CuboidBlockIterator blockIterator;
    private final BlockPos.Mutable pos;
    private final VoxelShape boxShape;
    private final CollisionView world;
    private final boolean forEntity;
    private final ExtendedMinecart minecart;
    private final Entity entity;
    @Nullable
    private BlockView chunk;
    private long chunkPos;

    public MinecartBlockCollisionSpliterator(CollisionView world, @Nullable Entity entity, Box box) {
        this(world, entity, box, false);
    }

    public MinecartBlockCollisionSpliterator(CollisionView world, @Nullable Entity entity, Box box, boolean forEntity) {
        this.context = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
        this.minecart = (ExtendedMinecart) entity;
        this.entity = entity;
        this.pos = new BlockPos.Mutable();
        this.boxShape = VoxelShapes.cuboid(box);
        this.world = world;
        this.box = box;
        this.forEntity = forEntity;
        int i = MathHelper.floor(box.minX - 1.0E-7D) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7D) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7D) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7D) + 1;
        int m = MathHelper.floor(box.minZ - 1.0E-7D) - 1;
        int n = MathHelper.floor(box.maxZ + 1.0E-7D) + 1;
        this.blockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
    }


    public static Iterable<VoxelShape> create(CollisionView view, Entity entity, Box box) {
        return () -> new MinecartBlockCollisionSpliterator(view, entity, box);
    }

    @Nullable
    private BlockView getChunk(int x, int z) {
        int i = ChunkSectionPos.getSectionCoord(x);
        int j = ChunkSectionPos.getSectionCoord(z);
        long l = ChunkPos.toLong(i, j);
        if (this.chunk != null && this.chunkPos == l) {
            return this.chunk;
        } else {
            BlockView blockView = this.world.getChunkAsView(i, j);
            this.chunk = blockView;
            this.chunkPos = l;
            return blockView;
        }
    }

    protected VoxelShape computeNext() {
        while (true) {
            if (this.blockIterator.step()) {
                int i = this.blockIterator.getX();
                int j = this.blockIterator.getY();
                int k = this.blockIterator.getZ();
                int l = this.blockIterator.getEdgeCoordinatesCount();
                if (l == 3) {
                    continue;
                }

                BlockView blockView = this.getChunk(i, k);
                if (blockView == null) {
                    continue;
                }

                this.pos.set(i, j, k);
                BlockState blockState = blockView.getBlockState(this.pos);
                if (this.forEntity && !blockState.shouldSuffocate(blockView, this.pos) || l == 1 && !blockState.exceedsCube() || l == 2 && !blockState.isOf(Blocks.MOVING_PISTON)) {
                    continue;
                }

                var railBlock = this.minecart.tatercart_getRailBlock();
                if ((railBlock != null) && (
                        (this.entity.getBlockY() > j)
                                || ((CartUtil.getShape(railBlock).isAscending()
                                || CartUtil.isShapeAscending(this.world.getBlockState(this.entity.getBlockPos().offset(this.entity.getMovementDirection()))))
                                && (this.entity.getBlockY() == j))
                )) {
                    continue;
                }

                VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);
                if (voxelShape == VoxelShapes.fullCube()) {
                    if (!this.box.intersects(i, j, k, i + 1.0D, j + 1.0D, k + 1.0D)) {
                        continue;
                    }

                    return voxelShape.offset(i, j, k);
                }

                VoxelShape voxelShape2 = voxelShape.offset(i, j, k);

                if (!VoxelShapes.matchesAnywhere(voxelShape2, this.boxShape, BooleanBiFunction.AND)) {
                    continue;
                }

                return voxelShape2;
            }

            return this.endOfData();
        }
    }
}
