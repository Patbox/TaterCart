package eu.pb4.tatercart.blockentity;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import eu.pb4.tatercart.block.TcBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static eu.pb4.tatercart.TaterCart.id;

public final class TcBlockEntities {
    private static final List<BlockEntityType<?>> ENTITIES = new ArrayList<>();

    public static BlockEntityType<ConfiguringRailBlockEntity> CONTROLLER_RAIL = FabricBlockEntityTypeBuilder.create(ConfiguringRailBlockEntity::new, TcBlocks.CONFIGURING_RAIL).build();

    public static void register() {
        register("configuring_rail", CONTROLLER_RAIL);

        PolymerBlockUtils.registerBlockEntity(ENTITIES.toArray(new BlockEntityType[0]));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, BlockEntityType<T> entity) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id(path), entity);
        ENTITIES.add(entity);
        return entity;
    }
}
