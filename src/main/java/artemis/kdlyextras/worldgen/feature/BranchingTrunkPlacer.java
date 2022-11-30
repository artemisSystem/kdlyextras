package artemis.kdlyextras.worldgen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import net.minecraft.util.RandomSource;
import java.util.function.BiConsumer;

public class BranchingTrunkPlacer extends AdvancedTrunkPlacer {

	private final IntProvider bottomMargin;
	private final IntProvider topMargin;
	private final FloatProvider branchChance;
	private final IntProvider branchLength;
	private final FoliagePlacer secondaryFoliagePlacer;
	private final BlockStateProvider secondaryFoliageProvider;
	private final IntProvider secondaryFoliageOffset;

	public BranchingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider bottomMargin, IntProvider topMargin, FloatProvider branchChance, IntProvider branchLength, FoliagePlacer secondaryFoliagePlacer, BlockStateProvider secondaryFoliageProvider, IntProvider secondaryFoliageOffset) {
		super(baseHeight, heightRandA, heightRandB);

		this.bottomMargin = bottomMargin;
		this.topMargin = topMargin;
		this.branchChance = branchChance;
		this.branchLength = branchLength;
		this.secondaryFoliagePlacer = secondaryFoliagePlacer;
		this.secondaryFoliageProvider = secondaryFoliageProvider;
		this.secondaryFoliageOffset = secondaryFoliageOffset;
	}

	public static final Codec<BranchingTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			// Need to copy these from TrunkPlacer because you can't chain-add properties like this beyond 8.
			// I suppose mojang never needed to do it for beyond 8 and didn't bother adding methods they didn't need
			Codec.intRange(0, 32).fieldOf("base_height").forGetter(trunkPlacer -> trunkPlacer.baseHeight),
			Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter(trunkPlacer -> trunkPlacer.heightRandA),
			Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter(trunkPlacer -> trunkPlacer.heightRandB),

			IntProvider.codec(0, Integer.MAX_VALUE).fieldOf("bottom_margin").forGetter(branchingTrunkPlacer -> branchingTrunkPlacer.bottomMargin),
			IntProvider.codec(0, Integer.MAX_VALUE).fieldOf("top_margin").forGetter(branchingTrunkPlacer -> branchingTrunkPlacer.topMargin),
			// The actual intended max value for this is 1, but apparently that means you have to set the max to 2
			FloatProvider.codec(0.0F, 2.0F).fieldOf("branch_chance").forGetter(branchingTrunkPlacer -> branchingTrunkPlacer.branchChance),
			IntProvider.codec(0, Integer.MAX_VALUE).fieldOf("branch_length").forGetter(branchingTrunkPlacer -> branchingTrunkPlacer.branchLength),
			FoliagePlacer.CODEC.fieldOf("secondary_foliage_placer").forGetter(branchingTrunkPlacer -> branchingTrunkPlacer.secondaryFoliagePlacer),
			BlockStateProvider.CODEC.fieldOf("secondary_foliage_provider").forGetter(treeConfiguration -> treeConfiguration.secondaryFoliageProvider),
			IntProvider.codec(Integer.MIN_VALUE, Integer.MAX_VALUE).fieldOf("secondary_foliage_offset").forGetter(treeConfiguration -> treeConfiguration.secondaryFoliageOffset)
		).apply(instance, BranchingTrunkPlacer::new)
	);

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunkWithCustomFoliage(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, BiConsumer<BlockPos, BlockState> foliageSetter, RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
		List<FoliagePlacer.FoliageAttachment> extraFoliageAttachments = Lists.newArrayList();

		// --- Tree placement ---
		setDirtAt(level, blockSetter, random, pos.below(), config);

		int bottomMargin = this.bottomMargin.sample(random);
		int topMargin = this.topMargin.sample(random);
		float branchChance = this.branchChance.sample(random);
		for(int i = 0; i < freeTreeHeight; ++i) {
			placeLog(level, blockSetter, random, pos.above(i), config);

			if (i >= bottomMargin && i < freeTreeHeight - topMargin) {
				for (Direction direction : Direction.Plane.HORIZONTAL) {
					if (random.nextFloat() < branchChance) {
						Direction.Axis axis = direction.getAxis();
						int branchLength = this.branchLength.sample(random);
						for (int l = 1; l <= branchLength; ++l) {
							placeLog(level, blockSetter, random, pos.above(i).relative(direction, l), config, blockState ->
								blockState.hasProperty(BlockStateProperties.AXIS)
									? blockState.setValue(BlockStateProperties.AXIS, axis)
									: blockState
							);
						}
						extraFoliageAttachments.add(new FoliagePlacer.FoliageAttachment(
							pos.above(i).relative(direction, branchLength + 1 + this.secondaryFoliageOffset.sample(random)),
							0, false
						));
					}
				}
			}
		}

		// --- Custom foliage placement ---
		int treeHeight = getTreeHeight(random);
		int foliageHeight = secondaryFoliagePlacer.foliageHeight(random, treeHeight, config);
		int foliageRadius = secondaryFoliagePlacer.foliageRadius(random, treeHeight - foliageHeight);
		// We set the foliageProvider and foliagePlacer to be our values from this tree config, so that it grabs the correct
		// BlockstateProvider to place the leaves with.
		TreeConfiguration.TreeConfigurationBuilder fakeConfigBuilder = new TreeConfiguration.TreeConfigurationBuilder(
			config.trunkProvider,
			config.trunkPlacer,
			secondaryFoliageProvider,
			secondaryFoliagePlacer,
			config.minimumSize
		).decorators(config.decorators).dirt(config.dirtProvider);
		if (config.ignoreVines) { fakeConfigBuilder.ignoreVines(); }
		if (config.forceDirt) { fakeConfigBuilder.forceDirt(); }
		TreeConfiguration fakeConfig = fakeConfigBuilder.build();

		extraFoliageAttachments.forEach(foliageAttachment -> secondaryFoliagePlacer.createFoliage(
			level, foliageSetter, random, fakeConfig, freeTreeHeight, foliageAttachment,
			foliageHeight,
			foliageRadius
		));

		return ImmutableList.of(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return KdlyExtrasTrunkPlacerType.BRANCHING_TRUNK_PLACER;
	}
}
