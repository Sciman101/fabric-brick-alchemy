package info.sciman.alchemicalbricks.entity;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.block.UnstableBlock;
import info.sciman.alchemicalbricks.util.AlchemyHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThrownBrickEntity extends ThrownItemEntity {

    private static final Tag<Block> BREAKABLE_BLOCKS;
    private static final Tag<EntityType<?>> SKELETONS;

    static {
        SKELETONS = TagRegistry.entityType(new Identifier("minecraft","skeletons"));
        BREAKABLE_BLOCKS = TagRegistry.block(AlchemicalBricksMod.id("brick_breakable"));
    }

    public ThrownBrickEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BRICK;
    }

    @Environment(EnvType.CLIENT)
    private ParticleEffect getParticleParameters() {
        Item item = this.getItem().getItem();
        if (item == Items.BRICK) {
            item = Blocks.BRICKS.asItem();
        }
        return (ParticleEffect)(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(item)));
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


    // Hit mobs
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();

        // Do more damage to skeletons
        boolean isSkeleton = SKELETONS.contains(entityHitResult.getEntity().getType());
        entity.damage((new ProjectileDamageSource("brick", this, getOwner())).setProjectile(),isSkeleton ? 2.0f : 1.0f);

        // Set on fire if we're a nether brick
        if (this.getItem().getItem() == Items.NETHER_BRICK) {
            entity.setOnFireFor(1);
        }
        // Make an entity unstable
        else if (this.getItem().getItem() == AlchemicalBricksMod.UNSTABLE_BRICK) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(AlchemicalBricksMod.INSTABILITY,600,1));
            }
        }else if (this.getItem().getItem() == AlchemicalBricksMod.ALCHEMICAL_BRICK) {
            // Summon lightning
            if (this.world instanceof ServerWorld) {
                LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.create(this.world);
                lightningEntity.refreshPositionAfterTeleport(entityHitResult.getPos());
                this.world.spawnEntity(lightningEntity);
            }
        }

        // Destroy
        this.world.sendEntityStatus(this, (byte)3);
        this.remove();
    }

    // Break glass
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        BlockState block = this.world.getBlockState(blockHitResult.getBlockPos());
        BlockPos pos = blockHitResult.getBlockPos();

        // Is the block glass?
        if (block.getBlock().isIn(BREAKABLE_BLOCKS)) {
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
            else if (this.getItem().getItem() == AlchemicalBricksMod.ALCHEMICAL_BRICK) {
                // Do a transmutation in the world
                AlchemyHelper.performWorldTransmutation(world,pos);
            }

            // Are we an unstable brick item?
            else if (this.getItem().getItem() == AlchemicalBricksMod.UNSTABLE_BRICK) {
                if (UnstableBlock.canSpreadToBlock(block)) {
                    // Set the block to an unstable block
                    this.world.setBlockState(pos, AlchemicalBricksMod.UNSTABLE_BLOCK.getDefaultState());
                }
            }

            // Destroy
            this.world.sendEntityStatus(this, (byte)3);
            this.remove();
        }
    }

}
