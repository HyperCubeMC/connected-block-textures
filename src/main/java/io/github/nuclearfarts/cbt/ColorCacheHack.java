package io.github.nuclearfarts.cbt;

import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.util.math.BlockPos;

public class ColorCacheHack extends BiomeColorCache {
	public ColorCacheHack(ToIntFunction<BlockPos> colorFactory) {
		super(colorFactory);
	}

	public int getBiomeColor(BlockPos pos, IntSupplier colorFactory) {
		return colorFactory.getAsInt();
	}
}
