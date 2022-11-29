package artemis.kdlyextras.worldgen;

import artemis.kdlyextras.KdlyExtras;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public interface KdlyExtrasPlacementModifierType<P extends PlacementModifier> extends PlacementModifierType<P> {
	KdlyExtrasPlacementModifierType<DensityFunctionCountPlacement> DENSITY_FUNCTION_COUNT_PLACEMENT = register("density_function_count", DensityFunctionCountPlacement.CODEC);

	static void init() {}

	private static <P extends PlacementModifier> KdlyExtrasPlacementModifierType<P> register(String name, Codec<P> codec) {
		return Registry.register(Registry.PLACEMENT_MODIFIERS, new ResourceLocation(KdlyExtras.MODID, name), () -> codec);
	}
}