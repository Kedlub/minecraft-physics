package me.arzyk.physics.client.render;

import com.bulletphysics.linearmath.Transform;
import me.arzyk.physics.entity.PhysicsBlockEntity;
import me.arzyk.physics.util.VecUtils;
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
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;

import javax.vecmath.Quat4f;
import java.nio.FloatBuffer;

public class PhysicsBlockRenderer extends EntityRenderer<PhysicsBlockEntity> {

    private final BlockRenderManager blockRenderManager;
    protected static Transform transform = new Transform();
    private static final FloatBuffer renderMatrix = BufferUtils.createFloatBuffer(16);

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
            World world = physicsBlockEntity.getWorld();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                matrixStack.push();
                matrixStack.translate(-physicsBlockEntity.getX(), -physicsBlockEntity.getY(), -physicsBlockEntity.getZ());
                matrixStack.translate(physicsBlockEntity.renderPosition.x, physicsBlockEntity.renderPosition.y, physicsBlockEntity.renderPosition.z);
                //BlockPos blockPos = new BlockPos(physicsBlockEntity.getX(), physicsBlockEntity.getBoundingBox().maxY, physicsBlockEntity.getZ());

                transform.setIdentity();
                transform.origin.set(physicsBlockEntity.renderPosition);
                transform.setRotation(physicsBlockEntity.renderRotation);
                VecUtils.setBufferFromTransform(renderMatrix, transform);
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.writeColumnMajor(renderMatrix);


                //matrixStack.translate(0.5f,0.5f,0.5f);
                Quat4f quat = physicsBlockEntity.getRotation();


                matrixStack.translate(0,0.5f,0);
                matrixStack.multiply(new Quaternion(quat.x,quat.y,quat.z,quat.w));
                matrixStack.translate(0,-0.5f,0);
                matrixStack.translate(-0.5, 0f, -0.5);


                //matrixStack.multiplyPositionMatrix(matrix4f);


                //matrixStack.multiplyPositionMatrix(matrix4f);


                //this.blockRenderManager.getModelRenderer().render(world, this.blockRenderManager.getModel(blockState), blockState, new BlockPos(VecUtils.toVec3d(physicsBlockEntity.renderPosition)), matrixStack, vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, Random.create(), blockState.getRenderingSeed(physicsBlockEntity.getPhysicsBlockPos()), OverlayTexture.DEFAULT_UV);
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
