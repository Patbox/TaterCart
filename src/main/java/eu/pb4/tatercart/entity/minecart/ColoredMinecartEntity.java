package eu.pb4.tatercart.entity.minecart;

import com.mojang.datafixers.util.Pair;
import eu.pb4.holograms.mixin.accessors.EntityAccessor;
import eu.pb4.holograms.mixin.accessors.EntityPassengersSetS2CPacketAccessor;
import eu.pb4.holograms.mixin.accessors.EntityTrackerUpdateS2CPacketAccessor;
import eu.pb4.holograms.mixin.accessors.MobSpawnS2CPacketAccessor;
import eu.pb4.holograms.utils.PacketHelpers;
import eu.pb4.tatercart.entity.Colorable;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.mixin.accessor.AbstractMinecartEntityAccessor;
import eu.pb4.tatercart.mixin.accessor.EntitySetHeadYawS2CPacketAccessor;
import eu.pb4.tatercart.mixin.accessor.PassiveEntityAccessor;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.*;

public class ColoredMinecartEntity extends CustomMinecartEntity implements Colorable {
    private final ArrayList<ServerPlayerEntity> listeners = new ArrayList<>();
    private final int bannerEntityId;

    private final DyeColor color;

    public DyeColor getColor() {
        return this.color;
    }

    private final BannerBlock bannerBlock;

    public BannerBlock getBannerBlock() {
        return this.bannerBlock;
    }

    public ColoredMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);

        color = TcEntities.COLORED_MINECART.inverse().get(entityType);
        bannerBlock = (BannerBlock) Registry.BLOCK.get(new Identifier(color.getName() + "_banner"));

        setCustomBlock(Blocks.SPRUCE_FENCE.getDefaultState());
        bannerEntityId = EntityAccessor.getMaxEntityId().incrementAndGet();
    }

    public static EntitySetHeadYawS2CPacket createSetHeadYaw() {
        try {
            return (EntitySetHeadYawS2CPacket) UnsafeAccess.UNSAFE.allocateInstance(EntitySetHeadYawS2CPacket.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Text getDefaultName() {
        return TcItems.COLORED_MINECART.get(color).getName();
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(bannerBlock);
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);

        listeners.add(player);

        {
            var packet = PacketHelpers.createMobSpawn();
            var accessor = (MobSpawnS2CPacketAccessor) packet;
            accessor.setId(bannerEntityId);
            accessor.setUUID(UUID.randomUUID());
            accessor.setYaw((byte) (interpolatedYaw - 90));
            accessor.setHeadYaw((byte) 0);
            accessor.setPitch((byte) 0);
            accessor.setX(getX());
            accessor.setY(getY());
            accessor.setZ(getZ());
            accessor.setEntityType(Registry.ENTITY_TYPE.getRawId(EntityType.VILLAGER));

            player.networkHandler.sendPacket(packet);
        }

        {
            var packet = PacketHelpers.createEntityTrackerUpdate();
            var accessor = (EntityTrackerUpdateS2CPacketAccessor) packet;

            accessor.setId(bannerEntityId);
            List<DataTracker.Entry<?>> data = new ArrayList<>();
            data.add(new DataTracker.Entry<>(EntityAccessor.getFlags(), (byte) 0x20));
            data.add(new DataTracker.Entry<>(PassiveEntityAccessor.CHILD(), true));
            accessor.setTrackedValues(data);

            player.networkHandler.sendPacket(packet);
        }

        {
            var packet = new EntityEquipmentUpdateS2CPacket(bannerEntityId, List.of(new Pair<>(EquipmentSlot.HEAD, new ItemStack(bannerBlock))));
            player.networkHandler.sendPacket(packet);
        }

        {
            var packet = PacketHelpers.createEntityPassengersSet();
            var accessor = (EntityPassengersSetS2CPacketAccessor) packet;
            accessor.setId(this.getId());
            accessor.setPassengers(new int[]{bannerEntityId});

            player.networkHandler.sendPacket(packet);
        }
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);

        listeners.remove(player);

        player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(bannerEntityId));
    }

    @Override
    public void tick() {
        super.tick();

        var yaw = interpolatedYaw - 90;

        if (!((AbstractMinecartEntityAccessor) this).isYawFlipped()) {
            yaw += 180;
        }

        {
            var packet = createSetHeadYaw();
            var accessor = (EntitySetHeadYawS2CPacketAccessor) packet;

            accessor.setEntity(bannerEntityId);
            accessor.setHeadYaw((byte) MathHelper.floor(yaw * 256.0F / 360.0F));

            for (var listener : listeners) {
                listener.networkHandler.sendPacket(packet);
            }
        }
    }

    @Override
    public Type getMinecartType() {
        return CustomMinecartType.COLORED.get(getColor());
    }
}
