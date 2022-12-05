package me.arzyk.physics.world;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class PhysicsChunk {
    public int x;
    public int z;
    public Map<BlockPos, Boolean> solidBlocks = new HashMap<>();

    public PhysicsChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }
}
