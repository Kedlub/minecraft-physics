package me.arzyk.physics.world;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.voxel.VoxelInfo;
import com.bulletphysics.collision.shapes.voxel.VoxelPhysicsWorld;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.List;

public class MinecraftPhysicsWorld extends DiscreteDynamicsWorld {

    static BroadphaseInterface broadphaseInterface = new DbvtBroadphase();
    static ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
    static CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
    static Dispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
    World world;

    public MinecraftPhysicsWorld(World world) {
        super(dispatcher,broadphaseInterface,constraintSolver,collisionConfiguration);
        this.setGravity(new Vector3f(0,-10f,0));
        this.world = world;
    }

    public VoxelInfo getCollisionShapeAt(int x, int y, int z) {
        return generateVoxelInfo(x,y,z);
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
                return null;
            }

            @Override
            public boolean isBlocking() {
                return !world.isAir(new BlockPos(x,y,z));
            }

            @Override
            public float getFriction() {
                return 0;
            }

            @Override
            public float getRestitution() {
                return 0;
            }
        };
    }
}
