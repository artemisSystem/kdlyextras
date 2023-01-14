package artemis.kdlyextras;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RecombiningSlabsEntry {
	private static final Codec<BlockState> DEFAULT_BLOCKSTATE_CODEC = Registry.BLOCK.byNameCodec().xmap(
		Block::defaultBlockState,
		BlockState::getBlock
	);
	private static final Codec<BlockState> BLOCKSTATE_CODEC = Codec.either(BlockState.CODEC, DEFAULT_BLOCKSTATE_CODEC).xmap(
		either -> either.map(l -> l, r -> r),
		Either::left
	);

	private static final Codec<RecombiningSlabsEntry> KEEP_AXIS_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BLOCKSTATE_CODEC.fieldOf("result").forGetter(rse -> rse.result),
			Codec.BOOL.fieldOf("keep_axis").forGetter(rse -> rse.keepAxis)
		).apply(instance, RecombiningSlabsEntry::new)
	);

	private static final Codec<RecombiningSlabsEntry> SIMPLE_CODEC = BLOCKSTATE_CODEC.xmap(
		RecombiningSlabsEntry::new,
		rse -> rse.result
	);

	public static final Codec<RecombiningSlabsEntry> CODEC = Codec.either(KEEP_AXIS_CODEC, SIMPLE_CODEC).xmap(
		either -> either.map(l -> l, r -> r),
		Either::left
	);

	private final BlockState result;
	private final boolean keepAxis;

	private RecombiningSlabsEntry(BlockState result, boolean keepAxis) {
		this.result = result;
		this.keepAxis = keepAxis;
	}

	private RecombiningSlabsEntry(BlockState result) {
		this.result = result;
		this.keepAxis = false;
	}

	public BlockState getNewState(Direction.Axis axis) {
		BlockState state = this.result;
		if (state.hasProperty(BlockStateProperties.AXIS)) {
			state = state.setValue(BlockStateProperties.AXIS, axis);
		}
		if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS) && axis.isHorizontal()) {
			state = state.setValue(BlockStateProperties.HORIZONTAL_AXIS, axis);
		}
		return state;
	}
}
