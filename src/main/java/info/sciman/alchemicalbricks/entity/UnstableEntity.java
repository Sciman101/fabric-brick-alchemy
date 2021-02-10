package info.sciman.alchemicalbricks.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class UnstableEntity extends HostileEntity {

    // Default constructor
    public UnstableEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 20;
    }

    @Override
    protected void initGoals() {
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0])); // Hit things back
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true)); // Target everything
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, 1.0D));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D, 0.2F));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
    }

    public static DefaultAttributeContainer.Builder createUnstableEntityAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D).
                add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5).
                add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D).
                add(EntityAttributes.GENERIC_MAX_HEALTH,30);
    }

    public void tickMovement() {
        // Float
        if (!this.onGround && this.getVelocity().y < 0.0D) {
            this.setVelocity(this.getVelocity().multiply(1.0D, 0.75D, 1.0D));
        }

        if (this.world.isClient) {
            for(int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.FLAME, this.getParticleX(0.5D), this.getRandomBodyY(), this.getParticleZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.tickMovement();
    }

    // Fly
    protected void mobTick() {
        LivingEntity livingEntity = this.getTarget();
        if (livingEntity != null && livingEntity.getEyeY() > this.getEyeY() && this.canTarget(livingEntity)) {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(this.getVelocity().add(0.0D, (0.30 - vec3d.y) * 0.3, 0.0D));
            this.velocityDirty = true;
        }

        super.mobTick();
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        return false;
    }
}
