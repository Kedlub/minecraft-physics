package com.github.kedlub.physics.render;

import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by Kubik on 16.06.2017.
 */
public class RenderPhysicsBlockFactory implements IRenderFactory<EntityPhysicsBlock> {
    public static final RenderPhysicsBlockFactory INSTANCE = new RenderPhysicsBlockFactory();

    @Override
    public Render<? super EntityPhysicsBlock> createRenderFor(RenderManager manager) {
        return new RenderPhysicsBlock(manager);
    }
}
