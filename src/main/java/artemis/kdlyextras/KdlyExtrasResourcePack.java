package artemis.kdlyextras;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackActivationType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KdlyExtrasResourcePack implements PackResources {

	private final String packID;
	private final PackMetadataSection metadata;

	private final HashMap<ResourceLocation, byte[]> textures;

	public KdlyExtrasResourcePack (String packID, PackMetadataSection metadata) {
		this.packID = KdlyExtras.MOD_ID + ":" + packID;
		this.metadata = metadata;
		this.textures = Maps.newHashMap();
	}

	public void registerPack() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerResourcePackProfileProvider((infoConsumer, infoFactory) -> {
			Pack pack = Pack.create(
				this.packID,
				true,
				() -> this,
				infoFactory,
				Pack.Position.TOP,
				PackSource.BUILT_IN
			);
			infoConsumer.accept(pack);
		});
	}

	private void addBytes(ResourceLocation path, byte[] bytes) {
		textures.put(path, bytes);
	}

	private void addTexture(ResourceLocation path, NativeImage image) throws IOException {
		addBytes(path, image.asByteArray());
		// JsonObject mcmeta = image.serializeMcMeta();
		// if (mcmeta != null) this.addJson(path, mcmeta, ResType.MCMETA);
	}

	@Nullable
	@Override
	public InputStream getRootResource(@Nullable String fileName){
		return null;
	}

	@Override
	public InputStream getResource(@NotNull PackType type, @NotNull ResourceLocation location) {
		if (type != PackType.CLIENT_RESOURCES) {
			return InputStream.nullInputStream();
		}
		if (textures.containsKey(location)) {
			try {
				return new ByteArrayInputStream(textures.get(location));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return InputStream.nullInputStream();
	}

	@Override
	public Collection<ResourceLocation> getResources(@NotNull PackType type, @NotNull String namespace, @NotNull String path, @NotNull Predicate<ResourceLocation> filter) {
		ArrayList<ResourceLocation> locations = new ArrayList<>();

		if (type != PackType.CLIENT_RESOURCES) {
			return locations;
		}

		textures.keySet().stream().filter(rl ->
			rl.getNamespace().equals(namespace) && rl.getPath().startsWith(path) && filter.test(rl)
		).collect(Collectors.toCollection(() -> locations));
		return locations;
	}

	@Override
	public boolean hasResource(@NotNull PackType type, @NotNull ResourceLocation location) {
		if (type != PackType.CLIENT_RESOURCES) {
			return false;
		}
		return textures.containsKey(location);
	}

	@Override
	public Set<String> getNamespaces(@NotNull PackType type) {
		return Set.of(KdlyExtras.MOD_ID);
	}

	@Nullable
	@Override
	public <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> deserializer) {
		return (deserializer == PackMetadataSection.SERIALIZER ? (T) this.metadata : null);
	}

	@Override
	public String getName() {
		return getDisplayName().toString();
	}

	@Override
	public void close() {

	}

	@Override
	public @NotNull Component getDisplayName() {
		return Component.literal("KdlyExtras dynamic assets");
	}

	@Override
	public @NotNull ResourcePackActivationType getActivationType() {
		return ResourcePackActivationType.ALWAYS_ENABLED;
	}
}
