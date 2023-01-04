package artemis.kdlyextras;

import artemis.kdlyextras.kdlycontent.VerticalSlabBlock;
import gay.lemmaeof.kdlycontent.api.BlockGenerator;
import gay.lemmaeof.kdlycontent.api.KdlyRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class KdlyContentInit implements Runnable {
	@Override
	public void run() {
		Generators.init();
	}

	public static class Generators {

		public static final BlockGenerator VERTICAL_SLAB =
			registerBlockGen("vertical_slab", (id, settings, customConfig) -> new VerticalSlabBlock(settings));

		private static BlockGenerator registerBlockGen(String name, BlockGenerator generator) {
			return Registry.register(KdlyRegistries.BLOCK_GENERATORS, new ResourceLocation(KdlyExtras.MOD_ID, name), generator);
		}

		public static void init() {}
	}
}
