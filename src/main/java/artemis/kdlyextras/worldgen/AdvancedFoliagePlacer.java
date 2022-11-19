package artemis.kdlyextras.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

import net.minecraft.util.RandomSource;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Same as FoliagePlacer but has a {@code placeLeavesRow} that takes a {@code shouldSkipLocation} parameter, so you can
 * pass in an unique test function to each {@code placeLeavesRow} call. This also lets you use the parameters that get
 * passed to {@code placeFoliage} in the test function. There is also a {@code placeLeavesRowSigned} that passes signed
 * coordinates to the {@code shouldSkipLocationSigned} function.
 */
public abstract class AdvancedFoliagePlacer extends FoliagePlacer {
	public AdvancedFoliagePlacer(IntProvider radius, IntProvider offset) {
		super(radius, offset);
	}

	protected void placeLeavesRowSigned(
		LevelSimulatedReader level,
		BiConsumer<BlockPos, BlockState> blockSetter,
		RandomSource random,
		TreeConfiguration config,
		BlockPos pos,
		int range,
		int yOffset,
		boolean doubleTrunk,
		BiFunction<Integer, Integer, Boolean> shouldSkipLocationSigned
	) {
		int extraRange = doubleTrunk ? 1 : 0;
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

		for (int x = -range; x <= range + extraRange; ++x) {
			for (int z = -range; z <= range + extraRange; ++z) {
				if (
					!shouldSkipLocationSigned.apply(x, z)
					&& !this.shouldSkipLocationSigned(random, x, yOffset, z, range, doubleTrunk)
				) {
					mutableBlockPos.setWithOffset(pos, x, yOffset, z);
					tryPlaceLeaf(level, blockSetter, random, config, mutableBlockPos);
				}
			}
		}
	}

	protected void placeLeavesRow(
		LevelSimulatedReader level,
		BiConsumer<BlockPos, BlockState> blockSetter,
		RandomSource random,
		TreeConfiguration config,
		BlockPos pos,
		int range,
		int yOffset,
		boolean doubleTrunk,
		BiFunction<Integer, Integer, Boolean> shouldSkipLocation
	) {

		BiFunction<Integer, Integer, Boolean> shouldSkipLocationSigned = (localX, localZ) -> {
			int absX;
			int absZ;
			if (doubleTrunk) {
				absX = Math.min(Math.abs(localX), Math.abs(localX - 1));
				absZ = Math.min(Math.abs(localZ), Math.abs(localZ - 1));
			} else {
				absX = Math.abs(localX);
				absZ = Math.abs(localZ);
			}
			return shouldSkipLocation.apply(absX, absZ);
		};

		placeLeavesRowSigned(level, blockSetter, random, config, pos, range, yOffset, doubleTrunk, shouldSkipLocationSigned);
	}

}
