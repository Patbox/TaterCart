package eu.pb4.tatercart.entity.minecart.storage;

import eu.pb4.tatercart.block.rail.ColoredDetectorRailBlock;
import eu.pb4.tatercart.entity.Colorable;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.item.ShulkerMinecartItem;
import eu.pb4.tatercart.item.TcItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShulkerMinecartEntity extends CustomStorageMinecartEntity implements Colorable {
    private final DefaultedList<ItemStack> inventory;
    private ItemStack shulkerBox = new ItemStack(Items.SHULKER_BOX);

    public ShulkerMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
        this.setVisualBlockState(Blocks.SHULKER_BOX.getDefaultState());
        this.setVisualBlockOffset(6);
    }

    public static ShulkerMinecartEntity create(World world, double x, double y, double z) {
        var entity = new ShulkerMinecartEntity(TcEntities.SHULKER_MINECART, world);
        entity.setPosition(x, y, z);
        return entity;
    }

    public void setShulkerBox(ItemStack stack) {
        this.shulkerBox = stack;

        if (stack.hasNbt() && stack.getNbt().contains("BlockEntityTag", NbtElement.COMPOUND_TYPE)) {
            Inventories.readNbt(stack.getNbt().getCompound("BlockEntityTag"), this.inventory);
        }

        this.setVisualBlockState(((BlockItem) stack.getItem()).getBlock().getDefaultState());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("ShulkerBox", this.shulkerBox.writeNbt(new NbtCompound()));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setShulkerBox(ItemStack.fromNbt(nbt.getCompound("ShukerBox")));
    }

    @Override
    public void markDirty() {
        Inventories.writeNbt(this.shulkerBox.getOrCreateSubNbt("BlockEntityTag"), this.inventory);
    }

    @Override
    protected DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected Text getDefaultName() {
        return new TranslatableText("entity.tatercart.minecart_with_x", this.shulkerBox.getItem().getName());
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        var shulkerBox = this.shulkerBox.copy();
        this.inventory.clear();
        super.dropItems(damageSource);
        this.dropStack(shulkerBox);
    }

    @Override
    protected @Nullable Item getDropItem() {
        return null;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return TcItems.SHULKER_MINECART.from(this.shulkerBox.getItem().getDefaultStack());
    }

    @Override
    protected ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ShulkerBoxScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Type getMinecartType() {
        return CustomMinecartType.SHULKER;
    }

    @Override
    public DyeColor getColor() {
        return ColoredDetectorRailBlock.getColorOf(this.getVisualState());
    }
}
