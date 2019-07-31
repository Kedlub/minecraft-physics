package com.github.kedlub.physics.render;

import com.bulletphysics.linearmath.QuaternionUtil;
import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import com.github.kedlub.physics.model.ModelCube;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import javax.annotation.Nullable;
import javax.vecmath.Quat4f;
import java.nio.FloatBuffer;

/**
 * Created by Kubik on 16.06.2017.
 */
@SideOnly(Side.CLIENT)
public class RenderPhysicsBlock extends Render {

    //ModelCube modelCube;

    public RenderPhysicsBlock(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
        //modelCube = new ModelCube();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    /*public void doRender(EntityPhysicsBlock entBlock, double posX, double posY, double posZ, float par8, float renderTick)
    {
        System.out.println("Rendering physics block!");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
        modelCube.render(entBlock,0,0,1,0,0,1);
        GL11.glPopMatrix();
    }


    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        System.out.println("Do Render!");
        this.doRender((EntityPhysicsBlock)par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
        return null;
    }*/

    public boolean shouldRender(EntityPhysicsBlock livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return true;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityPhysicsBlock entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        System.out.println("doRender() called");
        if (entity.block != null)
        {
            IBlockState iblockstate = entity.block;

            System.out.println("entity.block != null");

            if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL)
            {
                World world = entity.getWorldObj();

                if (iblockstate != world.getBlockState(new BlockPos(entity)) && iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE)
                {
                    this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.disableLighting();
                    Tessellator tessellator = Tessellator.getInstance();
                    VertexBuffer vertexbuffer = tessellator.getBuffer();

                    System.out.println("Rendering block at " + entity.getPosition());

                    if (this.renderOutlines)
                    {
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                    }

                    float[] matrix = new float[16];
                    FloatBuffer transformationBuffer = BufferUtils.createFloatBuffer(16);

                    Entity entity1 = Minecraft.getMinecraft().player;
                    double x1 = entity1.lastTickPosX + (entity1.posX - entity1.lastTickPosX) * Minecraft.getMinecraft().getRenderPartialTicks();
                    double y1 = entity1.lastTickPosY + (entity1.posY - entity1.lastTickPosY) * Minecraft.getMinecraft().getRenderPartialTicks();
                    double z1 = entity1.lastTickPosZ + (entity1.posZ - entity1.lastTickPosZ) * Minecraft.getMinecraft().getRenderPartialTicks();

                    //GlStateManager.translate(-x, -y, -z);

                    //GL11.glTranslatef(0.5f,0.5f,0.5f);
                    entity.xform.getOpenGLMatrix(matrix);
                    //Quat4f rotation = new Quat4f();
                    //entity.xform.getRotation(rotation);
                    //Rotations rotation = entity.getRotation();
                    //Quaternion quat = new Quaternion(rotation.getX(),rotation.getY(),rotation.getZ(),0);
                    Quaternion quat = entity.getRotation();

                    transformationBuffer.clear();
                    transformationBuffer.put(matrix);
                    transformationBuffer.flip();

                    GlStateManager.pushMatrix(); // Save the current OpenGL transformation




                    vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
                    BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);

                    float trx = (float)(x - (double)blockpos.getX() - 0.5D);
                    float tray = (float)(y - (double)blockpos.getY());
                    float trz = (float)(z - (double)blockpos.getZ() - 0.5D);

                    //GlStateManager.multMatrix(transformationBuffer); // Apply the object transformation

                    //GlStateManager.translate(trx, tray, trz);
                    GlStateManager.translate(0f,-0.5f,0f);
                    GlStateManager.translate((float)x,(float)y,(float)z);
                    GlStateManager.rotate(quat);

                    //GlStateManager.translate(trx, tray, trz);

                    int i = MathHelper.floor(entity.posX);
                    int j = MathHelper.floor(entity.getEntityBoundingBox().maxY);
                    int k = MathHelper.floor(entity.posZ);




                    vertexbuffer.setTranslation((double)((float)(-i) - 0.5f), (double)((float)(-j) - 0.5f), (double)((float)(-k) - 0.5f));

                    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                    blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos, vertexbuffer, false, MathHelper.getPositionRandom(entity.getOrigin()));

                    vertexbuffer.setTranslation(0,0,0);
                    tessellator.draw();

                    if (this.renderOutlines)
                    {
                        GlStateManager.disableOutlineMode();
                        GlStateManager.disableColorMaterial();
                    }

                    GlStateManager.popMatrix();

                    GlStateManager.enableLighting();
                    GlStateManager.popMatrix();
                    super.doRender(entity, x, y, z, entityYaw, partialTicks);
                }
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityPhysicsBlock entity)
    {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then handing it off to a worker
     * function which does the actual work. In all probabilty, the class Render
     * is generic (Render<T extends Entity) and this method has signature public
     * void func_76986_a(T entity, double d, double d1, double d2, float f,
     * float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_,
                         float p_76986_8_, float p_76986_9_) {
        this.doRender((EntityPhysicsBlock) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_,
                p_76986_9_);
    }

    public static class RenderFactory implements IRenderFactory<EntityPhysicsBlock>
    {
        @Override
        public Render<? super EntityPhysicsBlock> createRenderFor(RenderManager manager)
        {
            return new RenderPhysicsBlock(manager);
        }
    }
}
