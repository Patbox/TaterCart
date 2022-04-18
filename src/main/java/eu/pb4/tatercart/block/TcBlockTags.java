package eu.pb4.tatercart.block;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TcBlockTags {
    public static final TagKey<Block> CONCRETE = TagKey.of(Registry.BLOCK_KEY, new Identifier("c", "concrete"));
}
