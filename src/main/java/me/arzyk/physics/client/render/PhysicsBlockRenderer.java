package me.arzyk.physics.client.render;

import me.arzyk.physics.entity.PhysicsBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import javax.vecmath.Quat4f;

public class PhysicsBlockRenderer extends EntityRenderer<PhysicsBlockEntity> {

    private final BlockRenderManager blockRenderManager;

    public PhysicsBlockRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    public boolean shouldRender(PhysicsBlockEntity entity, Frustum frustum, double x, double y, double z) {
        entity.interpolate();

        return super.shouldRender(entity, frustum, x, y , z);
    }

    public void render(PhysicsBlockEntity physicsBlockEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        BlockState blockState = physicsBlockEntity.getBlockState();
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                matrixStack.push();
                matrixStack.translate(-physicsBlockEntity.getX(), -physicsBlockEntity.getY(), -physicsBlockEntity.getZ());
                matrixStack.translate(physicsBlockEntity.renderPosition.x, physicsBlockEntity.renderPosition.y, physicsBlockEntity.renderPosition.z);

                Quat4f quat = physicsBlockEntity.getRotation();
                matrixStack.translate(0,0.5f,0);
                matrixStack.multiply(new Quaternion(quat.x,quat.y,quat.z,quat.w));
                matrixStack.translate(0,-0.5f,0);
                matrixStack.translate(-0.5, 0f, -0.5);

                this.blockRenderManager.renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);
                matrixStack.pop();
                super.render(physicsBlockEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
            }
        }
    }

    @Override
    public Identifier getTexture(PhysicsBlockEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
