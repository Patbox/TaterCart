package eu.pb4.tatercart.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import eu.pb4.tatercart.TcDataPack;
import eu.pb4.tatercart.entity.minecart.BarrelMinecartEntity;
import eu.pb4.tatercart.entity.minecart.ColoredMinecartEntity;
import eu.pb4.tatercart.entity.minecart.SlimeMinecartEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import static eu.pb4.tatercart.TaterCartMod.id;

public final class TcEntities {
    public static final EntityType<SlimeMinecartEntity> SLIME_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, SlimeMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final EntityType<BarrelMinecartEntity> BARREL_MINECART = FabricEntityTypeBuilder.create(SpawnGroup.MISC, BarrelMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).trackRangeChunks(8).build();
    public static final BiMap<DyeColor, EntityType<ColoredMinecartEntity>> COLORED_MINECART = HashBiMap.create();

    public static void register() {
        register("slime_minecart", SLIME_MINECART);
        register("barrel_minecart", BARREL_MINECART);

        for (var dyeColor : DyeColor.values()) {
            COLORED_MINECART.put(
                    dyeColor,
                    register(dyeColor.getName() + "_colored_minecart", FabricEntityTypeBuilder.create(SpawnGroup.MISC, ColoredMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).build())
            );
        }
    }

    public static void registerData(EntityType<?> entityType, ItemStack... stacks) {

    }

    private static <T extends Entity> EntityType<T> register(String path, EntityType<T> entity) {
        Registry.register(Registry.ENTITY_TYPE, id(path), entity);
        return entity;
    }
}
