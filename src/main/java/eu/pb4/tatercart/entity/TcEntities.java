package eu.pb4.tatercart.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import eu.pb4.polymer.api.entity.PolymerEntityUtils;
import eu.pb4.tatercart.entity.minecart.other.ColoredMinecartEntity;
import eu.pb4.tatercart.entity.minecart.other.PocketMinecartEntity;
import eu.pb4.tatercart.entity.minecart.other.SlimeMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.BarrelMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.DispenserMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.DropperMinecartEntity;
import eu.pb4.tatercart.entity.minecart.storage.ShulkerMinecartEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static eu.pb4.tatercart.TaterCart.id;

public final class TcEntities {
    private static final List<EntityType<?>> ENTITIES = new ArrayList<>();
    public static final EntityType<SlimeMinecartEntity> SLIME_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, SlimeMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final EntityType<PocketMinecartEntity> POCKET_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, PocketMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final EntityType<BarrelMinecartEntity> BARREL_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, BarrelMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final EntityType<ShulkerMinecartEntity> SHULKER_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, ShulkerMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final EntityType<DispenserMinecartEntity> DISPENSER_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, DispenserMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final EntityType<DropperMinecartEntity> DROPPER_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, DropperMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final BiMap<DyeColor, EntityType<ColoredMinecartEntity>> COLORED_MINECART = HashBiMap.create();

    public static void register() {
        register("slime_minecart", SLIME_MINECART);
        register("barrel_minecart", BARREL_MINECART);
        register("shulker_minecart", SHULKER_MINECART);
        register("dispenser_minecart", DISPENSER_MINECART);
        register("dropper_minecart", DROPPER_MINECART);
        register("pocket_minecart", POCKET_MINECART);

        for (var dyeColor : DyeColor.values()) {
            COLORED_MINECART.put(
                    dyeColor,
                    register(dyeColor.getName() + "_colored_minecart", FabricEntityTypeBuilder.create(SpawnGroup.MISC, ColoredMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).build())
            );
        }

        PolymerEntityUtils.registerType(ENTITIES.toArray(new EntityType[0]));
    }

    private static <T extends Entity> EntityType<T> register(String path, EntityType<T> entity) {
        Registry.register(Registry.ENTITY_TYPE, id(path), entity);
        ENTITIES.add(entity);
        return entity;
    }
}
