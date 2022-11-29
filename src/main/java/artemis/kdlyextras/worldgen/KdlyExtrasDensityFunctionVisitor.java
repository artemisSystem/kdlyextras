package artemis.kdlyextras.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// TODO: Document that legacy stuff is not supported
public class KdlyExtrasDensityFunctionVisitor implements DensityFunction.Visitor {

	private final RandomState randomState;

	KdlyExtrasDensityFunctionVisitor(RandomState randomState) {
		this.randomState = randomState;
	}

	@Override
	public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder noiseHolder) {
		Holder<NormalNoise.NoiseParameters> holder = noiseHolder.noiseData();

		NormalNoise normalNoise = this.randomState.getOrCreateNoise(holder.unwrapKey().orElseThrow());
		return new DensityFunction.NoiseHolder(holder, normalNoise);
	}

	@Override
	public DensityFunction apply(DensityFunction densityFunction) {
		return densityFunction;
	}
}
