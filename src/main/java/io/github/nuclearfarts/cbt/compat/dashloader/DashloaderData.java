package io.github.nuclearfarts.cbt.compat.dashloader;

import com.mojang.datafixers.util.Pair;
import io.github.nuclearfarts.cbt.config.CTMConfig;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashloaderData {
    public static Map<BakedModel,List<Pair<Sprite[],CTMConfig>>> sprites = new HashMap<>();
    public static Map<CTMConfig, Identifier> configLocations = new HashMap<>();

}
