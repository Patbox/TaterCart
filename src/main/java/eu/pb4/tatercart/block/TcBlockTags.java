package eu.pb4.tatercart.block;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TcBlockTags {
    public static final Tag<Block> CONCRETE = TagRegistry.block(new Identifier("c", "concrete"));
}
