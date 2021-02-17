package info.sciman.alchemicalbricks.util;

import info.sciman.alchemicalbricks.recipe.TransmutationRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public class AlchemyHelper {

    /**
     * Release entropy into the world
     * @param world
     * @param pos
     * @param amount
     */
    public static void onEntropyReleased(World world, BlockPos pos, int amount) {

        System.out.println(amount + " entropy released into the environment");

        Vec3d pos2 = new Vec3d(pos.getX(),pos.getY(),pos.getZ()).add(0.5,0.5,0.5);

        // 10 or less has no effect
        if (amount < 10) {
            // Make some particles
            if (world.isClient) {
                for (int i=0;i<amount;i++) {
                    world.addParticle(ParticleTypes.POOF, pos2.x, pos2.y, pos2.z, 0, 0, 0);
                }
            }

        }else if (amount < 25) {
            // Silverfish, fuck you
            if (world instanceof ServerWorld) {
                for (int i=0;i<amount;i++) {
                    SilverfishEntity silverfishEntity = (SilverfishEntity) EntityType.SILVERFISH.create(world);
                    silverfishEntity.refreshPositionAndAngles(pos2.x, pos2.y,pos2.z, 0.0F, 0.0F);
                    world.spawnEntity(silverfishEntity);
                    silverfishEntity.playSpawnEffects();
                }
            }

        }else if (amount < 50) {
            if (world instanceof ServerWorld) {
                // Lightning? sure. Randomize the position so it's really bad
                pos2.add((world.random.nextDouble()-0.5)*16,
                    (world.random.nextDouble()-0.5)*16,
                    (world.random.nextDouble()-0.5)*16);

                LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.create(world);
                lightningEntity.refreshPositionAfterTeleport(pos2);
                world.spawnEntity(lightningEntity);
            }
        }else if (amount < 100) {



        }else{

        }

    }

    /**
     * Do a transmutation in the world on a block
     * @param world
     * @param pos
     * @return Whether or not the conversion was a success
     */
    public static boolean performWorldTransmutation(World world, BlockPos pos) {

        // Try and perform transmutation
        SimpleInventory inventory = new SimpleInventory(new ItemStack(world.getBlockState(pos).getBlock().asItem()));

        Optional<TransmutationRecipe> match = world.getRecipeManager().getFirstMatch(TransmutationRecipe.Type.INSTANCE,inventory,world);

        // Make sure a result exists
        if (match.isPresent()) {

            TransmutationRecipe recipe = match.get();
            if (recipe.getContext() == TransmutationRecipe.AlchemyContext.BRICK ||
                recipe.getContext() == TransmutationRecipe.AlchemyContext.ANY) {

                // Get output item
                Item resultItem = match.get().getOutput().getItem();

                // Perform transmutation
                if (resultItem instanceof BlockItem) {
                    // Set block
                    Block block1 = ((BlockItem) resultItem).getBlock();
                    world.setBlockState(pos, block1.getDefaultState());

                } else {
                    // Drop item
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(resultItem));
                    world.spawnEntity(itemEntity);
                    // Remove the old block
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }

                // Play SFX and particle FX
                if (world.isClient()) {
                    // Particles
                    createTransmutationParticles(pos, world, ParticleTypes.FLAME);
                } else {
                    world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
                }
                return true;
            }else {
                return false;
            }

        }else{
            return false;
        }
    }

    public static void createTransmutationParticles(BlockPos pos, World world, ParticleType type) {
        // Particles
        Random rand = world.random;
        double e,f,d;

        for (int i=0;i<12;i++) {
            d = (double) pos.getX() + rand.nextDouble();
            e = (double) pos.getY() + rand.nextDouble();
            f = (double) pos.getZ() + rand.nextDouble();
            world.addParticle((ParticleEffect) type, d, e, f, (rand.nextDouble()-.5)*.1, rand.nextDouble()*.1, (rand.nextDouble()-.5)*.1);
        }
    }

}
