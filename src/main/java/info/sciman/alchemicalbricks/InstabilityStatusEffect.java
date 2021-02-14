package info.sciman.alchemicalbricks;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class InstabilityStatusEffect extends StatusEffect {

    protected InstabilityStatusEffect() {
        super(StatusEffectType.HARMFUL, 0xFA008F);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int k = 40 >> amplifier;
        if (k > 0) {
            return duration % k == 0;
        } else {
            return true;
        }
    }

    // Called every tick to apply effect
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);

        World world = entity.world;

        // Just stolen from the chorus fruit
        if (!world.isClient) {
            double d = entity.getX();
            double e = entity.getY();
            double f = entity.getZ();

            for(int i = 0; i < 16; ++i) {
                double g = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 16.0D * (amplifier+1);
                double h = MathHelper.clamp(entity.getY() + (double)(entity.getRandom().nextInt(16) - 8), 0.0D, (double)(world.getDimensionHeight() - 1));
                double j = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 16.0D * (amplifier+1);
                if (entity.hasVehicle()) {
                    entity.stopRiding();
                }

                //BlockHitResult hit = world.raycast(new RaycastContext(entity.getPos(),new Vec3d(g,h,j), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,entity));

                if (entity.teleport(g, h, j, true)) {
                    world.playSound((PlayerEntity)null, d, e, f, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    entity.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                    break;
                }
            }
        }

        // Damage
        entity.damage(AlchemicalBricksMod.INSTABILITY_DAMAGE, 1.0F);
    }
}
