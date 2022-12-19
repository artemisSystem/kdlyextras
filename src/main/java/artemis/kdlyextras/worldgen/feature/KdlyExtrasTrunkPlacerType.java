package artemis.kdlyextras.worldgen.feature;

import artemis.kdlyextras.KdlyExtras;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class KdlyExtrasTrunkPlacerType<P extends TrunkPlacer> extends TrunkPlacerType<P> {
	public static final KdlyExtrasTrunkPlacerType<BranchingTrunkPlacer> BRANCHING_TRUNK_PLACER = register("branching_trunk_placer", BranchingTrunkPlacer.CODEC);

	protected KdlyExtrasTrunkPlacerType(Codec<P> codec) {
		super(codec);
	}

	public static void init() {}

	private static <P extends TrunkPlacer> KdlyExtrasTrunkPlacerType<P> register(String name, Codec<P> codec) {
		return Registry.register(Registry.TRUNK_PLACER_TYPES, new ResourceLocation(KdlyExtras.MOD_ID, name), new KdlyExtrasTrunkPlacerType<>(codec));
	}
}
