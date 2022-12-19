package artemis.kdlyextras;

import artemis.kdlyextras.worldgen.feature.KdlyExtrasFoliagePlacerType;
import artemis.kdlyextras.worldgen.placement_modifier.KdlyExtrasPlacementModifierType;
import artemis.kdlyextras.worldgen.feature.KdlyExtrasTrunkPlacerType;
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
}