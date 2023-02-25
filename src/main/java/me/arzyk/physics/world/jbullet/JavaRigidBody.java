package me.arzyk.physics.world.jbullet;

import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import me.arzyk.physics.world.RigidBody;
import me.arzyk.physics.world.shapes.Shape;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class JavaRigidBody extends RigidBody {
    final com.bulletphysics.dynamics.RigidBody body;
    private final Transform transform;
    private final JavaPhysicsWorld world;

    public JavaRigidBody(JavaPhysicsWorld world, Shape shape) {
        super();
        this.world = world;
        this.transform = new Transform();
        this.transform.setIdentity();
        this.body = new com.bulletphysics.dynamics.RigidBody(0, new DefaultMotionState(transform), shape.toBullet());
        world.addRigidBody(this);
    }

    @Override
    public void activate() {
        body.activate();
    }

    @Override
    public boolean isActive() {
        return body.isActive();
    }

    @Override
    public void setPosition(Vector3f vector3f) {
        Quat4f rotation = getRotation();
        transform.setIdentity();
        transform.origin.set(vector3f);
        transform.setRotation(rotation);
        body.setWorldTransform(transform);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x, y, z));
    }

    @Override
    public void setRotation(Quat4f quat4f) {
        Vector3f position = getPosition();
        transform.setIdentity();
        transform.origin.set(position);
        transform.setRotation(quat4f);
        body.setWorldTransform(transform);
    }

    @Override
    public void setRotation(float x, float y, float z, float w) {
        setRotation(new Quat4f(x, y, z, w));
    }

    @Override
    public Vector3f getPosition() {
        return this.body.getCenterOfMassPosition(new Vector3f());
    }

    @Override
    public Quat4f getRotation() {
        return this.body.getOrientation(new Quat4f());
    }

    @Override
    public void setFriction(float friction) {
        body.setFriction(friction);
    }

    @Override
    public void setRestitution(float restitution) {
        body.setRestitution(restitution);
    }

    @Override
    public void setDamping(float linear, float angular) {
        body.setDamping(linear, angular);
    }

    @Override
    public void setLinearVelocity(Vector3f velocity) {
        body.setLinearVelocity(velocity);
    }

    @Override
    public void destroy() {
        world.removeRigidBody(this);
        body.destroy();
    }

    @Override
    public void setMass(float _mass) {
        body.setMassProps(_mass, body.getCollisionShape().getLocalScaling(new Vector3f()));
    }
}
