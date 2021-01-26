package info.sciman.alchemicalbricks;

import net.minecraft.entity.damage.DamageSource;

public class CustomDamageSource extends DamageSource {
    protected CustomDamageSource(String name) {
        super(name);
    }

    @Override
    public DamageSource setBypassesArmor() {
        return super.setBypassesArmor();
    }
}
