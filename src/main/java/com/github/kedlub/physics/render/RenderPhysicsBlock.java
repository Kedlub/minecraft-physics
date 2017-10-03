package com.github.kedlub.physics.render;

import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import com.github.kedlub.physics.model.ModelCube;
import net.minecraft.block.BlockAnvil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Created by Kubik on 16.06.2017.
 */
public class RenderPhysicsBlock extends Render {

    ModelCube modelCube;

    public RenderPhysicsBlock(RenderManager renderManager) {
        super(renderManager);
        modelCube = new ModelCube();
    }

    public void doRender(EntityPhysicsBlock entBlock, double posX, double posY, double posZ, float par8, float renderTick)
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
    }
}
