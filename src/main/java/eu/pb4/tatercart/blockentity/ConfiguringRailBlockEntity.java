package eu.pb4.tatercart.blockentity;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.other.TcGameRules;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public final class ConfiguringRailBlockEntity extends BlockEntity {
    protected static final String PHYSICS_TAG = "enchanced_physics";
    protected static final String BRAKES_TAG = "brakes";
    protected static final String SPEED_TAG = "max_speed";

    protected TriState enchancedPhysics = TriState.DEFAULT;
    protected TriState brakesState = TriState.DEFAULT;
    protected double speed = 0;


    public ConfiguringRailBlockEntity(BlockPos pos, BlockState state) {
        super(TcBlockEntities.CONTROLLER_RAIL, pos, state);
    }

    public void applyChanges(ExtendedMinecart minecart) {
        if (this.enchancedPhysics != TriState.DEFAULT) {
            minecart.tatercart_setPhysics(this.enchancedPhysics.get());
        }

        if (this.brakesState != TriState.DEFAULT) {
            minecart.tatercart_setBrakes(this.brakesState.get());
        }

        if (this.speed > 0) {
            minecart.tatercart_setSpeed(this.speed);
        }
    }

    public void openGui(ServerPlayerEntity player) {
        new Gui(player).open();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putByte(PHYSICS_TAG, toByte(this.enchancedPhysics));
        nbt.putByte(BRAKES_TAG, toByte(this.brakesState));
        nbt.putDouble(SPEED_TAG, this.speed);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(PHYSICS_TAG)) {
            this.enchancedPhysics = fromByte(nbt.getByte(PHYSICS_TAG));
        }
        if (nbt.contains(BRAKES_TAG)) {
            this.brakesState = fromByte(nbt.getByte(BRAKES_TAG));
        }
        if (nbt.contains(SPEED_TAG)) {
            this.speed = nbt.getDouble(SPEED_TAG);
        }
    }

    private byte toByte(TriState state) {
        return switch (state) {
            case TRUE -> 1;
            case FALSE -> 0;
            case DEFAULT -> -1;
        };
    }

    private TriState fromByte(byte value) {
        return switch (value) {
            case 1 -> TriState.TRUE;
            case 0 -> TriState.FALSE;
            default -> TriState.DEFAULT;
        };
    }


    protected class Gui extends SimpleGui {
        private final ConfiguringRailBlockEntity controller = ConfiguringRailBlockEntity.this;

        private TriState currentBrakesState = null;
        private TriState currentEnchancedPhysics = null;
        private double currentSpeed = -999;

        public Gui(ServerPlayerEntity player) {
            super(ScreenHandlerType.HOPPER, player, false);
            this.setTitle(this.controller.getCachedState().getBlock().getName());
            this.updateInterface();
        }

        private void updateInterface() {
            if (this.controller.enchancedPhysics != this.currentEnchancedPhysics) {
                this.currentEnchancedPhysics = this.controller.enchancedPhysics;

                this.setSlot(0, new GuiElementBuilder(Items.REDSTONE)
                        .setName(Text.translatable("gui.tatercart.configure.enchanced_physics", fromTriState(this.currentEnchancedPhysics)))
                        .setCallback((x, y, z) -> {
                            this.controller.enchancedPhysics = nextTriState(this.controller.enchancedPhysics);
                            this.controller.markDirty();
                        })
                );
            }

            if (this.controller.speed != this.currentSpeed) {
                this.currentSpeed = this.controller.speed;

                this.setSlot(2, new GuiElementBuilder(Items.FEATHER)
                        .setName(Text.translatable("gui.tatercart.configure.speed", this.currentSpeed > 0
                                        ? Text.translatable("text.tatercart.blocks_per_second", this.currentSpeed).formatted(Formatting.GREEN)
                                        : Text.translatable("gui.tatercart.configure.keep_current").formatted(Formatting.GRAY)
                                ))
                        .setCallback((x, y, z) -> {
                            if (y.isRight || y.isLeft) {
                                double change = y.isLeft ? -1 : 1;

                                if (y.shift) {
                                    change *= 10;
                                }

                                this.controller.speed = Math.max(Math.min(this.controller.speed + change, this.player.world.getGameRules().get(TcGameRules.MAX_MINECART_SPEED).get()), 0);
                                this.controller.markDirty();
                            }
                        })
                );
            }

            if (this.controller.brakesState != this.currentBrakesState) {
                this.currentBrakesState = this.controller.brakesState;

                this.setSlot(4, new GuiElementBuilder(Items.IRON_INGOT)
                        .setName(Text.translatable("gui.tatercart.configure.brakes", fromTriState(this.currentBrakesState)))
                        .setCallback((x, y, z) -> {
                            this.controller.brakesState = nextTriState(this.controller.brakesState);
                            this.controller.markDirty();
                        })
                );
            }
        }

        private static Text fromTriState(TriState state) {
            return switch (state) {
                case TRUE -> Text.translatable("gui.tatercart.configure.enabled").formatted(Formatting.GREEN);
                case FALSE -> Text.translatable("gui.tatercart.configure.disabled").formatted(Formatting.RED);
                case DEFAULT -> Text.translatable("gui.tatercart.configure.keep_current").formatted(Formatting.GRAY);
            };
        }

        private static TriState nextTriState(TriState state) {
            return switch (state) {
                case TRUE -> TriState.FALSE;
                case FALSE -> TriState.DEFAULT;
                case DEFAULT -> TriState.TRUE;
            };
        }

        @Override
        public void onTick() {
            this.updateInterface();
        }
    }
}
