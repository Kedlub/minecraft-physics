package com.github.kedlub.physics.entity;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.demos.applet.Sphere;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.github.kedlub.physics.PhysicsMod;
import com.github.kedlub.physics.model.ModelCube;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import static com.github.kedlub.physics.PhysicsMod.blocks;

/**
 * Created by Kubik on 08.06.2017.
 */
public class EntityPhysicsBlock extends Entity implements IEntityAdditionalSpawnData {

    public static final Block DEFAULT_BLOCK = Blocks.STONE;

    //private static final DataParameter<BlockPos> VELOCITY = EntityDataManager.createKey(EntityPhysicsBlock.class, DataSerializers.BLOCK_POS);
    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityPhysicsBlock.class, DataSerializers.BLOCK_POS);
    protected static final DataParameter<Rotations> ROTATION = EntityDataManager.createKey(EntityPhysicsBlock.class, DataSerializers.ROTATIONS);
    protected static final DataParameter<Float> ROTATION_W = EntityDataManager.createKey(EntityPhysicsBlock.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> BLOCKID = EntityDataManager.createKey(EntityPhysicsBlock.class, DataSerializers.FLOAT);
    public RigidBody rigidBody;
    public int number;
    public Transform xform = new Transform();
    public ModelCube model;
    public Vector3f velocity = new Vector3f();
    public IBlockState block = DEFAULT_BLOCK.getDefaultState();
    float defaultX, defaultY, defaultZ;
    float defaultVelX, defaultVelY, defaultVelZ;


    public EntityPhysicsBlock(World worldIn) {
        super(worldIn);
    }

    public EntityPhysicsBlock(World worldIn, float x, float y, float z, float velX, float velY, float velZ, IBlockState block1) {
        super(worldIn);
        block = block1;
        defaultX = x;
        defaultY = y;
        defaultZ = z;
        defaultVelX = velX;
        defaultVelY = velY;
        defaultVelZ = velZ;




    }

    @SideOnly(Side.CLIENT)
    public World getWorldObj()
    {
        return this.world;
    }

    public void setOrigin(BlockPos p_184530_1_)
    {
        this.dataManager.set(ORIGIN, p_184530_1_);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getOrigin()
    {
        return (BlockPos)this.dataManager.get(ORIGIN);
    }

    /*public void setVelocity(BlockPos p_184530_1_)
    {
        this.dataManager.set(VELOCITY, p_184530_1_);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getVelocity()
    {
        return (BlockPos)this.dataManager.get(VELOCITY);
    }


*/

    public void setRotation(Quaternion quat)
    {
        Rotations rot = new Rotations(quat.x,quat.y,quat.z);
        this.dataManager.set(ROTATION, rot);
        this.dataManager.set(ROTATION_W, quat.w);
    }

    @SideOnly(Side.CLIENT)
    public Quaternion getRotation() {
        Rotations rot = this.dataManager.get(ROTATION);
        float rot_w = this.dataManager.get(ROTATION_W);
        Quaternion quat = new Quaternion(rot.getX(),rot.getY(),rot.getZ(),rot_w);

        return quat;
    }

    public void init(float x, float y, float z, float velX, float velY, float velZ, IBlockState block1) {


    }

    public void display() {
        GL11.glTranslated(posX,posY,posZ);
        model.render(this,0,0,0,0,0,1);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    int spawnTick = 0;

    /**
     * Custom update method that is called from EventManager.class
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        if(world.isRemote) {
            if (PhysicsMod.ready != 0 || PhysicsMod.paused) return;
            //System.out.println("onUpdate() ");

            if (this.block == null || this.block.getMaterial() == Material.AIR) {
                //System.out.println("block is null! for entity " + entityUniqueID);
                if (spawnTick < 100) {
                    spawnTick += 1;
                } else {
                    this.setDead();
                }
                return;
            }
            //System.out.println("Previous position:" + posX + " " + posY + " " + posZ);
            if(rigidBody != null && xform != null) {
                this.rigidBody.getCenterOfMassTransform(this.xform);
                //this.posX = xform.origin.x;
                //this.posY = xform.origin.y;
                //this.posZ = xform.origin.z;
                this.setPosition(xform.origin.x, xform.origin.y, xform.origin.z);
                this.dataManager.set(BLOCKID, (float) Block.getStateId(block));

                this.rigidBody.getLinearVelocity(this.velocity);

                Quat4f quat4f = new Quat4f();
                if (xform != null) {
                    this.xform.getRotation(quat4f);
                }

                setRotation(new Quaternion(quat4f.x, quat4f.y, quat4f.z, quat4f.w));
            }
        }
        else {
            block = Block.getStateById((int)((float)this.dataManager.get(BLOCKID)));
        }



        //System.out.println("New position:" + posX + " " + posY + " " + posZ);
    }


    protected void entityInit()
    {
        //super.entityInit();s
        if(!world.isRemote) return;
        BoxShape localBoxShape = PhysicsMod.cubeSize;
        Vector3f localVector3f = new Vector3f(0.5f,0.5f,0.5f);
        localBoxShape.calculateLocalInertia(20,localVector3f);

        this.xform = new Transform();
        this.xform.setIdentity();

        MotionState motion = new DefaultMotionState(this.xform);
        RigidBodyConstructionInfo rbinfo = new RigidBodyConstructionInfo(20.0F, motion, localBoxShape, localVector3f);
        rigidBody = new RigidBody(rbinfo);
        this.rigidBody.setRestitution(0.01F);
        this.rigidBody.setFriction(0.8F);
        this.rigidBody.setDamping(0.4F, 0.4F);

        this.setSize(0.98F, 0.98F);


        PhysicsMod.instance.dynamicsWorld.addRigidBody(this.rigidBody);


        //number = blocks.length + 1;
        //blocks[blocks.length + 1] = this;
        //System.out.println("Spawned block!");
        blocks.add(this);


        this.setOrigin(new BlockPos(this));

        this.velocity = new Vector3f(0.0F, 0.0F, 0.0F);

        this.xform.origin.set(defaultX,defaultY,defaultZ);

        this.velocity.x = ((float)defaultVelX);
        this.velocity.y = ((float)defaultVelY);
        this.velocity.z = ((float)defaultVelZ);

        this.rigidBody.setCenterOfMassTransform(xform);
        this.rigidBody.setLinearVelocity(this.velocity);
        this.rigidBody.activate();

        /*this.dataManager.register(FIRST_HEAD_TARGET, Integer.valueOf(0));
        this.dataManager.register(SECOND_HEAD_TARGET, Integer.valueOf(0));
        this.dataManager.register(THIRD_HEAD_TARGET, Integer.valueOf(0));
        this.dataManager.register(INVULNERABILITY_TIME, Integer.valueOf(0));*/
        this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
        Quat4f quat4f = new Quat4f();
        if(xform != null) {
            this.xform.getRotation(quat4f);
        }
        this.dataManager.register(ROTATION, new Rotations(quat4f.x,quat4f.y,quat4f.z));
        this.dataManager.register(ROTATION_W, quat4f.w);
        this.dataManager.register(BLOCKID, 1f);
        this.setSize(0.98F, 0.98F);
        //this.dataManager.register(VELOCITY, new BlockPos(velocity.x,velocity.y,velocity.z));
    }

    @Override
    protected void kill() {
        this.setDead();
    }

    @Override
    public void setDead() {
        if(this.rigidBody != null) {
            PhysicsMod.instance.dynamicsWorld.removeRigidBody(this.rigidBody);
        }
        if(blocks.contains(this)) {
            blocks.remove(this);
        }
        this.isDead = true;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        //super.writeEntityToNBT(compound);
        //compound.setInteger("Invul", this.getInvulTime());

        /*Block block1 = this.block != null ? this.block.getBlock() : Blocks.AIR;
        ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(block1);
        compound.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("Data", (byte)block1.getMetaFromState(this.block));*/
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        //super.readEntityFromNBT(compound);
        /*this.setInvulTime(compound.getInteger("Invul"));

        if (this.hasCustomName())
        {
            this.bossInfo.setName(this.getDisplayName());
        }*/

        /*int i = compound.getByte("Data") & 255;

        if (compound.hasKey("Block", 8))
        {
            this.block = Block.getBlockFromName(compound.getString("Block")).getStateFromMeta(i);
        }
        else if (compound.hasKey("TileID", 99))
        {
            this.block = Block.getBlockById(compound.getInteger("TileID")).getStateFromMeta(i);
        }
        else
        {
            this.block = Block.getBlockById(compound.getByte("Tile") & 255).getStateFromMeta(i);
        }*/
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if(this.block != null) {
            buffer.writeInt(Block.getStateId(this.block));
        }
        System.out.println("Sending block spawnData");
        /*buffer.writeFloat(velocity.x);
        buffer.writeFloat(velocity.y);
        buffer.writeFloat(velocity.z);*/
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        System.out.println("Received block spawnData");
        block = Block.getStateById(additionalData.readInt());
        System.out.println("Block is " + block.getBlock().getLocalizedName());
        //System.out.println("Block is " + block.getBlock().getLocalizedName());
        /*velocity.x = additionalData.readFloat();
        velocity.y = additionalData.readFloat();
        velocity.z = additionalData.readFloat();*/
        //this.velocity = new Vector3f(additionalData.readFloat(),additionalData.readFloat(),additionalData.readFloat());
        this.xform.origin.set((float)posX,(float)posY,(float)posZ);
        this.setSize(0.98F, 0.98F);

        /*if(this.velocity != null) {
            this.rigidBody.setLinearVelocity(this.velocity);
        }*/
    }
}
