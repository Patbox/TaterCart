package eu.pb4.tatercart.entity.minecart.other;

import com.mojang.datafixers.util.Pair;
import eu.pb4.holograms.mixin.accessors.EntityAccessor;
import eu.pb4.holograms.mixin.accessors.EntityPassengersSetS2CPacketAccessor;
import eu.pb4.holograms.mixin.accessors.EntityTrackerUpdateS2CPacketAccessor;
import eu.pb4.holograms.mixin.accessors.MobSpawnS2CPacketAccessor;
import eu.pb4.holograms.utils.PacketHelpers;
import eu.pb4.polymer.api.entity.PolymerEntityUtils;
import eu.pb4.tatercart.entity.Colorable;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.entity.minecart.CustomMinecartEntity;
import eu.pb4.tatercart.entity.minecart.CustomMinecartType;
import eu.pb4.tatercart.item.TcItems;
import eu.pb4.tatercart.mixin.ZombieEntityAccessor;
import eu.pb4.tatercart.mixin.accessor.AbstractMinecartEntityAccessor;
import eu.pb4.tatercart.mixin.accessor.EntitySetHeadYawS2CPacketAccessor;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColoredMinecartEntity extends CustomMinecartEntity implements Colorable {
    private final ArrayList<ServerPlayerEntity> listeners = new ArrayList<>();
    private final int bannerEntityId;

    private final DyeColor color;
    private ItemStack bannerItemStack;
    private ItemStack currentClientStack;
    private final BannerItem bannerItem;

    public ColoredMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);

        color = TcEntities.COLORED_MINECART.inverse().get(entityType);
        this.bannerItem = (BannerItem) Registry.ITEM.get(new Identifier(color.getName() + "_banner"));
        this.bannerItemStack = this.bannerItem.getDefaultStack();
        this.currentClientStack = this.bannerItemStack;

        this.setCustomBlock(Registry.BLOCK.get(new Identifier(color.getName() + "_wool")).getDefaultState());
        this.bannerEntityId = PolymerEntityUtils.requestFreeId();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        var handItem = player.getStackInHand(hand);
        if (!handItem.isEmpty() && handItem.getItem() instanceof BannerItem bannerItem && this.bannerItem == bannerItem) {
            this.bannerItemStack = handItem.copy();
            this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.5f, 1);
            return ActionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    public static EntitySetHeadYawS2CPacket createSetHeadYaw() {
        try {
            return (EntitySetHeadYawS2CPacket) UnsafeAccess.UNSAFE.allocateInstance(EntitySetHeadYawS2CPacket.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack getBannerItemStack() {
        return this.bannerItemStack;
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected Text getDefaultName() {
        return TcItems.COLORED_MINECART.get(color).getName();
    }

    @Override
    protected @Nullable Item getDropItem() {
        return this.dropSplit() ? this.bannerItem : TcItems.COLORED_MINECART.get(this.color);
    }

    @Override
    public ItemStack getPickBlockStack() {
        return TcItems.COLORED_MINECART.get(this.color).getDefaultStack();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("BannerItemStack", this.bannerItemStack.writeNbt(new NbtCompound()));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("BannerItemStack")) {
            this.bannerItemStack = ItemStack.fromNbt(nbt.getCompound("BannerItemStack"));

            if (this.bannerItemStack == ItemStack.EMPTY) {
                this.bannerItemStack = this.bannerItem.getDefaultStack();
            }
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);

       this.listeners.add(player);

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
            accessor.setEntityType(Registry.ENTITY_TYPE.getRawId(EntityType.ZOMBIE));

            player.networkHandler.sendPacket(packet);
        }

        {
            var packet = PacketHelpers.createEntityTrackerUpdate();
            var accessor = (EntityTrackerUpdateS2CPacketAccessor) packet;

            accessor.setId(bannerEntityId);
            List<DataTracker.Entry<?>> data = new ArrayList<>();
            data.add(new DataTracker.Entry<>(EntityAccessor.getFlags(), (byte) 0x20));
            data.add(new DataTracker.Entry<>(ZombieEntityAccessor.BABY(), true));
            accessor.setTrackedValues(data);

            player.networkHandler.sendPacket(packet);
        }

        {
            var packet = new EntityEquipmentUpdateS2CPacket(this.bannerEntityId, List.of(new Pair<>(EquipmentSlot.HEAD, this.bannerItemStack)));
            player.networkHandler.sendPacket(packet);
        }

        {
            var packet = PacketHelpers.createEntityPassengersSet();
            var accessor = (EntityPassengersSetS2CPacketAccessor) packet;
            accessor.setId(this.getId());
            accessor.setPassengers(new int[]{this.bannerEntityId});

            player.networkHandler.sendPacket(packet);
        }
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);

        this.listeners.remove(player);

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
        {
            if (!this.currentClientStack.equals(this.bannerItemStack)) {
                this.currentClientStack = this.bannerItemStack;
                var packet = new EntityEquipmentUpdateS2CPacket(this.bannerEntityId, List.of(new Pair<>(EquipmentSlot.HEAD, this.bannerItemStack)));
                for (var listener : listeners) {
                    listener.networkHandler.sendPacket(packet);
                }
            }
        }
    }

    @Override
    public Type getMinecartType() {
        return CustomMinecartType.COLORED.get(getColor());
    }
}
