package me.arzyk.physics.world;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.voxel.VoxelInfo;
import com.bulletphysics.collision.shapes.voxel.VoxelPhysicsWorld;
import com.bulletphysics.collision.shapes.voxel.VoxelWorldShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class MinecraftPhysicsWorld extends DiscreteDynamicsWorld {
    BroadphaseInterface broadphaseInterface;
    ConstraintSolver constraintSolver;
    CollisionConfiguration collisionConfiguration;
    Dispatcher dispatcher;
    VoxelPhysicsWorld physicsWorld;
    VoxelWorldShape worldShape;
    RigidBody worldBody;
    World world;
    Thread updateThread;
    List<PhysicsChunk> chunks = new ArrayList<>();

    public MinecraftPhysicsWorld(World world, Dispatcher dispatcher, BroadphaseInterface broadphaseInterface, ConstraintSolver constraintSolver, CollisionConfiguration collisionConfiguration) {
        super(dispatcher,broadphaseInterface,constraintSolver,collisionConfiguration);

        this.broadphaseInterface = broadphaseInterface;
        this.collisionConfiguration = collisionConfiguration;
        this.constraintSolver = constraintSolver;
        this.dispatcher = dispatcher;

        this.setGravity(new Vector3f(0,-10f,0));
        this.world = world;
        Transform transform = new Transform();
        transform.setIdentity();
        Transform transformOffset = new Transform();
        transformOffset.setIdentity();
        transformOffset.origin.set(0.25f,0.5f,0.25f);
        this.physicsWorld = this::generateVoxelInfo;
        this.worldShape = new VoxelWorldShape(physicsWorld);
        this.worldShape.calculateLocalInertia(0, new Vector3f());
        this.worldShape.setLocalScaling(new Vector3f(0.5f,0.5f,0.5f));
        this.worldBody = new RigidBody(0, new DefaultMotionState(transform), this.worldShape);
        this.worldBody.setCollisionFlags(CollisionFlags.STATIC_OBJECT | this.worldBody.getCollisionFlags());
        this.addRigidBody(worldBody);
        this.updateSingleAabb(worldBody);

        updateThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (this) {
                        stepSimulation(1F, getMaxSubstep());
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        updateThread.start();

        /*ChunkUpdateCallback.EVENT.register((chunk -> {
            if(chunk.getWorld().isClient) return ActionResult.PASS;
            updateChunkCache(chunk.getPos().x, chunk.getPos().z);
            return ActionResult.PASS;
        }));*/
    }

    public void dispose() {
        updateThread.interrupt();
        destroy();
    }

    public void updateChunkCache(int i, int j) {
        var chunk = this.world.getChunk(i,j);
        var filteredChunks = chunks.stream().filter(c -> c.x == i && c.z == j).toList();
        var physChunk = filteredChunks.size() > 0 ? filteredChunks.get(0) : new PhysicsChunk(i,j);
        for (int x = 0; x < 15; x++) {
            for (int z = 0; z < 15; z++) {
                for(int y = chunk.getBottomY(); y < chunk.getTopY(); y++) {
                    physChunk.solidBlocks.put(new BlockPos(x,y,z),chunk.getBlockState(new BlockPos(x,y,z)).isSolidBlock(world, new BlockPos(x,y,z)));
                }
            }
        }
        if(!chunks.contains(physChunk)) {
            chunks.add(physChunk);
        }
    }

    private VoxelInfo generateVoxelInfo(int x,int y, int z) {
        BlockState block = world.getBlockState(new BlockPos(x,y,z));
        List<Box> boxes = null;
        if(!block.isAir()) {
            var shape = block.getCollisionShape(world, new BlockPos(x,y,z));
            boxes = shape.getBoundingBoxes();
        }
        return new VoxelInfo() {
            @Override
            public boolean isColliding() {
                return false;
            }

            @Override
            public Object getUserData() {
                return null;
            }

            @Override
            public CollisionShape getCollisionShape() {
                return new BoxShape(new Vector3f(0.5f,0.5f,0.5f));
            }

            @Override
            public javax.vecmath.Vector3f getCollisionOffset() {
                return new Vector3f(0,0,0);
            }

            @Override
            public boolean isBlocking() {
                if(chunks.isEmpty()) {
                    return true;
                }
                var physChunk = chunks.stream().filter(c -> c.x == ChunkSectionPos.getSectionCoord(x) && c.z == ChunkSectionPos.getSectionCoord(z)).toList().get(0);
                return physChunk.solidBlocks.get(new BlockPos(x,y,z));
            }

            @Override
            public float getFriction() {
                return 0.3f;
            }

            @Override
            public float getRestitution() {
                return 0.2f;
            }
        };
    }

    public void update() {
        stepSimulation(1F, getMaxSubstep());

    }

    long lastFrame;

    /**
     * Calculate how many milliseconds have passed
     * since last frame.
     *
     * @return milliseconds passed since last frame
     */
    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;
    }

    /**
     * Get the accurate system time
     *
     * @return The system time in milliseconds
     */
    public long getTime() {
        return System.currentTimeMillis();
    }

    protected float lastDelta;

    public int getMaxSubstep() {
        lastDelta = getDelta();
        return MathHelper.clamp(Math.round(lastDelta / 10), 1, 100);
    }
}
