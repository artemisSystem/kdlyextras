package artemis.kdlyextras.worldgen.placement_modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DensityFunctionCountPlacement extends DensityFunctionPlacementModifier {
	private DensityFunctionCountPlacement(DensityFunction densityFunction) {
		super(densityFunction);
	}

	public static final Codec<DensityFunctionCountPlacement> CODEC = RecordCodecBuilder.create(
		instance -> densityFunctionField(instance).apply(instance, DensityFunctionCountPlacement::new)
	);

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos, double result) {
		return IntStream.range(0, Mth.floor(result)).mapToObj(i -> pos);
	}

	@Override
	public PlacementModifierType<?> type() {
		return KdlyExtrasPlacementModifierType.DENSITY_FUNCTION_COUNT_PLACEMENT;
	}
}
