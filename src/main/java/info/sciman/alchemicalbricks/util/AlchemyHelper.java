package info.sciman.alchemicalbricks.util;

import info.sciman.alchemicalbricks.recipe.TransmutationRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public class AlchemyHelper {

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
