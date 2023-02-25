package me.arzyk.physics.world.shapes;

import physx.geomutils.PxBoxGeometry;

import javax.vecmath.Vector3f;

public class BoxShape extends Shape {
    private Vector3f _size;
    public BoxShape(float halfExtents) {
        this(new Vector3f(halfExtents, halfExtents, halfExtents));
    }
    public BoxShape(Vector3f halfExtents) {
        _size = halfExtents;
    }

    public Vector3f getSize() {
        return _size;
    }

    public com.bulletphysics.collision.shapes.BoxShape toBullet() {
        return new com.bulletphysics.collision.shapes.BoxShape(_size);
    }

    public PxBoxGeometry toPhysX() {
        return new PxBoxGeometry(_size.x, _size.y, _size.z);
    }
}
