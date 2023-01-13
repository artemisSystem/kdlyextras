package artemis.kdlyextras.mixin;

import artemis.kdlyextras.KdlyExtras;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Inject(method = "getEnchantmentTags", at = @At("RETURN"), cancellable = true)
	public void kdlyextras_innateSilkTouch(CallbackInfoReturnable<ListTag> cir) {
		if (((ItemStack)(Object) this).is(KdlyExtras.Tags.INNATE_SILK_TOUCH)) {
			ListTag list = cir.getReturnValue();
			list.add(EnchantmentHelper.storeEnchantment(new ResourceLocation("silk_touch"), 1));
			cir.setReturnValue(list);
		}
	}
}
