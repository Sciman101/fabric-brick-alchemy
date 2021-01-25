package info.sciman.alchemicalbricks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;

public class AlchemicalBricksMod implements ModInitializer {

	// Define alchemical brick item
	public static final Item ALCHEMICAL_BRICK_ITEM = new Item(new Item.Settings().group(ItemGroup.MATERIALS));
	public static final Block ALCHEMICAL_BRICK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).hardness(2.0f));

	public static final EntityType<ThrownBrickEntity> THROWN_BRICK = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier("alchemicalbricks","thrown_brick"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, ThrownBrickEntity::new).dimensions(EntityDimensions.fixed(0.25f,0.25f)).build()
	);

	@Override
	public void onInitialize() {
		// Register alchemical brick item
		Registry.register(Registry.ITEM, new Identifier("alchemicalbricks","alchemical_brick"), ALCHEMICAL_BRICK_ITEM);

		// Register alchemical brick block and block item
		Registry.register(Registry.BLOCK, new Identifier("alchemicalbricks","alchemical_brick_block"), ALCHEMICAL_BRICK_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("alchemicalbricks","alchemical_brick_block"), new BlockItem(ALCHEMICAL_BRICK_BLOCK,new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));

		AlchemyRecipes.init();

		// Register right click listener
		UseItemCallback.EVENT.register((player, world, hand) -> {

			if (!player.isSpectator()) {

				// Are we holding a brick?
				ItemStack heldStack = player.getStackInHand(hand);
				if (heldStack.getItem().equals(Items.BRICK) ||
					heldStack.getItem().equals(Items.NETHER_BRICK) ||
					heldStack.getItem().equals(ALCHEMICAL_BRICK_ITEM)) {

					// Sound effect
					world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / 1.2f);

					// Increment used stat and decrement stack size
					player.incrementStat(Stats.USED.getOrCreateStat(heldStack.getItem()));
					if (!player.abilities.creativeMode) {
						heldStack.decrement(1);
					}

					// Spawn entity
					if (!world.isClient) {
						ThrownBrickEntity brickEntity = new ThrownBrickEntity(THROWN_BRICK,world);
						brickEntity.setOwner(player);
						brickEntity.setItem(new ItemStack(heldStack.getItem()));
						brickEntity.setPos(player.getX(),player.getEyeY(),player.getZ());
						brickEntity.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
						world.spawnEntity(brickEntity);
					}

					return TypedActionResult.success(heldStack, world.isClient());
				}
			}

			return TypedActionResult.pass(ItemStack.EMPTY);
		});
	}
}
