package me.arzyk.physics.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class WorldEventHandler {

    public static void onWorldLoad(MinecraftServer server,
                     ServerWorld world) {
        String dimensionID = world.getDimensionKey().getValue().toString();
        System.out.println("Server is loading a world, here will be a new dynamicsWorld created, dimension " + dimensionID);

    }
}
