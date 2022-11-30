package artemis.kdlyextras.worldgen.placement_modifier;

import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public abstract class DensityFunctionPlacementModifier extends PlacementModifier {
	public static final Codec<PlacementModifier> CODEC = Registry.PLACEMENT_MODIFIERS.byNameCodec().dispatch(PlacementModifier::type, PlacementModifierType::codec);
	protected final DensityFunction densityFunction;

	public DensityFunctionPlacementModifier(DensityFunction densityFunction) {
		this.densityFunction = densityFunction;
	}

	protected static <P extends DensityFunctionPlacementModifier> P1<Mu<P>, DensityFunction> densityFunctionField(Instance<P> instance) {
		return instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("density_function").forGetter(dfpm -> dfpm.densityFunction)
		);
	}

	public abstract Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos, double densityFunctionResult);

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {

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

		double result = densityFunction.mapAll(new KdlyExtrasDensityFunctionVisitor(randomState)).compute(fnContext);

		return getPositions(context, random, pos, result);
	}
}
