package me.arzyk.physics.world;

import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import me.arzyk.physics.Physics;
import me.arzyk.physics.world.shapes.Shape;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxRigidBodyExt;
import physx.physics.PxActorFlagEnum;
import physx.physics.PxPhysics;
import physx.physics.PxRigidBody;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class RigidBodyOld {
    private com.bulletphysics.dynamics.RigidBody _bulletRigidBody;
    // Maybe use PxRigidDynamic?
    private PxRigidBody _physxRigidBody;
    private Vector3f _positionOffset = new Vector3f(0, 0, 0);
    private float _mass;

    public RigidBodyOld(MinecraftPhysicsWorld physWorld, float mass) {
        this._mass = mass;
        if(Physics.instance.activeEngine == PhysicsEngine.BULLET) {
            _positionOffset = new Vector3f(0.5f, 0.5f, 0.5f);
        }
    }

    public RigidBodyOld(MinecraftPhysicsWorld physWorld, float mass, Shape shape) {
        this(physWorld, mass);
        if(Physics.instance.activeEngine == PhysicsEngine.BULLET) {
            _bulletRigidBody = new com.bulletphysics.dynamics.RigidBody(mass, new DefaultMotionState(), shape.toBullet());
            physWorld.addRigidBody(this);
        } else if(Physics.instance.activeEngine == PhysicsEngine.PHYSX) {
            _physxRigidBody = ((PxPhysics)physWorld.getNativeWorld()).createRigidDynamic(new PxTransform(new PxVec3(0, 0, 0), new PxQuat(0, 0, 0, 1)));
            PxRigidBodyExt.setMassAndUpdateInertia(_physxRigidBody, mass);
            _physxRigidBody.setActorFlag(PxActorFlagEnum.eDISABLE_GRAVITY, false);
            physWorld.addRigidBody(this);
        }
    }

    public void activate() {
        if(Physics.instance.activeEngine == PhysicsEngine.BULLET) {
            _bulletRigidBody.activate();
        } else if(Physics.instance.activeEngine == PhysicsEngine.PHYSX) {
            _physxRigidBody.setActorFlag(PxActorFlagEnum.eDISABLE_SIMULATION, false);
        }
    }

    public boolean isActive() {
        if(Physics.instance.activeEngine == PhysicsEngine.BULLET) {
            return _bulletRigidBody.isActive();
        } else if(Physics.instance.activeEngine == PhysicsEngine.PHYSX) {
            return !_physxRigidBody.getActorFlags().isSet(PxActorFlagEnum.eDISABLE_SIMULATION);
        }
        return false;
    }

    public void setPosition(Vector3f vector3f) {
        this.setPosition(vector3f.x, vector3f.y, vector3f.z);
    }

    public void setPosition(float x, float y, float z) {
        if(_bulletRigidBody != null) {
            _bulletRigidBody.setWorldTransform(new com.bulletphysics.linearmath.Transform(new javax.vecmath.Matrix4f(new javax.vecmath.Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(x, y, z), 1)));
        }
        if(_physxRigidBody != null) {
            Quat4f rot = getRotation();
            _physxRigidBody.setGlobalPose(new PxTransform(new PxVec3(x, y, z), new PxQuat(rot.x, rot.y, rot.z, rot.w)));
        }
    }

    public void setRotation(Quat4f quat4f) {
        this.setRotation(quat4f.x, quat4f.y, quat4f.z, quat4f.w);
    }

    public void setRotation(float x, float y, float z, float w) {
        if(_bulletRigidBody != null) {
            _bulletRigidBody.setWorldTransform(new com.bulletphysics.linearmath.Transform(new javax.vecmath.Matrix4f(new javax.vecmath.Quat4f(x, y, z, w), getPosition(), 1)));
        }
        if(_physxRigidBody != null) {
            Vector3f pos = getPosition();
            _physxRigidBody.setGlobalPose(new PxTransform(new PxVec3(pos.x,pos.y,pos.z), new PxQuat(x, y, z, w)));
        }
    }

    public Vector3f getPosition() {
        if(_bulletRigidBody != null) {
            return new Vector3f(_bulletRigidBody.getWorldTransform(new com.bulletphysics.linearmath.Transform()).origin);
        }
        if(_physxRigidBody != null) {
            return new Vector3f(_physxRigidBody.getGlobalPose().getP().getX(), _physxRigidBody.getGlobalPose().getP().getY(), _physxRigidBody.getGlobalPose().getP().getZ());
        }
        return new Vector3f(0, 0, 0);
    }

    public Quat4f getRotation() {
        if(_bulletRigidBody != null) {
            Quat4f quat4f = new Quat4f();
            Transform transform = new Transform();
            _bulletRigidBody.getWorldTransform(transform);
            transform.getRotation(quat4f);
            return quat4f;
        }
        if(_physxRigidBody != null) {
            return new Quat4f(_physxRigidBody.getGlobalPose().getQ().getX(), _physxRigidBody.getGlobalPose().getQ().getY(), _physxRigidBody.getGlobalPose().getQ().getZ(), _physxRigidBody.getGlobalPose().getQ().getW());
        }
        return new Quat4f(0, 0, 0, 1);
    }

    public void setFriction(float friction) {
        if(_bulletRigidBody != null) {
            _bulletRigidBody.setFriction(friction);
        }
    }

    public void setRestitution(float restitution) {
        if(_bulletRigidBody != null) {
            _bulletRigidBody.setRestitution(restitution);
        }
        if(_physxRigidBody != null) {
            // PhysX doesn't have these settings?
        }
    }

    public void setDamping(float linear, float angular) {
        if(_bulletRigidBody != null) {
            _bulletRigidBody.setDamping(linear, angular);
        }
        if(_physxRigidBody != null) {
            _physxRigidBody.setAngularDamping(angular);
            _physxRigidBody.setLinearDamping(linear);
        }
    }

    public void destroy() {
        if(_bulletRigidBody != null) {
            _bulletRigidBody.destroy();
        }
        if(_physxRigidBody != null) {
            _physxRigidBody.release();
        }
    }

    public void setMass(float _mass) {
        this._mass = _mass;
        if(_bulletRigidBody != null) {
            _bulletRigidBody.setMassProps(_mass, new javax.vecmath.Vector3f(0, 0, 0));
        }
        if(_physxRigidBody != null) {
            _physxRigidBody.setMass(_mass);
        }
    }
}
