package artemis.kdlyextras;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public record RecombiningSlabsEntry(BlockState result, boolean useSlabAxis) {
	private static final Codec<BlockState> BLOCKSTATE_CODEC = Codec.either(BlockState.CODEC, Registry.BLOCK.byNameCodec()).xmap(
		either -> either.map(state -> state, Block::defaultBlockState),
		state -> state.equals(state.getBlock().defaultBlockState()) ? Either.right(state.getBlock()) : Either.left(state)
	);

	public static final Codec<RecombiningSlabsEntry> CODEC = Codec.either(
			RecordCodecBuilder.<RecombiningSlabsEntry>create(instance -> instance.group(
				BLOCKSTATE_CODEC.fieldOf("result").forGetter(rse -> rse.result),
				Codec.BOOL.fieldOf("use_slab_axis").forGetter(rse -> rse.useSlabAxis)
			).apply(instance, RecombiningSlabsEntry::new)),
			BLOCKSTATE_CODEC).xmap(
		either -> either.map(rse -> rse, state -> new RecombiningSlabsEntry(state, false)),
		rse -> rse.useSlabAxis ? Either.left(rse) : Either.right(rse.result)
	);

	public BlockState getNewState(Direction.Axis axis) {
		BlockState state = this.result;
		if (!useSlabAxis) return state;
		if (state.hasProperty(BlockStateProperties.AXIS)) {
			state = state.setValue(BlockStateProperties.AXIS, axis);
		}
		if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS) && axis.isHorizontal()) {
			state = state.setValue(BlockStateProperties.HORIZONTAL_AXIS, axis);
		}
		return state;
	}
}
