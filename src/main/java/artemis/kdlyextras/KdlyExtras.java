package artemis.kdlyextras;

import artemis.kdlyextras.worldgen.feature.KdlyExtrasFoliagePlacerType;
import artemis.kdlyextras.worldgen.placement_modifier.KdlyExtrasPlacementModifierType;
import artemis.kdlyextras.worldgen.feature.KdlyExtrasTrunkPlacerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;
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
		// REAs need to be initialized and picked up for quilt to read them (probably to know what codec to use)
		REAs.init();
	}

	public static class Tags {
		// Tools in this tag will act as if they have the silk touch enchantment
		public static final TagKey<Item> INNATE_SILK_TOUCH =
			TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MOD_ID, "innate_silk_touch"));
	}

	public static class REAs {
		public static final RegistryEntryAttachment<Block, RecombiningSlabsEntry> RECOMBINING_SLABS = RegistryEntryAttachment
			.builder(Registry.BLOCK,
				new ResourceLocation(MOD_ID, "recombining_slabs"),
				RecombiningSlabsEntry.class,
				RecombiningSlabsEntry.CODEC)
			.build();

		public static void init() {}
	}
}