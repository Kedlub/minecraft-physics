package com.github.kedlub.physics;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@Mod.EventBusSubscriber
public class CommonHandler {

    /*
        Preparation for 1.12 Update, now it does nothing
     */

    /*@SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry entry = EntityEntryBuilder.create()
                .entity(MyEntity.class)
                .id(new ResourceLocation(...), ID++)
                .name("my_entity")
                .egg(0xFFFFFF, 0xAAAAAA)
                .tracker(64, 20, false)
                .build();﻿
        event.getRegistry();
    }*/

}
