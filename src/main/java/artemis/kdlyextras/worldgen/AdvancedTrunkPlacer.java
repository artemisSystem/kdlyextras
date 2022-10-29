package artemis.kdlyextras.worldgen;

import artemis.kdlyextras.KdlyExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

abstract public class AdvancedTrunkPlacer extends TrunkPlacer {
	public AdvancedTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
		super(baseHeight, heightRandA, heightRandB);
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(
		LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
		Random random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
		KdlyExtras.LOGGER.error("ERROR: placeTrunk was called on an AdvancedTrunkPlacer. Doing nothing.");
		return List.of();
	}

	public abstract List<FoliagePlacer.FoliageAttachment> placeTrunkWithCustomFoliage(
		LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, BiConsumer<BlockPos, BlockState> foliageSetter,
		Random random, int freeTreeHeight, BlockPos pos, TreeConfiguration config
	);
}
