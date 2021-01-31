package info.sciman.alchemicalbricks;

import info.sciman.alchemicalbricks.block.AlchemicalWorkbenchBlock;
import info.sciman.alchemicalbricks.block.UnstableBlock;
import info.sciman.alchemicalbricks.block.entity.AlchemicalWorkbenchBlockEntity;
import info.sciman.alchemicalbricks.block.entity.UnstableBlockEntity;
import info.sciman.alchemicalbricks.recipe.TransmutationRecipe;
import info.sciman.alchemicalbricks.recipe.TransmutationRecipeSerializer;
import info.sciman.alchemicalbricks.screen.AlchemicalWorkbenchScreenHandler;
import info.sciman.alchemicalbricks.recipe.AlchemyRecipes;
import info.sciman.alchemicalbricks.util.CustomDamageSource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.Material;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class AlchemicalBricksMod implements ModInitializer {

	/* META */
	public static final String MODID = "alchemicalbricks";

	/* ITEMS */
	public static Item ALCHEMICAL_BRICK;
	/* ITEM GROUP */
	public static final ItemGroup ALCHEMICAL_BRICKS_GROUP = FabricItemGroupBuilder.build(id("group"),() -> new ItemStack(ALCHEMICAL_BRICK));
	/* BACK TO ITEMS */
	public static final Item UNSTABLE_MATTER = new Item(new Item.Settings().group(ALCHEMICAL_BRICKS_GROUP));
	public static final Item UNSTABLE_BRICK = new Item(new Item.Settings().group(ALCHEMICAL_BRICKS_GROUP));
	/* BLOCKS */
	public static final Block ALCHEMICAL_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).hardness(2.0f).luminance(8));
	public static final Block ALCHEMICAL_BRICK_SLAB = new SlabBlock(FabricBlockSettings.of(Material.STONE).hardness(2.0f).luminance(8));
	public static final Block POLISHED_ALCHEMICAL_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).hardness(3.0f).luminance(6));
	public static final Block POLISHED_ALCHEMICAL_BRICK_SLAB = new SlabBlock(FabricBlockSettings.of(Material.STONE).hardness(3.0f).luminance(6));
	public static final Block UNSTABLE_BLOCK = new UnstableBlock(FabricBlockSettings.of(Material.PORTAL).breakInstantly().luminance(15).resistance(1500).dropsNothing());
	public static final Block ALCHEMICAL_WORKBENCH = new AlchemicalWorkbenchBlock(FabricBlockSettings.of(Material.STONE).hardness(1.5f).luminance(8));

	/* STATUS */
	public static final StatusEffect INSTABILITY = new InstabilityStatusEffect();
	public static final DamageSource INSTABILITY_DAMAGE = (new CustomDamageSource("instability")).setBypassesArmor().setUsesMagic();

	/* ENTITIES */
	public static final EntityType<ThrownBrickEntity> THROWN_BRICK = Registry.register(
		Registry.ENTITY_TYPE,
		id("thrown_brick"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, ThrownBrickEntity::new).dimensions(EntityDimensions.fixed(0.25f,0.25f)).build()
	);

	/* BLOCK ENTITIES */
	public static BlockEntityType<UnstableBlockEntity> UNSTABLE_BLOCK_ENTITY;
	public static BlockEntityType<AlchemicalWorkbenchBlockEntity> ALCHEMICAL_WORKBENCH_ENTITY;

	/* SCREEN HANDLERS */
	public static final ScreenHandlerType<AlchemicalWorkbenchScreenHandler> ALCHEMICAL_WORKBENCH_SCREEN_HANDLER;

	// Initialize this here and not with the other items since it gets used as an icon
	static {
		ALCHEMICAL_BRICK = new Item(new Item.Settings().group(ALCHEMICAL_BRICKS_GROUP));

		// Screen types
		ALCHEMICAL_WORKBENCH_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(Registry.BLOCK.getId(ALCHEMICAL_WORKBENCH),AlchemicalWorkbenchScreenHandler::new);
	}

    @Override
	public void onInitialize() {

		// Recipe type and serializer
		Registry.register(Registry.RECIPE_SERIALIZER, TransmutationRecipeSerializer.ID,TransmutationRecipeSerializer.INSTANCE);
		Registry.register(Registry.RECIPE_TYPE,id(TransmutationRecipe.Type.ID),TransmutationRecipe.Type.INSTANCE);

		// Register items
		Registry.register(Registry.ITEM, id("alchemical_brick"), ALCHEMICAL_BRICK);
		Registry.register(Registry.ITEM, id("unstable_matter"), UNSTABLE_MATTER);
		Registry.register(Registry.ITEM, id("unstable_brick"), UNSTABLE_BRICK);

		// Register alchemical brick block and block item
		registerBlockAndItem(id("alchemical_bricks"), ALCHEMICAL_BRICKS, ALCHEMICAL_BRICKS_GROUP);
		registerBlockAndItem(id("alchemical_brick_slab"), ALCHEMICAL_BRICK_SLAB, ALCHEMICAL_BRICKS_GROUP);
		registerBlockAndItem(id("polished_alchemical_bricks"), POLISHED_ALCHEMICAL_BRICKS, ALCHEMICAL_BRICKS_GROUP);
		registerBlockAndItem(id("polished_alchemical_brick_slab"), POLISHED_ALCHEMICAL_BRICK_SLAB, ALCHEMICAL_BRICKS_GROUP);
		registerBlockAndItem(id("unstable_block"), UNSTABLE_BLOCK, ALCHEMICAL_BRICKS_GROUP);
		registerBlockAndItem(id("alchemical_workbench"), ALCHEMICAL_WORKBENCH, ALCHEMICAL_BRICKS_GROUP);

		// Register block entites
		UNSTABLE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,id("unstable_block_entity"),BlockEntityType.Builder.create(UnstableBlockEntity::new, UNSTABLE_BLOCK).build(null));
		ALCHEMICAL_WORKBENCH_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,Registry.BLOCK.getId(ALCHEMICAL_WORKBENCH),BlockEntityType.Builder.create(AlchemicalWorkbenchBlockEntity::new, ALCHEMICAL_WORKBENCH).build(null));

		// Status effects
		Registry.register(Registry.STATUS_EFFECT,id("instability"),INSTABILITY);

		// TEMP
		AlchemyRecipes.init();

		// Register use item listener
		UseItemCallback.EVENT.register((player, world, hand) -> {

			if (!player.isSpectator()) {

				// Are we holding a brick?
				ItemStack heldStack = player.getStackInHand(hand);
				if (heldStack.getItem().equals(Items.BRICK) ||
					heldStack.getItem().equals(Items.NETHER_BRICK) ||
					heldStack.getItem().equals(ALCHEMICAL_BRICK) ||
					heldStack.getItem().equals(UNSTABLE_BRICK)) {

					// Sound effect
					world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / 1.2f);

					// Spawn entity
					if (!world.isClient) {
						ThrownBrickEntity brickEntity = new ThrownBrickEntity(THROWN_BRICK,world);
						brickEntity.setOwner(player);
						brickEntity.setItem(new ItemStack(heldStack.getItem()));
						brickEntity.setPos(player.getX(),player.getEyeY(),player.getZ());
						brickEntity.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
						world.spawnEntity(brickEntity);
					}

					// Increment used stat and decrement stack size
					player.incrementStat(Stats.USED.getOrCreateStat(heldStack.getItem()));
					if (!player.abilities.creativeMode) {
						heldStack.decrement(1);
					}

					return TypedActionResult.success(heldStack, world.isClient());
				}
			}

			return TypedActionResult.pass(ItemStack.EMPTY);
		});

		// Add dispenser behaviour
		DispenserBehavior brickThrowBehaviour = new ProjectileDispenserBehavior()  {
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				ThrownBrickEntity brickEntity = new ThrownBrickEntity(THROWN_BRICK,world);
				brickEntity.setItem(new ItemStack(stack.getItem()));
				brickEntity.setPos(position.getX(),position.getY(),position.getZ());
				return brickEntity;
			}
		};
		DispenserBlock.registerBehavior(Items.BRICK,brickThrowBehaviour);
		DispenserBlock.registerBehavior(Items.NETHER_BRICK,brickThrowBehaviour);
		DispenserBlock.registerBehavior(ALCHEMICAL_BRICK,brickThrowBehaviour);
		DispenserBlock.registerBehavior(UNSTABLE_BRICK,brickThrowBehaviour);
	}

	/**
	 * Helper to register a block and it's item simultaneously
	 * @param id
	 * @param block
	 * @param group
	 */
	private void registerBlockAndItem(Identifier id, Block block, ItemGroup group) {
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block,new Item.Settings().group(group)));
	}

	// Helper to create an identifier
	public static Identifier id(String name) {
		return new Identifier(MODID,name);
	}
}
