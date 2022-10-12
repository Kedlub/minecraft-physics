package me.arzyk.physics.client;

import me.arzyk.physics.Physics;
import me.arzyk.physics.client.render.PhysicsBlockRenderer;
import me.arzyk.physics.network.DataSerializers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class PhysicsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Physics.PHYS_BLOCK, PhysicsBlockRenderer::new);
        DataSerializers.register();
    }
}
