package me.arzyk.physics;

import me.arzyk.physics.entity.PhysicsBlockEntity;
import me.arzyk.physics.event.WorldEventHandler;
import me.arzyk.physics.item.WandItem;
import me.arzyk.physics.network.DataSerializers;
import me.arzyk.physics.world.MinecraftPhysicsWorld;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class Physics implements ModInitializer {

    public static final EntityType<PhysicsBlockEntity> PHYS_BLOCK = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("physics", "phys_block"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, PhysicsBlockEntity::new).dimensions(EntityDimensions.fixed(0.98f, 0.98f)).build()
    );

    public static final Item PHYSICS_WAND = new WandItem(new FabricItemSettings().group(ItemGroup.MISC));

    public static Map<String, MinecraftPhysicsWorld> dynamicWorlds = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register(WorldEventHandler::onWorldLoad);
        ServerWorldEvents.UNLOAD.register(WorldEventHandler::onWorldUnload);
        ServerTickEvents.START_WORLD_TICK.register(WorldEventHandler::onWorldTick);

        Registry.register(Registry.ITEM, new Identifier("physics", "physics_wand"), PHYSICS_WAND);

        DataSerializers.register();
    }

    public static MinecraftPhysicsWorld getPhysicsWorld(String dimensionID) {
        return dynamicWorlds.get(dimensionID);
    }
}
