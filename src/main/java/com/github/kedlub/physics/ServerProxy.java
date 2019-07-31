package com.github.kedlub.physics;

import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
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
import com.github.kedlub.physics.command.CommandPhysics;
import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.vecmath.Vector3f;

/**
 * Created by Kubik on 08.12.2017.
 */
public class ServerProxy extends CommonProxy {


    public void preInit(FMLPreInitializationEvent event) {
        EventManager manager = new EventManager();
        MinecraftForge.EVENT_BUS.register(manager);
    }

    public void load(FMLInitializationEvent event) {

        
    }

}
