package eu.pb4.tatercart.entity.minecart.base;

import eu.pb4.polymer.api.entity.PolymerEntity;
import eu.pb4.polymer.api.item.PolymerItem;
import eu.pb4.polymer.impl.interfaces.EntityAttachedPacket;
import eu.pb4.tatercart.mixin.accessor.AbstractMinecartEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

public abstract class CustomMinecartEntity extends AbstractMinecartEntity implements PolymerEntity {
    private int clientInterpolationSteps;
    private double clientX;
    private double clientY;
    private double clientZ;
    private double clientYaw;
    private double clientPitch;

    protected double interpolatedX;
    protected double interpolatedY;
    protected double interpolatedZ;
    protected double interpolatedYaw;
    protected double interpolatedPitch;

    private BlockState visualBlock = this.getDefaultContainedBlock();
    private int visualOffset = this.getDefaultBlockOffset();
    private boolean forceTrackerUpdate;

    protected CustomMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    protected void setVisualBlockState(BlockState state) {
        this.visualBlock = state;
        this.markDataTrackerAsDirty();
    }

    protected void setVisualBlockOffset(int offset) {
        this.visualOffset = offset;
        this.markDataTrackerAsDirty();
    }

    protected void markDataTrackerAsDirty() {
        this.forceTrackerUpdate = true;
    }

    @Override
    public void onEntityTrackerTick(Set<EntityTrackingListener> listeners) {
        if (this.forceTrackerUpdate) {
            this.forceTrackerUpdate = false;
            var packet = EntityAttachedPacket.set(new EntityTrackerUpdateS2CPacket(this.getId(), this.dataTracker, true), this);

            for (var l : listeners) {
                l.sendPacket(packet);
            }
        }
    }

    @Override
    public void modifyTrackedData(List<DataTracker.Entry<?>> data) {

        data.removeIf((x) -> x.getData() == AbstractMinecartEntityAccessor.CUSTOM_BLOCK_ID()
                        || x.getData() == AbstractMinecartEntityAccessor.CUSTOM_BLOCK_OFFSET()
                        || x.getData() == AbstractMinecartEntityAccessor.CUSTOM_BLOCK_PRESENT()
        );

        data.add(new DataTracker.Entry<>(AbstractMinecartEntityAccessor.CUSTOM_BLOCK_PRESENT(), true));
        data.add(new DataTracker.Entry<>(AbstractMinecartEntityAccessor.CUSTOM_BLOCK_OFFSET(), this.hasCustomBlock() ? this.getBlockOffset() : this.visualOffset));
        var blockState = this.hasCustomBlock() ? this.getContainedBlock() : this.visualBlock;
        data.add(new DataTracker.Entry<>(AbstractMinecartEntityAccessor.CUSTOM_BLOCK_ID(), Block.getRawIdFromState(blockState)));
    }

    @Override
    public EntityType<?> getPolymerEntityType() {
        return EntityType.MINECART;
    }

    public void setInterpolationSteps(int interpolationSteps) {
        this.clientX = getX();
        this.clientY = getY();
        this.clientZ = getZ();
        this.clientYaw = getYaw();
        this.clientPitch = getPitch();
        this.clientInterpolationSteps = interpolationSteps + 2;
    }

    @Override
    public void tick() {
        if (this.clientInterpolationSteps > 0) {
            interpolatedX = interpolatedX + (this.clientX - interpolatedX) / (double) this.clientInterpolationSteps;
            interpolatedY = interpolatedY + (this.clientY - interpolatedY) / (double) this.clientInterpolationSteps;
            interpolatedZ = interpolatedZ + (this.clientZ - interpolatedZ) / (double) this.clientInterpolationSteps;
            double yaw = MathHelper.wrapDegrees(this.clientYaw - interpolatedYaw);
            interpolatedYaw = interpolatedYaw + (float) yaw / (float) this.clientInterpolationSteps;
            interpolatedPitch = interpolatedPitch + (float) (this.clientPitch - interpolatedPitch) / (float) this.clientInterpolationSteps;
            --this.clientInterpolationSteps;
        } else {
            interpolatedX = this.getX();
            interpolatedY = this.getY();
            interpolatedZ = this.getZ();
            interpolatedYaw = this.getYaw();
            interpolatedPitch = this.getPitch();
        }

        super.tick();
    }
}
