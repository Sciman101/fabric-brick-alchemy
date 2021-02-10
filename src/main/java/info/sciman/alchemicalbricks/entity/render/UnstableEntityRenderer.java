package info.sciman.alchemicalbricks.entity.render;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.entity.UnstableEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class UnstableEntityRenderer extends MobEntityRenderer<UnstableEntity,UnstableEntityModel> {

    public UnstableEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new UnstableEntityModel(), 0.5f);
    }

    @Override
    public Identifier getTexture(UnstableEntity entity) {
        return AlchemicalBricksMod.id("textures/entity/unstable_entity/unstable_entity.png");
    }
}
