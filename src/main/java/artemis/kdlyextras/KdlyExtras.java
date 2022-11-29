package artemis.kdlyextras;

import artemis.kdlyextras.worldgen.KdlyExtrasFoliagePlacerType;
import artemis.kdlyextras.worldgen.KdlyExtrasPlacementModifierType;
import artemis.kdlyextras.worldgen.KdlyExtrasTrunkPlacerType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KdlyExtras implements ModInitializer {
	public static final String MODID = "kdlyextras";
	public static final Logger LOGGER = LoggerFactory.getLogger("KdlyExtras");

	@Override
	public void onInitialize(ModContainer mod) {
		KdlyExtrasTrunkPlacerType.init();
		KdlyExtrasFoliagePlacerType.init();
		KdlyExtrasPlacementModifierType.init();
	}
}