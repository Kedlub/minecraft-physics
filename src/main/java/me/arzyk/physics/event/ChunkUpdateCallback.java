package me.arzyk.physics.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkUpdateCallback {
    Event<ChunkUpdateCallback> EVENT = EventFactory.createArrayBacked(ChunkUpdateCallback.class,
            (listeners) -> (chunk) -> {
                for (ChunkUpdateCallback listener : listeners) {
                    ActionResult result = listener.update(chunk);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult update(WorldChunk chunk);
}
