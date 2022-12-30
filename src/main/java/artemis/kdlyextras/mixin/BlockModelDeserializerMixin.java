package artemis.kdlyextras.mixin;

import artemis.kdlyextras.KdlyExtras;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.util.GsonHelper;
import org.apache.commons.compress.utils.ByteUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.Map;

@Mixin(targets = "net.minecraft.client.renderer.block.model.BlockModel$Deserializer")
public abstract class BlockModelDeserializerMixin {

	@Shadow
	private static Either<Material, String> parseTextureLocationOrReference(ResourceLocation location, String name) {
		return null;
	}

	// This mixin is a bit incompatible, sorry. I don't think there's a much better way to do this.
	// At least the function's surface area is pretty small.
	@Inject(method = "getTextureMap", at = @At("HEAD"), cancellable = true)
	private void kdlyextras_getTextureMapWithPalettes(JsonObject json, CallbackInfoReturnable<Map<String, Either<Material, String>>> cir) {
		ResourceLocation atlas = TextureAtlas.LOCATION_BLOCKS;
		Map<String, Either<Material, String>> map = Maps.newHashMap();
		if (json.has("textures")) {
			JsonObject jsonObject = GsonHelper.getAsJsonObject(json, "textures");

			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				JsonElement value = entry.getValue();
				if (value.isJsonObject()) {
					JsonObject obj = value.getAsJsonObject();
					String texture = obj.get("texture").getAsString();
					String palette = obj.get("palette").getAsString();


				} else {
					map.put(entry.getKey(), parseTextureLocationOrReference(atlas, value.getAsString()));
				}
			}
		}

		cir.setReturnValue(map);
	}
}
