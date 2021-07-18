package io.github.nuclearfarts.cbt.compat.dashloader;

import com.mojang.datafixers.util.Pair;
import io.github.nuclearfarts.cbt.config.CTMConfig;
import io.github.nuclearfarts.cbt.model.CBTBakedModel;
import io.github.nuclearfarts.cbt.sprite.SpriteProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.model.DashModel;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@DashObject(CBTBakedModel.class)
public class DashCBTBakedModel implements DashModel {
    @Serialize(order = 0)
    public final int model;
    @Serialize(order = 1)
    public final Map<int[], TriPair> spriteProviders;

    public DashCBTBakedModel(@Deserialize("model") int model,
                             @Deserialize("spriteProviders") Map<int[], TriPair> spriteProviders) {
        this.model = model;
        this.spriteProviders = spriteProviders;
    }

    public DashCBTBakedModel(CBTBakedModel cbtBakedModel, DashRegistry registry) {
        final ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        this.model = registry.createModelPointer(cbtBakedModel.getWrapped());
        final List<Pair<Sprite[], CTMConfig>> pairs = DashloaderData.sprites.get(cbtBakedModel);
        this.spriteProviders = new HashMap<>();
        pairs.forEach(pair -> {
            final Sprite[] sprites = pair.getFirst();
            final int[] spritePointers = new int[sprites.length];
            for (int i = 0; i < sprites.length; i++) {
                spritePointers[i] = registry.createSpritePointer(sprites[i]);
            }
            try {
                final Identifier id = DashloaderData.configLocations.get(pair.getSecond());
                final Resource resource = manager.getResource(id);
                final InputStream inputStream = resource.getInputStream();
                final byte[] bytes = IOUtils.toByteArray(inputStream);
                this.spriteProviders.put(spritePointers, new TriPair(bytes, resource.getResourcePackName(), registry.createIdentifierPointer(id)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CBTBakedModel toUndash(DashRegistry registry) {
        try {
            List<SpriteProvider> spriteProviders = new ArrayList<>();
            final ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
            this.spriteProviders.entrySet().forEach(entry -> {
                final int[] spritePointers = entry.getKey();
                final TriPair info = entry.getValue();
                final Sprite[] sprites = new Sprite[spritePointers.length];
                for (int i = 0; i < spritePointers.length; i++) {
                    sprites[i] = registry.getSprite(spritePointers[i]);
                }
                Identifier configLocation = registry.getIdentifier(info.configLocation);
                try {
                    Properties properties = new Properties();
                    properties.load(new ByteArrayInputStream(info.bytes));
                    final CTMConfig config = CTMConfig.load(properties, configLocation, manager, info.resourcePackName);
                    spriteProviders.add(config.createSpriteProvider(sprites));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            return new CBTBakedModel(registry.getModel(model), spriteProviders.toArray(new SpriteProvider[spriteProviders.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("null");
        return null;
    }

    @Override
    public int getStage() {
        return 3;
    }


    public static class TriPair {
        @Serialize(order = 0)
        public byte[] bytes;
        @Serialize(order = 1)
        public String resourcePackName;
        @Serialize(order = 2)
        public int configLocation;


        public TriPair(@Deserialize("bytes") byte[] bytes,
                       @Deserialize("resourcePackName") String resourcePackName,
                       @Deserialize("configLocation") int configLocation) {
            this.bytes = bytes;
            this.resourcePackName = resourcePackName;
            this.configLocation = configLocation;
        }
    }
}
