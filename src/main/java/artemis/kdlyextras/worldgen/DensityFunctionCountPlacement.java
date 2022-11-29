package artemis.kdlyextras.worldgen;

import artemis.kdlyextras.KdlyExtras;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DensityFunctionCountPlacement extends PlacementModifier {
	private final DensityFunction densityFunction;

	private DensityFunctionCountPlacement(DensityFunction densityFunction) {
		this.densityFunction = densityFunction;
	}

	public static final Codec<DensityFunctionCountPlacement> CODEC = DensityFunction.HOLDER_HELPER_CODEC
		.fieldOf("density_function")
		.xmap(DensityFunctionCountPlacement::new, dfCountPlacement -> dfCountPlacement.densityFunction)
		.codec();

	protected int count(PlacementContext context, BlockPos pos) {
		ChunkGenerator generator = context.generator();
		NoiseGeneratorSettings generatorSettings = generator instanceof NoiseBasedChunkGenerator noiseBasedGenerator
			? noiseBasedGenerator.generatorSettings().value()
			: NoiseGeneratorSettings.dummy();

		RandomState randomState = RandomState.create(
			generatorSettings,
			context.getLevel().registryAccess().registryOrThrow(Registry.NOISE_REGISTRY),
			context.getLevel().getSeed()
		);
		DensityFunction.FunctionContext fnContext = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());

		double result = densityFunction.mapAll(
			new KdlyExtrasDensityFunctionVisitor(randomState)
		).compute(fnContext);
		KdlyExtras.LOGGER.info("Function called! {}, {}", result, pos);
		return Mth.floor(result);
	}

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		return IntStream.range(0, this.count(context, pos)).mapToObj(i -> pos);
	}

	@Override
	public PlacementModifierType<?> type() {
		return KdlyExtrasPlacementModifierType.DENSITY_FUNCTION_COUNT_PLACEMENT;
	}
}
