package me.arzyk.physics.mixin;

import me.arzyk.physics.Physics;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class ChunkUpdateMixin {
    @Shadow @Final private World world;

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;", at = @At(value = "TAIL"))
    private void onUpdate(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        if(!world.isClient()) {
            WorldChunk chunk = (WorldChunk) (Object) this;
            var physWorld = Physics.dynamicWorlds.get(world.getDimensionKey().getValue().toString());
            physWorld.updateChunkCache(chunk.getPos().x, chunk.getPos().z);
        }
    }
}
