package artemis.kdlyextras.worldgen.placement_modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class DensityFunctionThresholdPlacement extends DensityFunctionPlacementModifier {
	public FloatProvider threshold;

	public DensityFunctionThresholdPlacement(DensityFunction densityFunction, FloatProvider threshold) {
		super(densityFunction);
		this.threshold = threshold;
	}

	public static Codec<DensityFunctionThresholdPlacement> CODEC = RecordCodecBuilder.create(
		instance -> densityFunctionField(instance)
			.and(FloatProvider.codec(Float.MIN_VALUE, Float.MAX_VALUE).fieldOf("threshold").forGetter(dftp -> dftp.threshold))
			.apply(instance, DensityFunctionThresholdPlacement::new)
	);

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos, double densityFunctionResult) {
		return Stream.of(pos).filter(_pos -> densityFunctionResult > threshold.sample(random));
	}

	@Override
	public PlacementModifierType<?> type() {
		return KdlyExtrasPlacementModifierType.DENSITY_FUNCTION_THRESHOLD_PLACEMENT;
	}
}
