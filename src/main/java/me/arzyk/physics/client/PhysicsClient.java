package me.arzyk.physics.client;

import me.arzyk.physics.Physics;
import me.arzyk.physics.client.render.PhysicsBlockRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PhysicsClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_PHYS_BLOCK_LAYER = new EntityModelLayer(new Identifier("physics", "phys_block"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Physics.PHYS_BLOCK, (context) -> {
            return new PhysicsBlockRenderer(context);
        });
    }
}
