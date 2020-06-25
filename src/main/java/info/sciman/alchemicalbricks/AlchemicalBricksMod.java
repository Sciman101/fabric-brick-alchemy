package info.sciman.alchemicalbricks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AlchemicalBricksMod implements ModInitializer {

	// Define alchemical brick item
	public static final Item ALCHEMICAL_BRICK_ITEM = new Item(new Item.Settings().group(ItemGroup.MATERIALS));

	@Override
	public void onInitialize() {
		// Register alchemical brick item
		Registry.register(Registry.ITEM, new Identifier("alchemicalbricks","alchemical_brick"), ALCHEMICAL_BRICK_ITEM);
	}
}
