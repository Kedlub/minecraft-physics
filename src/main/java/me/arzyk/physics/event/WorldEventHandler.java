package me.arzyk.physics.event;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import me.arzyk.physics.Physics;
import me.arzyk.physics.world.MinecraftPhysicsWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class WorldEventHandler {

    public static void onWorldLoad(MinecraftServer server,
                     ServerWorld world) {
        String dimensionID = world.getDimensionKey().getValue().toString();
        System.out.println("Server is loading a world, here will be a new dynamicsWorld created, dimension " + dimensionID);

        BroadphaseInterface broadphaseInterface = new DbvtBroadphase();
        ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        Dispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

        MinecraftPhysicsWorld physicsWorld = new MinecraftPhysicsWorld(world, dispatcher, broadphaseInterface, constraintSolver, collisionConfiguration);
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
