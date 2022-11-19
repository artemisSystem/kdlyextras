package artemis.kdlyextras.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import net.minecraft.util.RandomSource;
import java.util.function.BiConsumer;

public class EllipsoidFoliagePlacer extends AdvancedFoliagePlacer {

	public EllipsoidFoliagePlacer(IntProvider radius, IntProvider offset) {
		super(radius, offset);
	}

	public static final Codec<EllipsoidFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> foliagePlacerParts(instance).apply(instance, EllipsoidFoliagePlacer::new)
	);

	@Override
	protected FoliagePlacerType<?> type() {
		return KdlyExtrasFoliagePlacerType.ELLIPSOID_FOLIAGE_PLACER;
	}

	@Override
	protected void createFoliage(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
		// Make sure to use the radius that got sampled in foliageHeight and not sample a new one
		int radius = (foliageHeight - 1) / 2;
		BlockPos origin = attachment.pos().above(offset);
		for (int varY = -radius; varY <= radius; ++varY) {
			// Need to make final because it's used in lambda
			final int y = varY;
			this.placeLeavesRow(
				level, blockSetter, random, config, origin, radius, y, attachment.doubleTrunk(),
				(x, z) -> Mth.sqrt(x * x + y * y + z * z) > radius
			);
		}
	}

	@Override
	public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
		return 1 + 2 * this.radius.sample(random);
	}

	@Override
	protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
		return false;
	}
}
