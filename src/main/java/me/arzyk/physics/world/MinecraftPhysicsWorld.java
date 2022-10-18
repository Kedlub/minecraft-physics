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
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
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
                return block.isFullCube(world, new BlockPos(x,y,z));
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
                return block.isFullCube(world, new BlockPos(x,y,z));
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
}
