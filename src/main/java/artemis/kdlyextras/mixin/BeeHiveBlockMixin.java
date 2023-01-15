package artemis.kdlyextras.mixin;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlock.class)
public abstract class BeeHiveBlockMixin extends BaseEntityBlock {

	protected BeeHiveBlockMixin(Properties properties) {
		super(properties);
	}

	@Redirect(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
	public int kdlyextras_innateSilkTouch(Enchantment enchantment, ItemStack stack) {
		// We've mixined into the item predicate code to respect the innate silk touch tag when checking for silk touch.
		boolean hasSilkTouch = ItemPredicate.Builder.item()
			.hasEnchantment(new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.atLeast(1)))
			.build()
			.matches(stack);
		return hasSilkTouch ? 1 : 0;
	}
}
