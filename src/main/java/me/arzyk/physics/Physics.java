package me.arzyk.physics;

import me.arzyk.physics.config.SimpleConfig;
import me.arzyk.physics.entity.PhysicsBlockEntity;
import me.arzyk.physics.event.WorldEventHandler;
import me.arzyk.physics.item.WandItem;
import me.arzyk.physics.network.DataSerializers;
import me.arzyk.physics.world.MinecraftPhysicsWorld;
import me.arzyk.physics.world.PhysicsEngine;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Physics implements ModInitializer {
    public static final String MOD_ID = "physics";
    public static final Logger LOGGER = LoggerFactory.getLogger("Physics");
    public static Physics instance;
    public static final EntityType<PhysicsBlockEntity> PHYS_BLOCK = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("physics", "phys_block"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, PhysicsBlockEntity::new).dimensions(EntityDimensions.fixed(0.98f, 0.98f)).build()
    );

    public static final Item PHYSICS_WAND = new WandItem(new FabricItemSettings().group(ItemGroup.MISC));
    public SimpleConfig CONFIG = SimpleConfig.of( "config" ).provider(this::configProvider).request();
    public PhysicsEngine activeEngine;
    public int maxPhysicsBlocks = 1000;
    public Map<String, MinecraftPhysicsWorld> dynamicWorlds = new HashMap<>();

    @Override
    public void onInitialize() {
        instance = this;
        activeEngine = PhysicsEngine.valueOf(CONFIG.getOrDefault("physics_engine", "bullet").toUpperCase());
        maxPhysicsBlocks = CONFIG.getOrDefault("max_physics_blocks", 1000);

        if(activeEngine == PhysicsEngine.PHYSX) {
            // Initialize PhysX JNI library
            // Loader.load();
            LOGGER.warn("PhysX is not yet supported!");
            activeEngine = PhysicsEngine.BULLET;
        }

        ServerWorldEvents.LOAD.register(WorldEventHandler::onWorldLoad);
        ServerWorldEvents.UNLOAD.register(WorldEventHandler::onWorldUnload);
        ServerTickEvents.START_WORLD_TICK.register(WorldEventHandler::onWorldTick);
        PlayerBlockBreakEvents.AFTER.register(WorldEventHandler::afterBlockBreak);

        Registry.register(Registry.ITEM, new Identifier("physics", "physics_wand"), PHYSICS_WAND);

        DataSerializers.register();
    }

    public String configProvider(String fileName) {
        return "# Active physics engine; bullet - java port of the Bullet engine, slow; physx - native physics library, faster\n" +
                "physics_engine=bullet\n" +
                "\n" +
                "# Max physics blocks\n" +
                "max_physics_blocks=1000\n";
    }

    public MinecraftPhysicsWorld getPhysicsWorld(String dimensionID) {
        return dynamicWorlds.get(dimensionID);
    }
}
