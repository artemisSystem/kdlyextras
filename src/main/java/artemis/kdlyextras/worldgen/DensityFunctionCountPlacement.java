package artemis.kdlyextras.worldgen;

import artemis.kdlyextras.KdlyExtras;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class DensityFunctionCountPlacement extends RepeatingPlacement {
	private final DensityFunction densityFunction;

	private DensityFunctionCountPlacement(DensityFunction densityFunction) {
		this.densityFunction = densityFunction;
	}

	public static final Codec<DensityFunctionCountPlacement> CODEC = DensityFunction.HOLDER_HELPER_CODEC
		.fieldOf("density_function")
		.xmap(DensityFunctionCountPlacement::new, dfCountPlacement -> dfCountPlacement.densityFunction)
		.codec();

	@Override
	protected int count(RandomSource random, BlockPos pos) {
		DensityFunction.FunctionContext context = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
		double result = densityFunction.compute(context);
		KdlyExtras.LOGGER.info("Function called! {}, {}", result, pos);
		return Mth.floor(result);
	}

	@Override
	public PlacementModifierType<?> type() {
		return KdlyExtrasPlacementModifierType.DENSITY_FUNCTION_COUNT_PLACEMENT;
	}
}
