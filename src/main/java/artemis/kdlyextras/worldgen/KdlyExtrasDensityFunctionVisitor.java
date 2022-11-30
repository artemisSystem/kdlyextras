package artemis.kdlyextras.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;


// TODO: Document that legacy stuff is not supported
public record KdlyExtrasDensityFunctionVisitor(RandomState randomState) implements DensityFunction.Visitor {

	@Override
	public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder noiseHolder) {
		Holder<NormalNoise.NoiseParameters> holder = noiseHolder.noiseData();

		NormalNoise normalNoise = this.randomState.getOrCreateNoise(holder.unwrapKey().orElseThrow());
		return new DensityFunction.NoiseHolder(holder, normalNoise);
	}

	@Override
	public DensityFunction apply(@NotNull DensityFunction densityFunction) {
		return densityFunction;
	}
}
