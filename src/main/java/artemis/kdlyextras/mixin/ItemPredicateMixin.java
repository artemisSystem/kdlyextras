package artemis.kdlyextras.mixin;

import artemis.kdlyextras.KdlyExtras;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ItemPredicate.class)
public abstract class ItemPredicateMixin {

	@Redirect(
		method = "matches",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/advancements/critereon/EnchantmentPredicate;containedIn(Ljava/util/Map;)Z",
			ordinal = 0
		)
	)
	public boolean kdlyextras_innateSilkTouch(EnchantmentPredicate enchantmentPredicate, Map<Enchantment, Integer> enchantments, ItemStack stack) {
		// The result of this gets negated, but we want to return true if things match
		if (enchantmentPredicate.containedIn(enchantments)) {
			return true;
		}
		return stack.is(KdlyExtras.Tags.INNATE_SILK_TOUCH);
	}
}
