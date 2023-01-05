package artemis.kdlyextras.mixin;

import artemis.kdlyextras.KdlyExtras;
import artemis.kdlyextras.RecombiningSlabsEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SlabBlock.class)
public abstract class SlabBlockMixin extends Block implements SimpleWaterloggedBlock {

	public SlabBlockMixin(Properties properties) {
		super(properties);
	}

	@Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
	public void kdlyextras_transformingSlabs(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
		BlockPos clickedPos = context.getClickedPos();
		BlockState clickedPosState = context.getLevel().getBlockState(clickedPos);
		Optional<RecombiningSlabsEntry> recombineEntry = KdlyExtras.REAs.RECOMBINING_SLABS.get(this);
		if (clickedPosState.is(this) && recombineEntry.isPresent()) {
			cir.setReturnValue(recombineEntry.get().getNewState(Direction.Axis.Y));
		}
	}
}
