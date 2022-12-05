package me.arzyk.physics.item;

import me.arzyk.physics.entity.PhysicsBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class WandItem extends Item {

    static final int MAX_BLOCK_COUNT = 100;

    public WandItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();

        if(!world.isClient()) {
            BlockState block = world.getBlockState(blockPos);
            awakenBlocks(world, blockPos, block);
        }

        blockPosList.clear();
        blockCount = 0;

        return ActionResult.success(world.isClient);
    }

    List<BlockPos> blockPosList = new ArrayList<>();
    int blockCount = 0;

    public void awakenBlocks(World world, BlockPos blockPos, BlockState block) {
        blockCount++;
        if(blockCount > MAX_BLOCK_COUNT)
            return;

        blockPosList.add(blockPos);
        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = blockPos.offset(direction);
            if(world.getBlockState(offsetPos).getBlock().equals(block.getBlock()) && !blockPosList.contains(offsetPos)) {
                awakenBlocks(world, offsetPos, block);
            }
        }
        PhysicsBlockEntity.spawnFromBlock(world, blockPos, block);
    }
}