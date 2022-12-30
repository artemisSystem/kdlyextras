package artemis.kdlyextras;

import artemis.kdlyextras.worldgen.feature.KdlyExtrasFoliagePlacerType;
import artemis.kdlyextras.worldgen.placement_modifier.KdlyExtrasPlacementModifierType;
import artemis.kdlyextras.worldgen.feature.KdlyExtrasTrunkPlacerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KdlyExtras implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("KdlyExtras");
	public static final String MOD_ID = "kdlyextras";

	@Override
	public void onInitialize(ModContainer mod) {
		KdlyExtrasTrunkPlacerType.init();
		KdlyExtrasFoliagePlacerType.init();
		KdlyExtrasPlacementModifierType.init();
	}

	public static class Tags {
		public static final TagKey<Item> INNATE_SILK_TOUCH =
			TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MOD_ID, "innate_silk_touch"));
	}
}