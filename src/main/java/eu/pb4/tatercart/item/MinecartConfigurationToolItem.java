package eu.pb4.tatercart.item;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.tatercart.entity.ExtendedMinecart;
import eu.pb4.tatercart.other.TcGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MinecartConfigurationToolItem extends GlowingItem {
    public MinecartConfigurationToolItem(Settings settings) {
        super(settings, Items.BLAZE_ROD);
    }

    public void openGui(ServerPlayerEntity player, ExtendedMinecart minecart) {
        new Gui(player, minecart).open();
    }


    protected class Gui extends SimpleGui {
        private final ExtendedMinecart controller;

        private boolean currentBrakesState = false;
        private boolean currentEnchancedPhysics = false;
        private double currentSpeed = -999;

        public Gui(ServerPlayerEntity player, ExtendedMinecart minecart) {
            super(ScreenHandlerType.HOPPER, player, false);
            this.controller = minecart;
            this.setTitle(((Entity) minecart).getDisplayName());
            this.updateInterface(true);
        }

        private void updateInterface(boolean force) {
            if (force || this.controller.tatercart_hasCustomPhysics() != this.currentEnchancedPhysics) {
                this.currentEnchancedPhysics = this.controller.tatercart_hasCustomPhysics();

                this.setSlot(0, new GuiElementBuilder(Items.REDSTONE)
                        .setName(new TranslatableText("gui.tatercart.configure.enchanced_physics", fromBoolean(this.currentEnchancedPhysics)))
                        .setCallback((x, y, z) -> {
                            this.controller.tatercart_setPhysics(!this.currentEnchancedPhysics);
                        })
                );
            }

            if (force || this.controller.tatercart_getSpeed() != this.currentSpeed) {
                this.currentSpeed = this.controller.tatercart_getSpeed();

                this.setSlot(2, new GuiElementBuilder(Items.FEATHER)
                        .setName(new TranslatableText("gui.tatercart.configure.speed",
                                new TranslatableText("text.tatercart.blocks_per_second", this.currentSpeed).formatted(Formatting.GREEN)))
                        .setCallback((x, y, z) -> {
                            if (y.isRight || y.isLeft) {
                                double change = y.isLeft ? -1 : 1;

                                if (y.shift) {
                                    change *= 10;
                                }

                                this.controller.tatercart_setSpeed(Math.max(Math.min(this.currentSpeed + change, this.player.world.getGameRules().get(TcGameRules.MAX_MINECART_SPEED).get()), 0.1));
                            }
                        })
                );
            }

            if (force || this.controller.tatercart_getBrakes() != this.currentBrakesState) {
                this.currentBrakesState = this.controller.tatercart_getBrakes();

                this.setSlot(4, new GuiElementBuilder(Items.IRON_INGOT)
                        .setName(new TranslatableText("gui.tatercart.configure.brakes", fromBoolean(this.currentBrakesState)))
                        .setCallback((x, y, z) -> {
                            this.controller.tatercart_setBrakes(!this.currentBrakesState);
                        })
                );
            }
        }

        private static Text fromBoolean(boolean state) {
            return state
                    ? new TranslatableText("gui.tatercart.configure.enabled").formatted(Formatting.GREEN)
                    : new TranslatableText("gui.tatercart.configure.disabled").formatted(Formatting.RED);
        }

        @Override
        public void onTick() {
            this.updateInterface(false);
        }
    }
}
