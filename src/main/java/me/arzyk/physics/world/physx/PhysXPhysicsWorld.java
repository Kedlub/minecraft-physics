package me.arzyk.physics.world.physx;

import me.arzyk.physics.world.MinecraftPhysicsWorld;
import me.arzyk.physics.world.RigidBody;
import me.arzyk.physics.world.shapes.Shape;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class PhysXPhysicsWorld extends MinecraftPhysicsWorld {

    public PhysXPhysicsWorld(World world) {
        super(world);
    }

    @Override
    public void updateChunkCache(int x, int z) {
        Chunk chunk = world.getChunk(x, z);
        PhysXPhysicsChunk physicsChunk = new PhysXPhysicsChunk();
        int startY = 0;
        int endY = 256;

        for (int xi = 0; xi < 16; xi++) {
            for (int zi = 0; zi < 16; zi++) {
                for (int yi = startY; yi < endY; yi++) {
                    BlockPos pos = new BlockPos(xi + (x << 4), yi, zi + (z << 4));
                    BlockState state = chunk.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block.getMaterial(state).isSolid() && !block.hasTileEntity(state)) {
                        physicsChunk.setSolid(xi, yi, zi, true);
                    }
                }
            }
        }

        chunkCache.put(new BlockPos(x, z), physicsChunk);
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
