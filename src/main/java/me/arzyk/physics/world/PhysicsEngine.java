package me.arzyk.physics.world;

import me.arzyk.physics.world.jbullet.JavaPhysicsWorld;
import me.arzyk.physics.world.physx.PhysXPhysicsWorld;

public enum PhysicsEngine {
    BULLET("Bullet", JavaPhysicsWorld.class),
    PHYSX("PhysX", PhysXPhysicsWorld.class);

    private String name;
    private Class<? extends MinecraftPhysicsWorld> clazz;

    PhysicsEngine(String name, Class<? extends MinecraftPhysicsWorld> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<? extends MinecraftPhysicsWorld> getClazz() {
        return clazz;
    }
}
