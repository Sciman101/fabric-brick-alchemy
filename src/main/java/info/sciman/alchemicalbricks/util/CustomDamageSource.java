package info.sciman.alchemicalbricks.util;

import net.minecraft.entity.damage.DamageSource;

public class CustomDamageSource extends DamageSource {
    public CustomDamageSource(String name) {
        super(name);
    }

    @Override
    public DamageSource setBypassesArmor() {
        return super.setBypassesArmor();
    }
}
