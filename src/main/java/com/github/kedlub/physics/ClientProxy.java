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
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Created by Kubik on 16.06.2017.
 */
public class ClientProxy extends CommonProxy {

    public static KeyBinding[] keyBindings;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        //RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, new RenderPhysicsBlock.RenderFactory());
        //RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, RenderPhysicsBlockFactory.INSTANCE);
        System.out.println("Registered renderer for EntityPhysicsBlock");
        RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, RenderPhysicsBlock::new);
        //RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, new RenderPhysicsBlock.RenderFactory());
        /*RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, new IRenderFactory<EntityPhysicsBlock>()
        {
            @Override
            public Render<? super EntityPhysicsBlock> createRenderFor(RenderManager manager)
            {
                return new RenderPhysicsBlock(manager);
            }
        });*/
        /*RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.entityRenderMap.put(EntityPhysicsBlock.class, new RenderPhysicsBlock(renderManager));*/
        //RenderingRegistry.registerEntityRenderingHandler(EntityPhysicsBlock.class, new RenderPhysicsBlock(Minecraft.getMinecraft().getRenderManager()));
    }

    @Override
    public void load(FMLInitializationEvent event) {
        super.load(event);

        registerKeybinds();


    }

    public void registerKeybinds() {
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
