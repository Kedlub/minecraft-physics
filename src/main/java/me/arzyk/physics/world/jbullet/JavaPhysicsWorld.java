package me.arzyk.physics.world.jbullet;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.voxel.VoxelInfo;
import com.bulletphysics.collision.shapes.voxel.VoxelPhysicsWorld;
import com.bulletphysics.collision.shapes.voxel.VoxelWorldShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import me.arzyk.physics.world.MinecraftPhysicsWorld;
import me.arzyk.physics.world.shapes.Shape;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class JavaPhysicsWorld extends MinecraftPhysicsWorld {
    DiscreteDynamicsWorld dynamicsWorld;
    static BroadphaseInterface broadphaseInterface = new DbvtBroadphase();
    static ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
    static CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
    static Dispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
    VoxelPhysicsWorld physicsWorld;
    VoxelWorldShape worldShape;
    RigidBody worldBody;
    List<JavaRigidBody> rigidBodies = new ArrayList<>();

    public JavaPhysicsWorld(World world) {
        super(world);

        this.dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphaseInterface, constraintSolver, collisionConfiguration);

        this.setGravity(0,-10f,0);
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
        dynamicsWorld.addRigidBody(worldBody);
        dynamicsWorld.updateSingleAabb(worldBody);
    }

    public void updateChunkCache(int i, int j) {
        // JavaPhysicsWorld doesn't use chunk caching, as it's single threaded it has direct access to minecraft world
    }

    @Override
    public me.arzyk.physics.world.RigidBody createRigidBody(Shape shape) {
        return new JavaRigidBody(this, shape);
    }

    @Override
    public void addRigidBody(me.arzyk.physics.world.RigidBody rigidbody) {
        dynamicsWorld.addRigidBody(((JavaRigidBody) rigidbody).body);
        rigidBodies.add((JavaRigidBody) rigidbody);
    }

    @Override
    public void removeRigidBody(me.arzyk.physics.world.RigidBody rigidbody) {
        dynamicsWorld.removeRigidBody(((JavaRigidBody) rigidbody).body);
        rigidBodies.remove(rigidbody);
    }

    @Override
    public void tick() {
        dynamicsWorld.stepSimulation(1f, getMaxSubstep());
    }

    @Override
    public void awakenRigidBodiesInBox(float x1, float y1, float z1, float x2, float y2, float z2) {
        dynamicsWorld.awakenRigidBodiesInArea(new Vector3f(x1,y1,z1), new Vector3f(x2,y2,z2));
    }

    @Override
    public me.arzyk.physics.world.RigidBody[] getRigidBodiesInBox(float x1, float y1, float z1, float x2, float y2, float z2) {
        List<me.arzyk.physics.world.RigidBody> rigidBodies = new ArrayList<>();
        dynamicsWorld.getCollisionObjectArray().forEach(collisionObject -> {
            if(collisionObject instanceof RigidBody body) {
                Vector3f min = new Vector3f();
                Vector3f max = new Vector3f();
                body.getAabb(min,max);
                // Find which one rigidBody from rigidBodies contain this body
                var rigidBody = this.rigidBodies.stream().filter(r -> r.body == body).findFirst().orElse(null);
                if(rigidBody != null) {
                    if(min.x >= x1 && min.y >= y1 && min.z >= z1 && max.x <= x2 && max.y <= y2 && max.z <= z2) {
                        rigidBodies.add(rigidBody);
                    }
                }
            }
        });
        return rigidBodies.toArray(new me.arzyk.physics.world.RigidBody[0]);
    }

    @Override
    public void setGravity(float x, float y, float z) {
        dynamicsWorld.setGravity(new Vector3f(x,y,z));
    }

    @Override
    public Object getNativeWorld() {
        return dynamicsWorld;
    }

    @Override
    public void destroy() {
        // destroy all rigid bodies
        rigidBodies.forEach(r -> dynamicsWorld.destroy());
        dynamicsWorld.destroy();
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
                return world.getBlockState(new BlockPos(x,y,z)).isSolidBlock(world, new BlockPos(x,y,z));
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
