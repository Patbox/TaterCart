package eu.pb4.tatercart.entity.minecart;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.DyeColor;

public class CustomMinecartType {
    static {
        AbstractMinecartEntity.Type.values();
    }

    public static AbstractMinecartEntity.Type SLIME;
    public static AbstractMinecartEntity.Type BARREL;
    public static AbstractMinecartEntity.Type SHULKER;
    public static BiMap<DyeColor, AbstractMinecartEntity.Type> COLORED = HashBiMap.create();
}
