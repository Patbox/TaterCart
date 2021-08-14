package eu.pb4.tatercart;

import eu.pb4.tatercart.block.TcBlocks;
import eu.pb4.tatercart.entity.TcEntities;
import eu.pb4.tatercart.item.TcItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class TaterCartMod implements ModInitializer {
    public static final String ID = "tatercart";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        TcBlocks.register();
        TcItems.register();
        TcEntities.register();

        TcBlocks.registerData();
        TcItems.registerData();

        TcDataPack.create();
    }
}
