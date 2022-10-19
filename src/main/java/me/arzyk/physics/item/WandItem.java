package me.arzyk.physics.item;

import me.arzyk.physics.entity.PhysicsBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class WandItem extends Item {

    public WandItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        if(!world.isClient()) {
            BlockState block = world.getBlockState(blockPos);
            PhysicsBlockEntity.spawnFromBlock(world, blockPos, block);
        }

        return ActionResult.success(world.isClient);
    }
}