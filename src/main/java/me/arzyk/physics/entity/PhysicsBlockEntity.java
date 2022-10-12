package me.arzyk.physics.entity;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import me.arzyk.physics.Physics;
import me.arzyk.physics.network.DataSerializers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PhysicsBlockEntity extends Entity {

    public BlockState block;
    @Nullable
    public NbtCompound blockEntityData;
    public RigidBody rigidBody;
    public Transform transform = new Transform();
    public Quat4f physicsRotation = new Quat4f();
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(PhysicsBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    protected static final TrackedData<Quat4f> ROTATION = DataTracker.registerData(PhysicsBlockEntity.class, DataSerializers.QUAT4F);


    public PhysicsBlockEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        this.block = Blocks.SAND.getDefaultState();

        if(!world.isClient()) {
            this.rigidBody = new RigidBody(1, new DefaultMotionState(), new BoxShape(new Vector3f(0.5f,0.5f,0.5f)));
            transform = new Transform();
            var pos = this.getPos();
            transform.origin.set((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
            this.rigidBody.setCenterOfMassTransform(transform);
            var dimension = world.getDimensionKey().getValue().toString();
            System.out.println("Spawning block rigidbody in " + dimension);
            Physics.dynamicWorlds.get(dimension).addRigidBody(rigidBody);
            rigidBody.activate();
        }
    }



    public BlockPos getBlockPos() {
        return this.dataTracker.get(BLOCK_POS);
    }
    public Quat4f getPhysicsBlockRot() {
        return this.dataTracker.get(ROTATION);
    }
    public void setBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
    }
    public void setPhysicsBlockRot(Quat4f rot) {
        this.dataTracker.set(ROTATION, rot);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(ROTATION, new Quat4f());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public void tick() {
        super.tick();
        if(!world.isClient()) {
            Vector3f position = new Vector3f();
            Quat4f rotation = new Quat4f();
            Vector3f vel = new Vector3f();
            rigidBody.getCenterOfMassPosition(position);
            rigidBody.getOrientation(rotation);
            rigidBody.getAngularVelocity(vel);

            this.physicsRotation = rotation;
            this.setVelocity(vel.x,vel.y,vel.z);

            this.dataTracker.set(ROTATION, physicsRotation);
            this.setPositionInternal(position.x,position.y,position.z);
        }
        else {
            this.physicsRotation = getPhysicsBlockRot();
        }
    }

    void setPositionInternal(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);

        if(!world.isClient() && this.transform != null) {
            System.out.println("Setting physBlock position to " + x + " " + y + " " + z);
            transform.origin.set((float) x, (float) y, (float) z);
            if(rigidBody != null) {
                this.rigidBody.setCenterOfMassTransform(transform);
            }
        }
    }

    public BlockState getBlockState() {
        return this.block;
    }

    public Quat4f getRotation() {
        return this.physicsRotation;
    }

    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.block = Block.getStateFromRawId(packet.getEntityData());
        this.intersectionChecked = true;
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.setPosition(d, e, f);
        this.setBlockPos(this.getBlockPos());
        this.setPhysicsBlockRot(this.getPhysicsBlockRot());
    }
}
