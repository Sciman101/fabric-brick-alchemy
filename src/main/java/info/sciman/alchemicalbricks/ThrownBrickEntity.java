package info.sciman.alchemicalbricks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ThrownBrickEntity extends ThrownItemEntity {

    public ThrownBrickEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BRICK;
    }

    @Environment(EnvType.CLIENT)
    private ParticleEffect getParticleParameters() {
        return (ParticleEffect)(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getItem()));
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    // Break glass
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        BlockState block = this.world.getBlockState(blockHitResult.getBlockPos());
        BlockPos pos = blockHitResult.getBlockPos();

        // Is the block glass?
        if (block.getBlock().isIn(BlockTags.IMPERMEABLE)) {
            this.world.breakBlock(pos,true);
        }else{

            // Are we a nether brick item, and is this a sand block?
            if (this.getItem().getItem() == Items.NETHER_BRICK && block.getBlock().isIn(BlockTags.SAND)) {
                // Convert to glass
                this.world.setBlockState(pos,Blocks.GLASS.getDefaultState());
                // Play SFX and particle FX
                if (!world.isClient()) {
                    world.playSound(null,pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS,1f,1f);
                }else{
                    // Particles
                    AlchemyHelper.createTransmutationParticles(pos,world,ParticleTypes.SMOKE);
                }
            }

            // Are we an alchemical brick item?
            else if (this.getItem().getItem() == AlchemicalBricksMod.ALCHEMICAL_BRICK_ITEM) {
                // Do a transmutation in the world
                AlchemyHelper.performWorldTransmutation(world,pos);
            }

            // Destroy
            this.world.sendEntityStatus(this, (byte)3);
            this.remove();
        }
    }

}
