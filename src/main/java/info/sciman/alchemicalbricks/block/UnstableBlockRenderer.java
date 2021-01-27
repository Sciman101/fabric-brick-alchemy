package info.sciman.alchemicalbricks.block;

import com.google.common.collect.ImmutableList;
import info.sciman.alchemicalbricks.block.entity.UnstableBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class UnstableBlockRenderer extends BlockEntityRenderer<UnstableBlockEntity> {

    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderLayer> field_21732 = (List)IntStream.range(0, 16).mapToObj((i) -> {
        return RenderLayer.getEndPortal(i + 1);
    }).collect(ImmutableList.toImmutableList());

    public UnstableBlockRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(UnstableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RANDOM.setSeed(31100L);
        double d = entity.getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true);
        int k = this.method_3592(d);
        Matrix4f matrix4f = matrices.peek().getModel();
        this.method_23084(entity, 1f, 0.15F, matrix4f, vertexConsumers.getBuffer((RenderLayer)field_21732.get(0)));

        for(int l = 1; l < k; ++l) {
            this.method_23084(entity, 1f, 2.0F / (float)(18 - l), matrix4f, vertexConsumers.getBuffer((RenderLayer)field_21732.get(l)));
        }
    }

    private void method_23084(UnstableBlockEntity endPortalBlockEntity, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
        float h = (RANDOM.nextFloat() * 0.5F + 0.5F) * g;
        float i = (RANDOM.nextFloat() * 0.5F + 0.2F) * g;
        float j = (RANDOM.nextFloat() * 0.5F + 0.5F) * g;
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, h, i, j, Direction.SOUTH);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, h, i, j, Direction.NORTH);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.EAST);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.WEST);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, h, i, j, Direction.DOWN);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, f, f, 1.0F, 1.0F, 0.0F, 0.0F, h, i, j, Direction.UP);
    }

    private void method_23085(UnstableBlockEntity entity, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, Direction direction) {
        vertexConsumer.vertex(matrix4f, f, h, j).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, g, h, k).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, g, i, l).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, f, i, m).color(n, o, p, 1.0F).next();
    }

    protected int method_3592(double d) {
        if (d > 36864.0D) {
            return 1;
        } else if (d > 25600.0D) {
            return 3;
        } else if (d > 16384.0D) {
            return 5;
        } else if (d > 9216.0D) {
            return 7;
        } else if (d > 4096.0D) {
            return 9;
        } else if (d > 1024.0D) {
            return 11;
        } else if (d > 576.0D) {
            return 13;
        } else {
            return d > 256.0D ? 14 : 15;
        }
    }
}
