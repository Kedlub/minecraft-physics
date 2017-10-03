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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3f;

import static com.github.kedlub.physics.PhysicsMod.blocks;

/**
 * Created by Kubik on 08.06.2017.
 */
public class EntityPhysicsBlock extends Entity {

    public RigidBody rigidBody;
    public int number;
    public Transform xform;
    public ModelCube model;
    public Vector3f velocity;
    public IBlockState block;

    public EntityPhysicsBlock(World worldIn) {
        super(worldIn);



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


        PhysicsMod.instance.dynamicsWorld.addRigidBody(this.rigidBody);


        //number = blocks.length + 1;
        //blocks[blocks.length + 1] = this;
        //System.out.println("Spawned block!");
        blocks.add(this);

        this.velocity = new Vector3f(0.0F, 0.0F, 0.0F);
    }

    public void init(float x, float y, float z, float velX, float velY, float velZ, IBlockState block1) {
        block = block1;
        this.xform.origin.set(x,y,z);

        this.velocity.x = ((float)velX);
        this.velocity.y = ((float)velY);
        this.velocity.z = ((float)velZ);

        this.rigidBody.setCenterOfMassTransform(xform);
        this.rigidBody.setLinearVelocity(this.velocity);
        this.rigidBody.activate();
    }

    public void display() {
        GL11.glTranslated(posX,posY,posZ);
        model.render(this,0,0,0,0,0,1);
    }

    public void update() {
        //System.out.println("Previous position:" + posX + " " + posY + " " + posZ);
        this.xform = this.rigidBody.getCenterOfMassTransform(this.xform);
        this.posX = xform.origin.x;
        this.posY = xform.origin.y;
        this.posZ = xform.origin.z;

        this.rigidBody.getLinearVelocity(this.velocity);
        //System.out.println("New position:" + posX + " " + posY + " " + posZ);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void kill() {
        this.setDead();
    }

    @Override
    public void setDead() {
        PhysicsMod.instance.dynamicsWorld.removeRigidBody(this.rigidBody);
        blocks.remove(this);
        this.isDead = true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }
}
