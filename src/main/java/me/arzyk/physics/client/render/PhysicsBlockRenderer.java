package me.arzyk.physics.client.render;

import me.arzyk.physics.entity.PhysicsBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import javax.vecmath.Quat4f;

public class PhysicsBlockRenderer extends EntityRenderer<PhysicsBlockEntity> {

    private final BlockRenderManager blockRenderManager;

    public PhysicsBlockRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    public void render(PhysicsBlockEntity physicsBlockEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BlockState blockState = physicsBlockEntity.getBlockState();
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            World world = physicsBlockEntity.getWorld();
            if (blockState != world.getBlockState(physicsBlockEntity.getBlockPos()) && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                matrixStack.push();
                BlockPos blockPos = new BlockPos(physicsBlockEntity.getX(), physicsBlockEntity.getBoundingBox().maxY, physicsBlockEntity.getZ());
                matrixStack.translate(-0.5, 0.0, -0.5);
                Quat4f quat = physicsBlockEntity.getRotation();
                matrixStack.multiply(new Quaternion(quat.x,quat.y,quat.z,quat.w));
                this.blockRenderManager.getModelRenderer().render(world, this.blockRenderManager.getModel(blockState), blockState, blockPos, matrixStack, vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, Random.create(), blockState.getRenderingSeed(physicsBlockEntity.getBlockPos()), OverlayTexture.DEFAULT_UV);
                matrixStack.pop();
                super.render(physicsBlockEntity, f, g, matrixStack, vertexConsumerProvider, i);
            }
        }
    }

    @Override
    public Identifier getTexture(PhysicsBlockEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
