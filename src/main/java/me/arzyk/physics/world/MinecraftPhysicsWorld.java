package me.arzyk.physics.world;

import me.arzyk.physics.Physics;
import me.arzyk.physics.world.shapes.Shape;
import net.minecraft.world.World;

public abstract class MinecraftPhysicsWorld {
    public World world;

    public MinecraftPhysicsWorld(World world) {
        this.world = world;
    }

    public static MinecraftPhysicsWorld create(World world) {
        try {
            return Physics.instance.activeEngine.getClazz().getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void updateChunkCache(int x, int z);
    public abstract RigidBody createRigidBody(Shape shape);
    public abstract void addRigidBody(RigidBody rigidbody);
    public abstract void removeRigidBody(RigidBody rigidbody);
    public abstract void tick();
    public abstract void awakenRigidBodiesInBox(float x1, float y1, float z1, float x2, float y2, float z2);
    public abstract RigidBody[] getRigidBodiesInBox(float x1, float y1, float z1, float x2, float y2, float z2);
    public abstract void setGravity(float x, float y, float z);
    public abstract Object getNativeWorld();
    public abstract void destroy();
}
