package me.arzyk.physics.event;

import me.arzyk.physics.Physics;
import me.arzyk.physics.world.MinecraftPhysicsWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class WorldEventHandler {

    public static void onWorldLoad(MinecraftServer server,
                     ServerWorld world) {
        String dimensionID = world.getDimensionKey().getValue().toString();
        System.out.println("Server is loading a world, here will be a new dynamicsWorld created, dimension " + dimensionID);
        MinecraftPhysicsWorld physicsWorld = new MinecraftPhysicsWorld(world);
        Physics.dynamicWorlds.put(dimensionID, physicsWorld);
    }

    public static void onWorldUnload(MinecraftServer server, ServerWorld world) {
        String dimensionID = world.getDimensionKey().getValue().toString();
        System.out.println("Removing dynamicsWorld for dimension " + dimensionID);
        Physics.dynamicWorlds.remove(dimensionID);
    }

    public static void onWorldTick(ServerWorld world) {
        String dimensionID = world.getDimensionKey().getValue().toString();
        MinecraftPhysicsWorld physicsWorld = Physics.dynamicWorlds.get(dimensionID);
        physicsWorld.stepSimulation(0.026666668F);
    }
}
