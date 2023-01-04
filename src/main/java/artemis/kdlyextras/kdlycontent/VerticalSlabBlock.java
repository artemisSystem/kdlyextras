package artemis.kdlyextras.kdlycontent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock {
	public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public VerticalSlabBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
			.setValue(TYPE, SlabType.BOTTOM)
			.setValue(AXIS, Direction.Axis.X)
			.setValue(WATERLOGGED, false)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(TYPE, AXIS, WATERLOGGED);
	}

	public static BlockState setStateToDirection(Direction dir, BlockState state) {
		boolean isDouble = state.getValue(TYPE) == SlabType.DOUBLE;
		return switch (dir) {
			case NORTH -> state.setValue(AXIS, Direction.Axis.Z).setValue(TYPE, isDouble ? SlabType.DOUBLE : SlabType.BOTTOM);
			case SOUTH -> state.setValue(AXIS, Direction.Axis.Z).setValue(TYPE, isDouble ? SlabType.DOUBLE : SlabType.TOP);
			case WEST -> state.setValue(AXIS, Direction.Axis.X).setValue(TYPE, isDouble ? SlabType.DOUBLE : SlabType.BOTTOM);
			case EAST -> state.setValue(AXIS, Direction.Axis.X).setValue(TYPE, isDouble ? SlabType.DOUBLE : SlabType.TOP);
			default -> state;
		};
	}

	public static Direction getDirectionFromState(BlockState state) {
		Direction axisDirection = switch (state.getValue(AXIS)) {
			case X -> Direction.WEST;
			case Z -> Direction.NORTH;
			// this shouldn't happen but java doesn't know that :(
			case Y -> Direction.DOWN;
		};
		return state.getValue(TYPE) == SlabType.TOP ? axisDirection.getOpposite() : axisDirection;
	}


	@Override
	public BlockState rotate(@NotNull BlockState state, Rotation rot) {
		return setStateToDirection(rot.rotate(getDirectionFromState(state)), state);
	}

	@Override
	public BlockState mirror(@NotNull BlockState state, Mirror mirror) {
		return setStateToDirection(mirror.mirror(getDirectionFromState(state)), state);
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return state.getValue(TYPE) != SlabType.DOUBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
		if (state.getValue(TYPE) == SlabType.DOUBLE) {
			return Shapes.block();
		}
		return switch (getDirectionFromState(state)) {
			case NORTH -> NORTH_AABB;
			case SOUTH -> SOUTH_AABB;
			case WEST -> WEST_AABB;
			case EAST -> EAST_AABB;
			// impossible
			case DOWN, UP -> Shapes.block();
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		BlockPos clickedPos = blockPlaceContext.getClickedPos();
		BlockState clickedPosState = blockPlaceContext.getLevel().getBlockState(clickedPos);
		if (clickedPosState.is(this)) {
			return clickedPosState.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false);
		} else {
			FluidState fluidState = blockPlaceContext.getLevel().getFluidState(clickedPos);
			BlockState state = this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
			Direction direction = blockPlaceContext.getHorizontalDirection();
			return setStateToDirection(direction, state);
		}
	}

	// Part of SlabBlock's canBeReplaced but on x/z axes instead of y. false -> x, true -> z
	private boolean slabReplacementLogic(BlockState state, BlockPlaceContext useContext, boolean xOrZ) {
		SlabType slabType = state.getValue(TYPE);
		double clickLocation = xOrZ ? useContext.getClickLocation().z : useContext.getClickLocation().x;
		double clickedPos = xOrZ ? useContext.getClickedPos().getZ() : useContext.getClickedPos().getX();
		boolean clickedUpper = clickLocation - clickedPos > 0.5;
		Direction direction = useContext.getClickedFace();
		Predicate<Direction.Axis> isDifferentAxis = axis -> xOrZ ? (axis != Direction.Axis.Z) : (axis != Direction.Axis.X);
		if (slabType == SlabType.BOTTOM) {
			return direction == (xOrZ ? Direction.SOUTH : Direction.EAST)
				|| clickedUpper && isDifferentAxis.test(direction.getAxis());
		} else {
			return direction == (xOrZ ? Direction.NORTH : Direction.WEST)
				|| !clickedUpper && isDifferentAxis.test(direction.getAxis());
		}
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		ItemStack itemStack = useContext.getItemInHand();
		if (state.getValue(TYPE) == SlabType.DOUBLE || !itemStack.is(this.asItem())) {
			return false;
		} else if (useContext.replacingClickedOnBlock()) {
			return switch (state.getValue(AXIS)) {
				case X -> slabReplacementLogic(state, useContext, false);
				case Z -> slabReplacementLogic(state, useContext, true);
				// Unreachable
				case Y -> false;
			};
		} else {
			return true;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return state.getValue(TYPE) != SlabType.DOUBLE && SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getValue(TYPE) != SlabType.DOUBLE && SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
	}

	@Override
	public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
		return pathComputationType == PathComputationType.WATER && blockGetter.getFluidState(blockPos).is(FluidTags.WATER);
	}
}
