package artemis.kdlyextras.worldgen;

import artemis.kdlyextras.KdlyExtras;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import java.util.Random;
import java.util.function.BiConsumer;

public class BirchFoliagePlacer extends AdvancedFoliagePlacer {

	private final IntProvider trunkHeight;
	private final FloatProvider widestPartFrom;
	private final FloatProvider widestPartTo;

	public BirchFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight, FloatProvider widestPartFrom, FloatProvider widestPartTo) {
		super(radius, offset);
		// Distance from the ground to the first layer of leaves
		this.trunkHeight = trunkHeight;
		// Value between 0 and 1 describing how far along the tree the widest section begins (from the bottom).
		// Outside of this area, leaves will gradually move in towards the trunk.
		this.widestPartFrom = widestPartFrom;
		// Value between 0 and 1 describing how far along the tree the widest section ends (from the bottom).
		// Outside of this area, leaves will gradually move in towards the trunk.
		this.widestPartTo = widestPartTo;
	}

	public static final Codec<BirchFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> foliagePlacerParts(instance)
				.and(IntProvider.codec(0, Integer.MAX_VALUE).fieldOf("trunk_height").forGetter(birchFoliagePlacer -> birchFoliagePlacer.trunkHeight))
				// The actual intended max value for these is 1, but apparently that means you have to set the max to 2
				.and(FloatProvider.codec(0.0F, 2.0F).fieldOf("widest_part_from").forGetter(birchFoliagePlacer -> birchFoliagePlacer.widestPartFrom))
				.and(FloatProvider.codec(0.0F, 2.0F).fieldOf("widest_part_to").forGetter(birchFoliagePlacer -> birchFoliagePlacer.widestPartTo))
				.apply(instance, BirchFoliagePlacer::new)
	);

	@Override
	protected FoliagePlacerType<?> type() {
		return KdlyExtrasFoliagePlacerType.BIRCH_FOLIAGE_PLACER;
	}

	@Override
	protected void createFoliage(
		LevelSimulatedReader level,
		BiConsumer<BlockPos, BlockState> blockSetter,
		Random random,
		TreeConfiguration config,
		int maxFreeTreeHeight,
		FoliageAttachment attachment,
		int foliageHeight,
		int foliageRadius,
		int maxY // also called offset
	) {
		int minY = maxY - (foliageHeight - 1);
		int maxRadius = radius.sample(random);
		int widestPartMinY = yFromFloat(this.widestPartFrom, minY, maxY, random);
		int widestPartMaxY = yFromFloat(this.widestPartTo, minY, maxY, random);

		KdlyExtras.LOGGER.info("minY: {}, maxY: {}, foliageHeight: {}, widestPartFrom: {}, widestPartTo: {}",
			minY, maxY, foliageHeight, widestPartFrom, widestPartTo);

		for (int y = minY; y <= maxY; y++) {

			// Uhh how do i explain this. area is just a general measure of "how big" a plane is, and the furthest out block
			// (radius passed to placeLeavesRow) is always half the amount (rounded up) of the area away from the trunk. So
			// area=1 is a 5-block plus shape, area=2 is a 3x3 square, area=3 is a 13-block diamond, area=4 is a 21-block
			// rounded square and so on. Something like that.
			int maxArea = maxRadius * 2;
			int area;
			if (y > widestPartMaxY) {
				area = Math.round(Mth.clampedMap(y, maxY, widestPartMaxY, 0, maxArea));
				KdlyExtras.LOGGER.info("Top branch, y: {}, area: {}", y, area);
			} else if (y >= widestPartMinY) {
				area = maxArea;
				KdlyExtras.LOGGER.info("Mid branch, y: {}, area: {}", y, area);
			} else {
				area = Math.round(Mth.clampedMap(y, widestPartMinY, minY, maxArea, 1));
				KdlyExtras.LOGGER.info("Lower branch, y: {}, area: {}", y, area);
			}
			int radius =  Mth.ceil((float) area / 2);

			this.placeLeavesRow(
				level, blockSetter, random, config, attachment.pos(), radius, y, attachment.doubleTrunk(),
				(x, z) -> {
					if (radius == 0) { return false; }

					int trunkDistance = x + z;
					int keepDistance = area;
					if (area > 2) {
						keepDistance--;
					}
					if (trunkDistance > keepDistance) {
						return true;
					}
					float removalChance = Mth.clampedMap(trunkDistance, 1, radius * 2, 0, 0.3F);

					return random.nextFloat() < removalChance;
				}
			);
		}
	}

	private int yFromFloat(FloatProvider value, int minY, int maxY, Random random) {
		return Mth.floor(Mth.lerp(value.sample(random), minY, maxY + 1));
	}

	@Override
	public int foliageHeight(Random random, int height, TreeConfiguration config) {
		// So the calculation is:
		// height of log area
		// - trunk height config (bottom unleafed part of tree)
		// + offset config (how much extra height to add after the top of logs)
		// + 1 (with offset=0, the top log should still have one leaf block above it)
		return height - this.trunkHeight.sample(random) + this.offset.sample(random) + 1;
	}

	@Override
	protected boolean shouldSkipLocation(Random random, int localX, int localY, int localZ, int range, boolean doubleTrunk) {
		return false;
	}
}
