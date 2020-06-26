package info.sciman.alchemicalbricks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

@Environment(EnvType.CLIENT)
public class AlchemicalBricksModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Register thrown brick entity renderer
        EntityRendererRegistry.INSTANCE.register(AlchemicalBricksMod.THROWN_BRICK, (dispatcher, context) -> new FlyingItemEntityRenderer<ThrownBrickEntity>(dispatcher, context.getItemRenderer()));

    }

}
