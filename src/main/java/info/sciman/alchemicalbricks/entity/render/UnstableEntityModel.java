// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

package info.sciman.alchemicalbricks.entity.render;

import info.sciman.alchemicalbricks.entity.UnstableEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class UnstableEntityModel extends EntityModel<UnstableEntity> {
    private final ModelPart innerRing;
    private final ModelPart outerRing;
    private final ModelPart head;

    public UnstableEntityModel() {
        textureWidth = 64;
        textureHeight = 32;
        innerRing = new ModelPart(this);
        innerRing.setPivot(0.0F, 30.0F, 0.0F);
        innerRing.setTextureOffset(48, 0).addCuboid(-12.0F, -37.0F, 8.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        innerRing.setTextureOffset(48, 0).addCuboid(-12.0F, -37.0F, -12.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        innerRing.setTextureOffset(48, 0).addCuboid(8.0F, -37.0F, -12.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        innerRing.setTextureOffset(48, 0).addCuboid(8.0F, -37.0F, 8.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        outerRing = new ModelPart(this);
        outerRing.setPivot(0.5F, 34.0F, 0.0F);
        outerRing.setTextureOffset(48, 0).addCuboid(-19.5F, -39.0F, -2.0F, 4.0F, 16.0F, 4.0F, 0.0F, false);
        outerRing.setTextureOffset(48, 0).addCuboid(-2.5F, -39.0F, -19.0F, 4.0F, 16.0F, 4.0F, 0.0F, false);
        outerRing.setTextureOffset(48, 0).addCuboid(-2.5F, -39.0F, 14.0F, 4.0F, 16.0F, 4.0F, 0.0F, false);
        outerRing.setTextureOffset(48, 0).addCuboid(15.5F, -39.0F, -2.0F, 4.0F, 16.0F, 4.0F, 0.0F, false);

        head = new ModelPart(this);
        head.setPivot(0.0F, 31.0F, 0.0F);
        head.setTextureOffset(0, 0).addCuboid(-6.0F, -37.0F, -6.0F, 12.0F, 12.0F, 12.0F, 0.0F, false);
    }

	@Override
	public void setAngles(UnstableEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        innerRing.yaw = (float) (animationProgress * 0.05);
        outerRing.yaw = (float) (animationProgress * -0.1);

        this.head.pivotX = (float) (entity.getRandom().nextFloat() - 0.5);
        this.head.pivotY = (float) (31 + (entity.getRandom().nextFloat() - 0.5));
        this.head.pivotZ = (float) (entity.getRandom().nextFloat() - 0.5);

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
	}

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        innerRing.render(matrixStack, buffer, packedLight, packedOverlay);
        outerRing.render(matrixStack, buffer, packedLight, packedOverlay);
        head.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }
}