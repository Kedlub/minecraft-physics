package me.arzyk.physics.world.physx;

import me.arzyk.physics.world.MinecraftPhysicsWorld;
import me.arzyk.physics.world.RigidBody;
import me.arzyk.physics.world.shapes.Shape;
import net.minecraft.world.World;

public class PhysXPhysicsWorld extends MinecraftPhysicsWorld {

    public PhysXPhysicsWorld(World world) {
        super(world);
    }

    @Override
    public void updateChunkCache(int x, int z) {

    }

    @Override
    public RigidBody createRigidBody(Shape shape) {
        return null;
    }

    @Override
    public void addRigidBody(RigidBody rigidbody) {

    }

    @Override
    public void removeRigidBody(RigidBody rigidbody) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void awakenRigidBodiesInBox(float x1, float y1, float z1, float x2, float y2, float z2) {

    }

    @Override
    public RigidBody[] getRigidBodiesInBox(float x1, float y1, float z1, float x2, float y2, float z2) {
        return new RigidBody[0];
    }

    @Override
    public void setGravity(float x, float y, float z) {

    }

    @Override
    public Object getNativeWorld() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
