package me.arzyk.physics.entity;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import me.arzyk.physics.Physics;
import me.arzyk.physics.network.DataSerializers;
import me.arzyk.physics.util.VecUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.List;

public class PhysicsBlockEntity extends Entity {

    static final Vector3f BLOCK_SIZE = new Vector3f(0.5f, 0.5f, 0.5f);
    static final BoxShape BLOCK_SHAPE = new BoxShape(BLOCK_SIZE);
    static final Vector3f BLOCK_OFFSET = new Vector3f(0.5f, 0f, 0.5f);
    public BlockState block;
    @Nullable
    public NbtCompound blockEntityData;
    public boolean shouldSolidify = false;
    public int solidifyTick = 40;
    public RigidBody rigidBody;
    public Transform transform = new Transform();
    public Quat4f physicsRotation = new Quat4f();
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(PhysicsBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    protected static final TrackedData<Quat4f> ROTATION = DataTracker.registerData(PhysicsBlockEntity.class, DataSerializers.QUAT4F);


    public PhysicsBlockEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        this.block = Blocks.SAND.getDefaultState();
        this.ignoreCameraFrustum = true;
        this.intersectionChecked = true;

        if (!world.isClient()) {
            BoxShape localBoxShape = BLOCK_SHAPE;
            Vector3f inertia = new Vector3f();
            localBoxShape.calculateLocalInertia(20, inertia);

            var pos = this.getPos();
            transform.setIdentity();
            transform.origin.set((float) pos.getX() - BLOCK_OFFSET.x, (float) pos.getY() - BLOCK_OFFSET.y, (float) pos.getZ() - BLOCK_OFFSET.z);
            transform.getRotation(this.physicsRotation);
            //System.out.println("constructor");
            setPhysicsBlockRot(this.physicsRotation);

            this.rigidBody = new RigidBody(20, new DefaultMotionState(transform), localBoxShape, inertia);
            this.rigidBody.setRestitution(0.01F);
            this.rigidBody.setFriction(0.8F);
            this.rigidBody.setDamping(0.2F, 0.4F);

            var dimension = world.getDimensionKey().getValue().toString();
            //System.out.println("Spawning block rigidBody in " + dimension);
            Physics.dynamicWorlds.get(dimension).addRigidBody(rigidBody);
            rigidBody.activate();
        }
    }

    private PhysicsBlockEntity(World world, double x, double y, double z, BlockState block) {
        this(Physics.PHYS_BLOCK, world);
        this.block = block;
        this.intersectionChecked = true;
        this.setPosition(x, y, z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setPhysicsBlockPos(this.getBlockPos());
    }

    // This (along with some other parts) was proudly stolen from FallingBlockEntity
    public static PhysicsBlockEntity spawnFromBlock(World world, BlockPos pos, BlockState state) {
        PhysicsBlockEntity physicsBlockEntity = new PhysicsBlockEntity(world, (double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5, state.contains(Properties.WATERLOGGED) ? (BlockState) state.with(Properties.WATERLOGGED, false) : state);
        world.setBlockState(pos, state.getFluidState().getBlockState(), 3);
        world.spawnEntity(physicsBlockEntity);
        return physicsBlockEntity;
    }

    public Quat4f getPhysicsBlockRot() {
        return this.dataTracker.get(ROTATION);
    }

    public void setPhysicsBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
    }

    public void setPhysicsBlockRot(Quat4f rot) {
        this.physicsRotation = rot;
        //System.out.println("Setting rotation to " + rot.x + " " + rot.y + " " + rot.z + " " + rot.w);
        this.dataTracker.set(ROTATION, rot);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(ROTATION, new Quat4f());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.block = NbtHelper.toBlockState(nbt.getCompound("BlockState"));
        NbtList list = nbt.getList("PhysicsRotation", NbtElement.FLOAT_TYPE);
        Quat4f quat4f = new Quat4f(list.getFloat(0), list.getFloat(1), list.getFloat(2), list.getFloat(3));
        if (!Float.isNaN(quat4f.x)) {
            //System.out.println("readCustomDataFromNbt");
            setPhysicsBlockRot(quat4f);
            this.transform.setRotation(this.physicsRotation);
            this.rigidBody.setWorldTransform(this.transform);
        }

        if (this.block.isAir()) {
            this.block = Blocks.SAND.getDefaultState();
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("BlockState", NbtHelper.fromBlockState(this.block));
        nbt.put("PhysicsRotation", toNbtList(physicsRotation.x, physicsRotation.y, physicsRotation.z, physicsRotation.w));
    }

    private BlockPos getPhysicsBlockPos() {
        Vec3d position = getPos().add(0.5f, 1, 0.5f);
        return new BlockPos(MathHelper.floor(position.x), MathHelper.floor(position.y), MathHelper.floor(position.z));
    }

    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!world.isClient) {
            String dimensionID = this.world.getDimensionKey().getValue().toString();
            //System.out.println("Removing rigidBody from dimension " + dimensionID);
            Physics.getPhysicsWorld(dimensionID).removeRigidBody(this.rigidBody);
        }
    }

    public void tick() {
        super.tick();
        if (block.isAir()) {
            this.discard();
        } else if (!world.isClient()) {
            Vector3f position;
            Quat4f rotation = new Quat4f();
            Vector3f vel = new Vector3f();
            this.rigidBody.getWorldTransform(this.transform);
            this.rigidBody.getOrientation(rotation);
            position = this.transform.origin;
            this.rigidBody.getAngularVelocity(vel);


            this.setVelocity(vel.x, vel.y, vel.z);

            this.setPhysicsBlockRot(rotation);
            position.add(new Vector3f(BLOCK_OFFSET.x,BLOCK_OFFSET.y,BLOCK_OFFSET.z));
            this.setPositionInternal(position.x, position.y, position.z);

            if(shouldSolidify && (!this.rigidBody.isActive() || world.getBlockState(this.getPhysicsBlockPos()).isSolidBlock(world, this.getPhysicsBlockPos()))) {
                solidifyTick--;
                if(solidifyTick <= 0) {
                    placeBlock();
                }
            }
            else {
                solidifyTick = 40;
            }
        } else {
            //this.move(MovementType.SELF, this.getVelocity());
            this.onGround = false;
            this.physicsRotation = getPhysicsBlockRot();
        }

        this.prevX = this.getPos().x;
        this.prevY = this.getPos().y;
        this.prevZ = this.getPos().z;

        this.checkBlockCollision();
        List<Entity> list = this.world.getOtherEntities(this, this.getCollisionBoundingBox(), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                this.pushAwayFrom(entity);
            }
        }
    }

    public Box getCollisionBoundingBox() {
        return super.getBoundingBox().offset(0.5f, 0.0f, 0.5f);
    }

    boolean shouldUpdatePosition(Vec3d newPos) {
        System.out.println(this.getPos());
        System.out.println(newPos);
        boolean shouldUpdate = !newPos.isInRange(this.getPos(), 0.1d);
        System.out.println("Should update position: " + shouldUpdate);
        return shouldUpdate;
    }

    void placeBlock() {
        /*if(this.world.canSetBlock(this.getPhysicsBlockPos())) {
            this.world.setBlockState(this.getPhysicsBlockPos(), this.getBlockState());
        }
        else {*/
            this.dropItem(block.getBlock());
        //}
        this.discard();
    }

    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable());
    }

    public boolean isCollidable() {
        return true;
    }

    public Vector3f renderPosition = new Vector3f();
    public Quat4f renderRotation = new Quat4f();

    public void interpolate() {
        final float interp = 0.15f;
        Vector3f newPos = VecUtils.toVector3f(new Vec3d(this.getPos().x, this.getBoundingBox().maxY - 1d, this.getPos().z));
        if(!isWithinDistance(this.renderPosition, newPos, 10)) {
            this.renderPosition = newPos;
        }
        this.renderPosition.interpolate(newPos, interp);
        this.renderRotation.interpolate(physicsRotation, interp);
    }

    void setPositionInternal(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);

        if (!world.isClient() && this.transform != null) {
            rigidBody.getWorldTransform(this.transform);
            //System.out.println("Setting physBlock position to " + x + " " + y + " " + z);
            transform.origin.set((float) x - BLOCK_OFFSET.x, (float) y - BLOCK_OFFSET.y, (float) z - BLOCK_OFFSET.z);
            if (rigidBody != null) {
                this.rigidBody.setWorldTransform(transform);
            }
        }
    }

    protected void refreshPosition() {
        super.refreshPosition();
        if(!world.isClient)
            this.rigidBody.activate();
    }

    boolean isWithinDistance(Vector3f pos1, Vector3f pos2, float dist) {
        /*float xDist = Math.abs(pos1.x - pos2.x);
        //System.out.println("X Distance between " + pos1.x + " and " + pos2.x + " is " + xDist);
        float yDist = Math.abs(pos1.y - pos2.y);
        //System.out.println("Y Distance between " + pos1.y + " and " + pos2.y + " is " + yDist);
        float zDist = Math.abs(pos1.z - pos2.z);
        //System.out.println("Z Distance between " + pos1.z + " and " + pos2.z + " is " + zDist);
        return (xDist < dist) && (yDist < dist) && (zDist < dist);*/
        Vec3d position1 = VecUtils.toVec3d(pos1);
        Vec3d position2 = VecUtils.toVec3d(pos2);
        return position1.isInRange(position2, dist);
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
        this.setPhysicsBlockPos(this.getBlockPos());
    }
}
