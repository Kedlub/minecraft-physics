package me.arzyk.physics;

import me.arzyk.physics.entity.PhysicsBlockEntity;
import me.arzyk.physics.event.WorldEventHandler;
import me.arzyk.physics.network.DataSerializers;
import me.arzyk.physics.world.MinecraftPhysicsWorld;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class Physics implements ModInitializer {

    public static final EntityType<PhysicsBlockEntity> PHYS_BLOCK = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("physics", "phys_block"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, PhysicsBlockEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );

    public static Map<String, MinecraftPhysicsWorld> dynamicWorlds = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register(WorldEventHandler::onWorldLoad);
        ServerWorldEvents.UNLOAD.register(WorldEventHandler::onWorldUnload);
        ServerTickEvents.START_WORLD_TICK.register(WorldEventHandler::onWorldTick);

        DataSerializers.register();
    }

    public static MinecraftPhysicsWorld getPhysicsWorld(String dimensionID) {
        return dynamicWorlds.get(dimensionID);
    }
}
