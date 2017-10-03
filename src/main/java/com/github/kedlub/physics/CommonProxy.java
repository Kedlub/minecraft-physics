package com.github.kedlub.physics;

import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by Kubik on 16.06.2017.
 */
public class CommonProxy {

    public void preInit() {
        EventManager manager = new EventManager();
        manager.setCubeList();
        MinecraftForge.EVENT_BUS.register(manager);
    }

    public void load() {
        createEntity(EntityPhysicsBlock.class,1000,"Physics Block");
    }

    public static void createEntity(Class entityClass, int ID, String entityName){
        //EntityRegistry.registerGlobalEntityID(EntityPhysicsBlock.class, "ball", ID);

        EntityRegistry.registerModEntity(new ResourceLocation("physics"),entityClass, entityName, ID, PhysicsMod.instance, 128, 1, true);

    }
}
