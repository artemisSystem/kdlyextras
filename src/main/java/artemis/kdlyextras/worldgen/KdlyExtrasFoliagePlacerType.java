package artemis.kdlyextras.worldgen;

import artemis.kdlyextras.KdlyExtras;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class KdlyExtrasFoliagePlacerType<P extends FoliagePlacer> extends FoliagePlacerType<P>{
	public static final	KdlyExtrasFoliagePlacerType<BirchFoliagePlacer> BIRCH_FOLIAGE_PLACER = register("birch_foliage_placer", BirchFoliagePlacer.CODEC);

	private KdlyExtrasFoliagePlacerType(Codec<P> codec) {
		super(codec);
	}

	public static void init() {}

	private static <P extends FoliagePlacer> KdlyExtrasFoliagePlacerType<P> register(String name, Codec<P> codec) {
		return Registry.register(Registry.FOLIAGE_PLACER_TYPES, new ResourceLocation(KdlyExtras.MODID, name), new KdlyExtrasFoliagePlacerType<>(codec));
	}

}
