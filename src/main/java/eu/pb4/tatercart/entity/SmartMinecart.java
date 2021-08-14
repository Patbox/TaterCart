package eu.pb4.tatercart.entity;

import eu.pb4.polymer.entity.VirtualEntity;
import eu.pb4.tatercart.mixin.accessor.AbstractMinecartEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SmartMinecart extends CowEntity implements VirtualEntity {
    public SmartMinecart(EntityType<? extends CowEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityType<?> getVirtualEntityType() {
        return EntityType.MINECART;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(Items.IRON_INGOT), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.IRON_INGOT);
    }

    @Override
    public SmartMinecart createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return TcEntities.SMART_MINECART.create(serverWorld);
    }

    @Override
    public void modifyTrackedData(List<DataTracker.Entry<?>> data) {
        data.add(new DataTracker.Entry<>(AbstractMinecartEntityAccessor.CUSTOM_BLOCK_PRESENT(), true));
        data.add(new DataTracker.Entry<>(AbstractMinecartEntityAccessor.CUSTOM_BLOCK_ID(), Block.getRawIdFromState(this.isBaby() ? Blocks.CARVED_PUMPKIN.getDefaultState() : Blocks.JACK_O_LANTERN.getDefaultState())));
        data.add(new DataTracker.Entry<>(AbstractMinecartEntityAccessor.CUSTOM_BLOCK_OFFSET(), 15));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_MINECART_RIDING;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_METAL_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_ANVIL_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_MINECART_RIDING, 0.15F, 1.0F);
    }


}
