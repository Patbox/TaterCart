package eu.pb4.tatercart.entity.minecart.storage;

import eu.pb4.tatercart.entity.minecart.CustomMinecartEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public abstract class CustomStorageMinecartEntity extends CustomMinecartEntity implements Inventory, NamedScreenHandlerFactory {
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;

    protected CustomStorageMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    protected abstract DefaultedList<ItemStack> getItems();

    @Override
    protected void applySlowdown() {
        float f = 0.98F;
        if (this.lootTableId == null) {
            int i = 15 - ScreenHandler.calculateComparatorOutput(this);
            f += (float) i * 0.001F;
        }

        if (this.isTouchingWater()) {
            f *= 0.95F;
        }

        this.setVelocity(this.getVelocity().multiply(f, 0.0D, f));
    }

    public void generateLoot(@Nullable PlayerEntity player) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            LootTable lootTable = this.world.getServer().getLootManager().getTable(this.lootTableId);
            if (player instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity) player, this.lootTableId);
            }

            this.lootTableId = null;
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).parameter(LootContextParameters.ORIGIN, this.getPos()).random(this.lootSeed);
            if (player != null) {
                builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
            }

            lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }

    }

    @Override
    public void dropItems(DamageSource damageSource) {
        this.generateLoot(damageSource.getSource() instanceof PlayerEntity player ? player : null);
        super.dropItems(damageSource);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemScatterer.spawn(this.world, this, this);
            if (!this.world.isClient) {
                Entity entity = damageSource.getSource();
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    PiglinBrain.onGuardedBlockInteracted((PlayerEntity) entity, true);
                }
            }
        }
    }

    public void remove(Entity.RemovalReason reason) {
        if (!this.world.isClient && reason.shouldDestroy()) {
            this.generateLoot(null);
            ItemScatterer.spawn(this.world, this, this);
        }

        super.remove(reason);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.lootTableId != null) {
            nbt.putString("LootTable", this.lootTableId.toString());
            if (this.lootSeed != 0L) {
                nbt.putLong("LootTableSeed", this.lootSeed);
            }
        } else {
            Inventories.writeNbt(nbt, this.getItems());
        }

    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.getItems().clear();
        if (nbt.contains("LootTable", 8)) {
            this.lootTableId = new Identifier(nbt.getString("LootTable"));
            this.lootSeed = nbt.getLong("LootTableSeed");
        } else {
            Inventories.readNbt(nbt, this.getItems());
        }

    }

    public ActionResult interact(PlayerEntity player, Hand hand) {
        this.generateLoot(null);
        player.openHandledScreen(this);
        if (!player.world.isClient) {
            this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinBrain.onGuardedBlockInteracted(player, true);
            return ActionResult.CONSUME;
        } else {
            return ActionResult.SUCCESS;
        }
    }

    public boolean isEmpty() {
        Iterator var1 = this.getItems().iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack) var1.next();
        } while (itemStack.isEmpty());

        return false;
    }

    public ItemStack getStack(int slot) {
        this.generateLoot(null);
        return this.getItems().get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        this.generateLoot(null);
        return Inventories.splitStack(this.getItems(), slot, amount);
    }

    public ItemStack removeStack(int slot) {
        this.generateLoot(null);
        ItemStack itemStack = this.getItems().get(slot);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.getItems().set(slot, ItemStack.EMPTY);
            return itemStack;
        }
    }

    public void setStack(int slot, ItemStack stack) {
        this.generateLoot(null);
        this.getItems().set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

    }

    public int size() {
        return this.getItems().size();
    }

    public StackReference getStackReference(int mappedIndex) {
        return mappedIndex >= 0 && mappedIndex < this.size() ? new StackReference() {
            public ItemStack get() {
                return CustomStorageMinecartEntity.this.getStack(mappedIndex);
            }

            public boolean set(ItemStack stack) {
                CustomStorageMinecartEntity.this.setStack(mappedIndex, stack);
                return true;
            }
        } : super.getStackReference(mappedIndex);
    }

    public void markDirty() {
    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.isRemoved()) {
            return false;
        } else {
            return !(player.squaredDistanceTo(this) > 64.0D);
        }
    }

    @Override
    public void clear() {
        this.generateLoot(null);
        this.getItems().clear();
    }

    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.lootTableId != null && playerEntity.isSpectator()) {
            return null;
        } else {
            this.generateLoot(playerInventory.player);
            return this.getScreenHandler(i, playerInventory);
        }
    }

    protected abstract ScreenHandler getScreenHandler(int i, PlayerInventory playerInventory);

    public void setLootTable(Identifier id, long lootSeed) {
        this.lootTableId = id;
        this.lootSeed = lootSeed;
    }
}

