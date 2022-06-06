package eu.pb4.tatercart.entity.minecart.storage;

import eu.pb4.tatercart.entity.Directional;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.mixin.DispenserBlockAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class DispenserMinecartEntity extends CustomStorageMinecartEntity implements Directional {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
    protected BlockState dispenseState;
    private int lastPoweredTime = -1;
    private BlockPointer blockPointer;
    private Direction pointingDirection;

    public DispenserMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.pointingDirection = Direction.UP;
        this.dispenseState = Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, Direction.UP);
        this.setVisualBlockState(this.dispenseState);
        this.setVisualBlockOffset(8);
    }

    @Override
    protected DefaultedList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected ScreenHandler getScreenHandler(int i, PlayerInventory playerInventory) {
        return new Generic3x3ContainerScreenHandler(i, playerInventory, this);
    }

    @Override
    public Type getMinecartType() {
        return CustomMinecartType.DISPENSER;
    }

    protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
        return DispenserBlockAccessor.getBEHAVIORS().get(stack.getItem());
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        if (powered && this.age - this.lastPoweredTime > 1) {
            this.dispense((ServerWorld) this.world, new BlockPos(x, y, z));
        }
        this.lastPoweredTime = this.age;
    }

    public int chooseNonEmptySlot() {
        this.generateLoot(null);
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.items.size(); ++k) {
            if (!this.items.get(k).isEmpty() && this.random.nextInt(j++) == 0) {
                i = k;
            }
        }

        return i;
    }

    protected void dispense(ServerWorld world, BlockPos pos) {
        int i = this.chooseNonEmptySlot();

        if (i < 0) {
            world.syncWorldEvent(1001, pos, 0);
            world.emitGameEvent(this, GameEvent.DISPENSE_FAIL, pos);
        } else {
            ItemStack itemStack = this.getStack(i);
            DispenserBehavior dispenserBehavior = this.getBehaviorForItem(itemStack);
            if (dispenserBehavior != DispenserBehavior.NOOP) {
                try {
                    this.setStack(i, dispenserBehavior.dispense(this.getPointer(), itemStack));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected BlockPointer getPointer() {
        if (this.blockPointer == null) {
            this.blockPointer = new BlockPointer() {
                @Override
                public double getX() {
                    return DispenserMinecartEntity.this.getX();
                }

                @Override
                public double getY() {
                    return DispenserMinecartEntity.this.getY() + DispenserMinecartEntity.this.getHeight();
                }

                @Override
                public double getZ() {
                    return DispenserMinecartEntity.this.getZ();
                }

                @Override
                public BlockPos getPos() {
                    return DispenserMinecartEntity.this.getBlockPos();
                }

                @Override
                public BlockState getBlockState() {
                    return DispenserMinecartEntity.this.dispenseState;
                }

                @Override
                public <T extends BlockEntity> T getBlockEntity() {
                    return (T) new DispenserBlockEntity(this.getPos(), DispenserMinecartEntity.this.dispenseState) {
                        @Nullable
                        @Override
                        public World getWorld() {
                            return DispenserMinecartEntity.this.world;
                        }

                        @Override
                        public BlockPos getPos() {
                            return DispenserMinecartEntity.this.getBlockPos();
                        }

                        @Override
                        public BlockState getCachedState() {
                            return DispenserMinecartEntity.this.dispenseState;
                        }

                        @Override
                        protected DefaultedList<ItemStack> getInvStackList() {
                            return DispenserMinecartEntity.this.items;
                        }
                    };
                }

                @Override
                public ServerWorld getWorld() {
                    return (ServerWorld) DispenserMinecartEntity.this.world;
                }
            };
        }

        return this.blockPointer;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateVisualState();
    }

    @Override
    protected Item getItem() {
        return TcItems.DISPENSER_MINECART;
    }

    private void updateVisualState() {
        this.setVisualBlockState(this.dispenseState);

        /*if (visual.get(DispenserBlock.FACING).getAxis() == Direction.Axis.Y || this.pointingDirection.getAxis() == Direction.Axis.Y) {
            if (visual != this.dispenseState) {
                this.setVisualBlockState(this.dispenseState);
            }
        } else {
            var forward = (ExtendedMinecart.isYawFlipped(this) ? this.getMovementDirection().getOpposite() : this.getMovementDirection());
            this.setVisualBlockState(this.dispenseState.with(DispenserBlock.FACING, forward));
        }*/
    }

    @Override
    public void setFacingDirection(Direction direction) {
        /*this.dispenseState = this.dispenseState.with(DispenserBlock.FACING, direction);
        this.pointingDirection = direction;
        this.updateVisualState();*/
    }

    @Override
    public Direction getFacingDirection() {
        return this.pointingDirection;
    }
}
