package com.github.kedlub.physics;

import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.collision.shapes.voxel.VoxelInfo;
import com.bulletphysics.collision.shapes.voxel.VoxelPhysicsWorld;
import com.bulletphysics.collision.shapes.voxel.VoxelWorldShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.ContactSolverInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.github.kedlub.physics.command.CommandPhysics;
import com.github.kedlub.physics.entity.EntityPhysicsBlock;
//import com.sun.deploy.util.SessionState;
//import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.github.kedlub.physics.render.RenderPhysicsBlock;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.Array;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = PhysicsMod.MODID, version = PhysicsMod.VERSION)
public class PhysicsMod
{
    public static final String MODID = "physics";
    public static final String VERSION = "0.1";
    @Mod.Instance("physics")
    public static PhysicsMod instance;
    public static final Logger logger = LogManager.getLogger(MODID);
    public static List<EntityPhysicsBlock> blocks = new ArrayList();
    public List<RigidBody> chunks = new ArrayList();

    public static BoxShape cubeSize = new BoxShape(new Vector3f(0.5f,0.5f,0.5f));
    public static SphereShape sphereSize = new SphereShape(0.5f);
    //public static SphereShape cubeSize = new SphereShape(0.5f);
    //public static Inertia
    @SidedProxy(clientSide = "com.github.kedlub.physics.ClientProxy", serverSide = "com.github.kedlub.physics.ServerProxy")
    public static CommonProxy proxy;

    public BroadphaseInterface broadphaseInterface = new DbvtBroadphase();
    public ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
    public CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
    public Dispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
    public DiscreteDynamicsWorld dynamicsWorld;
    public Vector3f worldAabbMin;
    public Vector3f worldAabbMax;
    public AxisSweep3 overlappingPairCache;
    public RigidBody worldBody;
    public boolean updateShown = true;

    @SideOnly(Side.CLIENT)
    public GuiInfobox infobox;

    public static int ready;

    public static boolean paused;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);

        EventManager manager = new EventManager();
        MinecraftForge.EVENT_BUS.register(manager);

        //ClientProxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        EntityRegistry.registerModEntity(new ResourceLocation("physics","physicsblock"),EntityPhysicsBlock.class, "physics:physicsblock", 1, this, 64, 20, true);
        instance = this;
        proxy.load(event);

        /*if(event.getSide() == Side.CLIENT) {
            infobox = new GuiInfobox(Minecraft.getMinecraft());
        }*/

        ready = 1;


        this.worldAabbMin = new Vector3f(-20000.0F, -20000.0F, -20000.0F);
        this.worldAabbMax = new Vector3f(20000.0F, 20000.0F, 20000.0F);
        this.overlappingPairCache = new AxisSweep3(this.worldAabbMin, this.worldAabbMax, 16384, new HashedOverlappingPairCache());


        /*BoxShape bshape = new BoxShape(new Vector3f(20,4f,20));

        Transform xform = new Transform();
        xform.setIdentity();

        RigidBody rb = new RigidBody(0, new DefaultMotionState(), bshape);

        xform.origin.set(0,0,0);
        rb.setCenterOfMassTransform(xform);

        dynamicsWorld.addRigidBody(rb);*/








        // some example code
        //System.out.println("DIRT BLOCK >> "+Blocks.DIRT.getUnlocalizedName());

        //CommonProxy.preInit();
        //ClientProxy.preInit();

        //ClientProxy.init();
    }

    @EventHandler
    public static void serverInit(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandPhysics());
    }

    public void updateInfobox() {
        System.out.println("Running while");
        //while(PhysicsMod.instance.infobox.notificationTime > 0) {
        //PhysicsMod.instance.infobox.updateAchievementWindow();
        //}
    }

    /*public void addStaticChunk(Vector3f position, TriangleMeshShape chunkShape) {
        Matrix3f rot = new Matrix3f();
        rot.setIdentity();

        DefaultMotionState blockMotionState = new DefaultMotionState(new Transform(new Matrix4f(rot, position, 1.0f)));



        RigidBodyConstructionInfo blockCI = new RigidBodyConstructionInfo(0, blockMotionState, chunkShape, new Vector3f());
        RigidBody chunk = new RigidBody(blockCI);

        dynamicsWorld.addRigidBody(chunk);
        chunks.add(chunk);
    }*/
}
