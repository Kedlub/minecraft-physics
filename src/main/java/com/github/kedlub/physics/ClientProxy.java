package com.github.kedlub.physics;

import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import com.github.kedlub.physics.render.RenderPhysicsBlock;
import com.github.kedlub.physics.render.RenderPhysicsBlockFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.input.Keyboard;

/**
 * Created by Kubik on 16.06.2017.
 */
public class ClientProxy extends CommonProxy {

    public static KeyBinding[] keyBindings;

    public void preInit() {
        super.preInit();
        //RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, RenderPhysicsBlock::new);
        //RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, new RenderPhysicsBlock(Minecraft.getMinecraft().getRenderManager()));
    }

    public void load() {
        super.load();

        // declare an array of key bindings
        keyBindings = new KeyBinding[3];

        // instantiate the key bindings
        keyBindings[0] = new KeyBinding(I18n.format("key.spawnblock.desc"), Keyboard.KEY_P, I18n.format("key.physics.category"));
        keyBindings[1] = new KeyBinding(I18n.format("key.removeblocks.desc"), Keyboard.KEY_O, I18n.format("key.physics.category"));
        keyBindings[2] = new KeyBinding(I18n.format("key.pauseblocks.desc"), Keyboard.KEY_R, I18n.format("key.physics.category"));

        // register all the key bindings
        for (int i = 0; i < keyBindings.length; ++i)
        {
            ClientRegistry.registerKeyBinding(keyBindings[i]);
        }
    }
}
