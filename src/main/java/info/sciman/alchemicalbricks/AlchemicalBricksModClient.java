package info.sciman.alchemicalbricks;

import info.sciman.alchemicalbricks.entity.ThrownBrickEntity;
import info.sciman.alchemicalbricks.entity.render.UnstableEntityRenderer;
import info.sciman.alchemicalbricks.screen.AlchemicAltarScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

@Environment(EnvType.CLIENT)
public class AlchemicalBricksModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register thrown brick entity renderer
        EntityRendererRegistry.INSTANCE.register(AlchemicalBricksMod.THROWN_BRICK, (dispatcher, context) -> new FlyingItemEntityRenderer<ThrownBrickEntity>(dispatcher, context.getItemRenderer()));

        // Register workbench screen
        ScreenRegistry.register(AlchemicalBricksMod.ALCHEMICAL_WORKBENCH_SCREEN_HANDLER, AlchemicAltarScreen::new);

        // Register entity model
        EntityRendererRegistry.INSTANCE.register(AlchemicalBricksMod.UNSTABLE_ENTITY, (disp,ctx) -> {
            return new UnstableEntityRenderer(disp);
        });
    }

}
