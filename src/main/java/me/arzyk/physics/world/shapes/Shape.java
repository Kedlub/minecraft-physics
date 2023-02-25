package me.arzyk.physics.world.shapes;

public abstract class Shape {
    public abstract com.bulletphysics.collision.shapes.CollisionShape toBullet();
    public abstract physx.geomutils.PxGeometry toPhysX();
}
