package artemis.kdlyextras;

import artemis.kdlyextras.worldgen.feature.KdlyExtrasFoliagePlacerType;
import artemis.kdlyextras.worldgen.placement_modifier.KdlyExtrasPlacementModifierType;
import artemis.kdlyextras.worldgen.feature.KdlyExtrasTrunkPlacerType;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KdlyExtras implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("KdlyExtras");
	public static final String MOD_ID = "kdlyextras";
	public static final KdlyExtrasResourcePack RESOURCE_PACK = new KdlyExtrasResourcePack("paletted_assets",
		new PackMetadataSection(
			Component.literal("Textures generated from palette templates"),
			PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion())
		)
	);


	@Override
	public void onInitialize(ModContainer mod) {
		KdlyExtrasTrunkPlacerType.init();
		KdlyExtrasFoliagePlacerType.init();
		KdlyExtrasPlacementModifierType.init();

		RESOURCE_PACK.registerPack();
	}
}