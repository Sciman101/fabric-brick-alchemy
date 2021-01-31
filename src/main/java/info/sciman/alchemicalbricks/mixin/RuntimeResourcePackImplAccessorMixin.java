package info.sciman.alchemicalbricks.mixin;

import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RuntimeResourcePackImpl.class)
public interface RuntimeResourcePackImplAccessorMixin {

    @Invoker
    static byte[] invokeSerialize(Object object) {
        throw new AssertionError();
    }

    @Invoker
    static Identifier invokeFix(Identifier identifier, String prefix, String append) {
        throw new AssertionError();
    }

}
