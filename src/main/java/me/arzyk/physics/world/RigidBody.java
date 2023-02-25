package me.arzyk.physics.world;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public abstract class RigidBody {
    public abstract void activate();

    public abstract boolean isActive();

    public abstract void setPosition(Vector3f vector3f);

    public abstract void setPosition(float x, float y, float z);

    public abstract void setRotation(Quat4f quat4f);

    public abstract void setRotation(float x, float y, float z, float w);

    public abstract Vector3f getPosition();

    public abstract Quat4f getRotation();

    public abstract void setFriction(float friction);

    public abstract void setRestitution(float restitution);

    public abstract void setDamping(float linear, float angular);
    public abstract void setLinearVelocity(Vector3f velocity);

    public abstract void destroy();

    public abstract void setMass(float _mass);
}
