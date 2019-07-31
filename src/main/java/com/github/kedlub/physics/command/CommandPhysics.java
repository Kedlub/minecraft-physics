package com.github.kedlub.physics.command;

import com.github.kedlub.physics.PhysicsMod;
import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * Created by Kubik on 10.12.2017.
 */
public class CommandPhysics extends CommandBase {

    @Override
    public String getName() {
        return "physics";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/physics help to see command list";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args[0] == "remove") {
            for (Object o : sender.getEntityWorld().loadedEntityList) {
                if (o instanceof EntityPhysicsBlock) {
                    EntityPhysicsBlock block = (EntityPhysicsBlock) o;
                    block.setDead();
                }
            }
        }
        else if(args[0] == "spawn") {
            World worldObj = sender.getEntityWorld();
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            EntityPhysicsBlock pb = new EntityPhysicsBlock(worldObj, (float) player.posX - 0.5f, (float) player.posY - 2f, (float) player.posZ - 0.5f, 0, 0, 0, Blocks.STONE.getDefaultState());
            //pb.setPosition(player.posX,player.posY,player.posZ);
            pb.forceSpawn = true;
            worldObj.spawnEntity(pb);
        }
    }
}
