package com.github.kedlub.physics;

import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by Kubik on 16.06.2017.
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void load(FMLInitializationEvent event) {
        //createEntity(EntityPhysicsBlock.class,1,"Physics Block", "physicsblock");

    }

    public static void createEntity(Class entityClass, int ID, String entityName, String name){
        //EntityRegistry.registerGlobalEntityID(EntityPhysicsBlock.class, "ball", ID);

        EntityRegistry.registerModEntity(new ResourceLocation("physics",name),entityClass, entityName, ID, PhysicsMod.instance, 64, 10, true);

    }
}
