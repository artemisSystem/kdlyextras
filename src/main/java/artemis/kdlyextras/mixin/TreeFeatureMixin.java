package artemis.kdlyextras.mixin;

import artemis.kdlyextras.worldgen.AdvancedTrunkPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import net.minecraft.util.RandomSource;
import java.util.function.BiConsumer;

@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin {
	@Redirect(
		method = "doPlace",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/trunkplacers/TrunkPlacer;placeTrunk(Lnet/minecraft/world/level/LevelSimulatedReader;Ljava/util/function/BiConsumer;Ljava/util/Random;ILnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;)Ljava/util/List;")
	)
	List<FoliagePlacer.FoliageAttachment>  doPlaceWithAdvancedTrunkPlacer(TrunkPlacer trunkPlacer, LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config, WorldGenLevel worldGenLevel, RandomSource random2, BlockPos pos2, BiConsumer<BlockPos, BlockState> trunkBlockSetter, BiConsumer<BlockPos, BlockState> foliageBlockSetter, TreeConfiguration config2) {
		if (trunkPlacer instanceof AdvancedTrunkPlacer advancedTrunkPlacer) {
			return advancedTrunkPlacer.placeTrunkWithCustomFoliage(level, blockSetter, foliageBlockSetter, random, freeTreeHeight, pos, config);
		} else {
			return trunkPlacer.placeTrunk(level, blockSetter, random, freeTreeHeight, pos, config);
		}
	}
}
