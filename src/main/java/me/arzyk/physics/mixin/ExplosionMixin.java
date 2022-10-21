package me.arzyk.physics.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.arzyk.physics.entity.PhysicsBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.vecmath.Vector3f;
import java.util.Random;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final private World world;

    @Inject(method = "affectWorld(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void affectWorld(boolean particles, CallbackInfo ci, boolean bl, ObjectArrayList objectArrayList, boolean bl2, ObjectListIterator var5, BlockPos blockPos, BlockState blockState) {
        if(!blockState.isAir()) {
            if(!world.isClient()) {
                PhysicsBlockEntity entity = PhysicsBlockEntity.spawnFromBlock(world, blockPos, blockState);
                entity.rigidBody.setLinearVelocity(new Vector3f(0, new Random().nextInt(30), 0));
            }
        }
    }


}