package eu.pb4.tatercart.entity.minecart;

import eu.pb4.polymer.entity.VirtualEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class CustomMinecartEntity extends AbstractMinecartEntity implements VirtualEntity {
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

    protected CustomMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityType<?> getVirtualEntityType() {
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
