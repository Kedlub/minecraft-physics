package com.github.kedlub.physics;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.github.kedlub.physics.entity.EntityPhysicsBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.processing.SupportedSourceVersion;
import javax.vecmath.Vector3f;

import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 * Created by Kubik on 08.06.2017.
 */
public class EventManager {

    //RigidBody playerRigidBody;
    //Transform playerXForm;

    @SubscribeEvent
    public void OnWorldLoad(WorldEvent.Load e) {

    }

    @SubscribeEvent
    public void OnWorldUnload(WorldEvent.Unload e) {
        /*for(Iterator< EntityPhysicsBlock> i = PhysicsMod.blocks.iterator(); i.hasNext();){
            EntityPhysicsBlock block = i.next();
            block.setDead();
        }*/

        //PhysicsMod.instance.dynamicsWorld.removeRigidBody(playerRigidBody);
        /*playerRigidBody = null;
        playerXForm = null;*/
    }

    /*public void OnChunkLoad(ChunkEvent.Load event) {
        Chunk chunk =  event.getChunk();

        PhysicsMod.instance.addStaticChunk(new Vector3f((float)chunk.xPosition,0,(float)chunk.zPosition), chunk.);
    }*/

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        //if(Minecraft.getMinecraft().world == null) return;
        /*EntityPlayerSP ply = Minecraft.getMinecraft().player;
        if(playerRigidBody == null && ply != null) {

            playerXForm = new Transform();
            playerXForm.setIdentity();
            playerXForm.origin.set((float)ply.posX,(float)ply.posY,(float)ply.posZ);
            playerRigidBody = new RigidBody(20, new DefaultMotionState(),new BoxShape(new Vector3f(0.5f,1f,0.5f)));
            playerRigidBody.setCenterOfMassTransform(playerXForm);
            playerRigidBody.activate();

            PhysicsMod.instance.dynamicsWorld.addRigidBody(playerRigidBody);
        }*/


        if(PhysicsMod.ready == 0 && !PhysicsMod.paused) {
            try {
                for (Iterator<EntityPhysicsBlock> i = PhysicsMod.blocks.iterator(); i.hasNext(); ) {
                    EntityPhysicsBlock block = i.next();
                    block.update();
                }

                //EntityPlayerSP ply = Minecraft.getMinecraft().player;
        /*if(playerRigidBody != null && playerXForm != null) {
            playerXForm.origin.set((float) ply.posX, (float) ply.posY, (float) ply.posZ);
            playerRigidBody.setCenterOfMassTransform(playerXForm);
        }*/
                //if(PhysicsMod.ready == 1) {
                PhysicsMod.instance.dynamicsWorld.stepSimulation(0.026666668F);
            }
            catch (Exception e) {
                for (int i = PhysicsMod.blocks.size() - 1; i != -1; i--) {
                    EntityPhysicsBlock block = PhysicsMod.blocks.get(i);
                    block.setDead();
                }

                PhysicsMod.instance.infobox.displayInfo(I18n.format("physics.physicsfail.text"));
            }
        }
        else if(PhysicsMod.ready > 0) {
            PhysicsMod.ready -= 1;
        }
        //}
        //PhysicsMod.instance.dynamicsWorld.stepSimulation(System.nanoTime());
    }


    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate e) {
        PhysicsMod.ready += 5;
        List<BlockPos> pos = e.getExplosion().getAffectedBlockPositions();
        World worldObj = e.getWorld();

        for(Iterator< BlockPos> i = pos.iterator(); i.hasNext();){
            BlockPos block = i.next();
            IBlockState blockState = worldObj.getBlockState(block);

            if(blockState.isFullBlock()) {
                EntityPhysicsBlock pb = new EntityPhysicsBlock(worldObj);
                //pb.setPosition(player.posX,player.posY,player.posZ);
                pb.init((float) block.getX(), (float) block.getY(), (float) block.getZ(), 0, new Random().nextInt(30), 0, blockState);
                pb.forceSpawn = true;
            }
        }

        //PhysicsMod.ready = 1;
    }

    @SubscribeEvent
    public void blockDestroy(BlockEvent.BreakEvent event) {
        BlockPos pos = event.getPos();
        float x = pos.getX();
        float y = pos.getY();
        float z = pos.getZ();
        AxisAlignedBB scanAbove = new AxisAlignedBB(x + 3, y + 3, z + 3, x - 3, y - 3, z - 3);

        // Find entities above this tile entity.
        List entities = event.getWorld().getEntitiesWithinAABB(EntityPhysicsBlock.class, scanAbove);

        for (Object ob: entities
             ) {
            EntityPhysicsBlock pb = (EntityPhysicsBlock) ob;
            pb.rigidBody.activate();
        }
    }

    @SubscribeEvent
    public void drawHud(TickEvent.RenderTickEvent event) {

        PhysicsMod.instance.infobox.updateAchievementWindow();
    }

    String str;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if(event.getGui() instanceof GuiMainMenu) {
            if(PhysicsMod.instance.updateShown) return;
            try {
                if(str == null) {
                    System.out.println("Getting url...");
                    URL url = new URL("http://kedlub.thats.im/mod/physicsmod.txt");
                    str = url.openStream().toString();
                }

                if(str != "" && str != PhysicsMod.VERSION.toString()) {
                    System.out.println("Creating notification");
                    //sleep(500);
                    PhysicsMod.instance.infobox.displayInfoTitle("Physics Mod", I18n.format("physics.newversion.text"),0);
                    PhysicsMod.instance.updateShown = true;
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            //PhysicsMod.instance.updateInfobox();
        }
    }


    @SubscribeEvent
    public void drawHudText(RenderGameOverlayEvent.Text event) {


        if(!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            //Minecraft.getMinecraft().fontRendererObj.drawString("Realistic Physics by kedlub", 0, 0, 0xffFFFFFF);
            Minecraft.getMinecraft().fontRendererObj.drawString("Rigidbody Count: " + PhysicsMod.blocks.size(), 5, 5, 0xffFFFFFF);
        }
    }

    @SubscribeEvent
    public void changeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(event.toDim == -26 || event.toDim == -27) {
            PhysicsMod.instance.dynamicsWorld.setGravity(new Vector3f(0,0,0));
        }
        else if(event.toDim == -28) {
            PhysicsMod.instance.dynamicsWorld.setGravity(new Vector3f(0,-5,0));
        }
        else if(event.toDim == 2) {
            PhysicsMod.instance.dynamicsWorld.setGravity(new Vector3f(0,0,0));
        }
        else {
            PhysicsMod.instance.dynamicsWorld.setGravity(new Vector3f(0,-10,0));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRenderPre(RenderWorldLastEvent event)
    {

        //EntityPlayer player = Minecraft.func_71410_x().field_71439_g;

        //Field f = Minecraft.getMinecraft().getClass().getDeclaredField("renderViewEntity");

        /*Entity entity = Minecraft.getMinecraft().player;
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX);
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY);
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ);

        GL11.glPushMatrix();
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
        RenderHelper.enableStandardItemLighting();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glTranslated(-x, -y, -z);
        for (int i = 0; i < PhysicsMod.blocks.size(); i++)
        {
            EntityPhysicsBlock block = (EntityPhysicsBlock) PhysicsMod.blocks.get(i);
            block.display();
        }
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GL11.glPopMatrix();*/

        //PhysicsMod.instance.dynamicsWorld.debugDrawWorld();




        for (int i = 0; i < PhysicsMod.blocks.size(); i++)
        {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            Vec3d pos = Minecraft.getMinecraft().player.getPositionVector();
            Entity entity = Minecraft.getMinecraft().player;
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * Minecraft.getMinecraft().getRenderPartialTicks();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * Minecraft.getMinecraft().getRenderPartialTicks();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * Minecraft.getMinecraft().getRenderPartialTicks();
            //because of the way 3D rendering is done, all coordinates are relative to the camera.  This "resets" the "0,0,0" position to the location that is (0,0,0) in the world.
            GL11.glTranslated(-x, -y, -z);
            GL11.glDisable(GL11.GL_LIGHTING);
            //GL11.glDisable(GL11.GL_TEXTURE_2D);
            //you will need to supply your own position vectors
            //drawLineWithGL(pos1, pos2);
            float[] matrix = new float[16];
            FloatBuffer transformationBuffer = BufferUtils.createFloatBuffer(16);
            EntityPhysicsBlock block = (EntityPhysicsBlock) PhysicsMod.blocks.get(i);
            Vector3f rot = block.rigidBody.getAngularFactor();
            //GL11.glRotatef(1,rot.x,rot.y,rot.z);
            /*GL11.glRotatef(rot.x,1,0,0);
            GL11.glRotatef(rot.y,0,1,0);
            GL11.glRotatef(rot.z,0,0,1);*/
            GL11.glTranslatef(0.5f,0.5f,0.5f);
            block.xform.getOpenGLMatrix(matrix);
            //GL11.glTranslated(block.posX - 0.5, block.posY, block.posZ - 0.5);


            transformationBuffer.clear();
            transformationBuffer.put(matrix);
            transformationBuffer.flip();

            GL11.glPushMatrix(); // Save the current OpenGL transformation
            GL11.glMultMatrix(transformationBuffer); // Apply the object transformation

            /*for(int a = 0; a<lineList.size(); a+=2){
                drawLineWithGL(lineList.get(a), lineList.get(a+1));
            }*/
            //block.display();



            float radius = 0.5f;
            float posX = 0;
            float posY = 0f;
            float posZ = 0;

           // Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft", "textures/blocks/stone.png"));
            //GL11.glBindTexture(GL11.GL_TEXTURE_2D,);

            //RenderHelper.enableStandardItemLighting();

            //Minecraft.getMinecraft().entityRenderer.enableLightmap();

            //GlStateManager.enableLighting();

            Tessellator tessellator = Tessellator.getInstance();

            /*VertexBuffer vb = t.getBuffer();

            vb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            {
                vb.color(5.0f,1.0f,5.0f,1f); // white
                vb.pos(posX + radius, posY + radius, posZ - radius);
                vb.pos(posX - radius, posY + radius, posZ - radius);
                vb.pos(posX - radius, posY + radius, posZ + radius);
                vb.pos(posX + radius, posY + radius, posZ + radius);
            }

            t.draw();

            vb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            //bottom
            {
                vb.color(5.0f,1.0f,5.0f,1f); // white
                vb.pos(posX + radius, posY - radius, posZ + radius);
                vb.pos(posX - radius, posY - radius, posZ + radius);
                vb.pos(posX - radius, posY - radius, posZ - radius);
                vb.pos(posX + radius, posY - radius, posZ - radius);
            }

            t.draw();

            vb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            //right side
            {
                vb.color(5.0f,1.0f,5.0f,1); // white
                vb.pos(posX + radius, posY + radius, posZ + radius);
                vb.pos(posX + radius, posY - radius, posZ + radius);
                vb.pos(posX + radius, posY - radius, posZ - radius);
                vb.pos(posX + radius, posY + radius, posZ - radius);
            }

            t.draw();

            vb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            //left side
            {
                vb.color(5.0f,1.0f,5.0f,1); // white
                vb.pos(posX - radius, posY + radius, posZ - radius);
                vb.pos(posX - radius, posY - radius, posZ - radius);
                vb.pos(posX - radius, posY - radius, posZ + radius);
                vb.pos(posX - radius, posY + radius, posZ + radius);
            }

            t.draw();

            vb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            //back side
            {

                vb.color(5.0f,1.0f,5.0f,1); // white
                vb.pos(posX + radius, posY - radius, posZ - radius);
                vb.pos(posX - radius, posY - radius, posZ - radius);
                vb.pos(posX - radius, posY + radius, posZ - radius);
                vb.pos(posX + radius, posY + radius, posZ - radius);
            }

            t.draw();*/


            String texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(block.block).getParticleTexture().getIconName();
            int character = texture.indexOf(":");
            String tex = texture.substring(0,character);
            String tex2 = texture.substring(character + 1);
            tex2 = "textures/" + tex2 + ".png";

            /*System.out.println(tex);
            System.out.println(tex2);*/

            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(tex,tex2));
            //System.out.println(texture);

            //tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

            GL11.glBegin(GL_QUADS);
            //top
            {
                glColor3f(5.0f,1.0f,5.0f); // white
                GL11.glTexCoord2f(0.0f, 1.0f);
                glVertex3f(posX + radius, posY + radius, posZ - radius);
                GL11.glTexCoord2f(0.0f, 0.0f);
                glVertex3f(posX - radius, posY + radius, posZ - radius);
                GL11.glTexCoord2f(1.0f, 0.0f);
                glVertex3f(posX - radius, posY + radius, posZ + radius);
                GL11.glTexCoord2f(1.0f, 1.0f);
                glVertex3f(posX + radius, posY + radius, posZ + radius);
            }

            //bottom
            {
                glColor3f(5.0f,1.0f,5.0f); // white
                GL11.glTexCoord2f(1.0f, 1.0f);
                glVertex3f(posX + radius, posY - radius, posZ + radius);
                GL11.glTexCoord2f(0.0f, 1.0f);
                glVertex3f(posX - radius, posY - radius, posZ + radius);
                GL11.glTexCoord2f(0.0f, 0.0f);
                glVertex3f(posX - radius, posY - radius, posZ - radius);
                GL11.glTexCoord2f(1.0f, 0.0f);
                glVertex3f(posX + radius, posY - radius, posZ - radius);
            }

            //right side
            {
                glColor3f(5.0f,1.0f,5.0f); // white
                GL11.glTexCoord2f(1.0f, 0.0f);
                glVertex3f(posX + radius, posY + radius, posZ + radius);
                GL11.glTexCoord2f(1.0f, 1.0f);
                glVertex3f(posX + radius, posY - radius, posZ + radius);
                GL11.glTexCoord2f(0.0f, 1.0f);
                glVertex3f(posX + radius, posY - radius, posZ - radius);
                GL11.glTexCoord2f(0.0f, 0.0f);
                glVertex3f(posX + radius, posY + radius, posZ - radius);
            }

            //left side
            {
                glColor3f(5.0f,1.0f,5.0f); // white
                GL11.glTexCoord2f(0.0f, 0.0f);
                glVertex3f(posX - radius, posY + radius, posZ - radius);
                GL11.glTexCoord2f(1.0f, 0.0f);
                glVertex3f(posX - radius, posY - radius, posZ - radius);
                GL11.glTexCoord2f(1.0f, 1.0f);
                glVertex3f(posX - radius, posY - radius, posZ + radius);
                GL11.glTexCoord2f(0.0f, 1.0f);
                glVertex3f(posX - radius, posY + radius, posZ + radius);
            }

            //front side
            {
                glColor3f(5.0f,1.0f,5.0f); // white
                GL11.glTexCoord2f(0.0f, 0.0f);
                glVertex3f(posX + radius, posY + radius, posZ + radius);
                GL11.glTexCoord2f(1.0f, 0.0f);
                glVertex3f(posX - radius, posY + radius, posZ + radius);
                GL11.glTexCoord2f(1.0f, 1.0f);
                glVertex3f(posX - radius, posY - radius, posZ + radius);
                GL11.glTexCoord2f(0.0f, 1.0f);
                glVertex3f(posX + radius, posY - radius, posZ + radius);
            }

            //back side
            {
                glColor3f(5.0f,1.0f,5.0f); // white
                GL11.glTexCoord2f(0.0f, 1.0f);
                glVertex3f(posX + radius, posY - radius, posZ - radius);
                GL11.glTexCoord2f(1.0f, 1.0f);
                glVertex3f(posX - radius, posY - radius, posZ - radius);
                GL11.glTexCoord2f(1.0f, 0.0f);
                glVertex3f(posX - radius, posY + radius, posZ - radius);
                GL11.glTexCoord2f(0.0f, 0.0f);
                glVertex3f(posX + radius, posY + radius, posZ - radius);
            }
            GL11.glEnd();

            //Minecraft.getMinecraft().entityRenderer.disableLightmap();

            //GlStateManager.disableLighting();

            //BlockRendererDispatcher

            //BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            //blockrendererdispatcher.renderBlock(Blocks.STONE.getDefaultState(),new BlockPos())



            //RenderHelper.disableStandardItemLighting();

            GL11.glPopMatrix(); // Restore the saved transformation
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }


//...


    }

    ArrayList<Vec3d> lineList = new ArrayList<Vec3d>(); // Storage of vertices for all lines

    Vec3d[] cube = new Vec3d[8];	// Example

    public void setCubeList(){

        // vertices of a cube;
        cube[0] = new Vec3d(0,1,0);
        cube[1] = new Vec3d(1,1,0);
        cube[2] = new Vec3d(0,1,1);
        cube[3] = new Vec3d(1,1,1);
        cube[4] = new Vec3d(0,2,0);
        cube[5] = new Vec3d(1,2,0);
        cube[6] = new Vec3d(1,2,1);
        cube[7] = new Vec3d(0,2,1);

        lineList.add(cube[0]); // edge 1-2
        lineList.add(cube[1]);

        lineList.add(cube[1]); // edge 2-4
        lineList.add(cube[3]);

        lineList.add(cube[0]); // edge 1-3
        lineList.add(cube[2]);

        lineList.add(cube[2]); // edge 3-4
        lineList.add(cube[3]);

        lineList.add(cube[4]); // edge 5-8
        lineList.add(cube[7]);

        lineList.add(cube[4]); // edge 5-6
        lineList.add(cube[5]);

        lineList.add(cube[6]); // edge 7-8
        lineList.add(cube[7]);

        lineList.add(cube[5]); // edge 6-7
        lineList.add(cube[6]);

        lineList.add(cube[0]); // edge 1-5
        lineList.add(cube[4]);

        lineList.add(cube[1]); // edge 2-6
        lineList.add(cube[5]);

        lineList.add(cube[3]); // edge 4-7
        lineList.add(cube[6]);

        lineList.add(cube[2]); // edge 3-8
        lineList.add(cube[7]);
    }


    private void drawLineWithGL(Vec3d blockA, Vec3d blockB) {
        //int d = Math.round((float)blockA.distanceTo(blockB)+0.2f);
        glColor3f(0F, 1F, 0F);
        float oz = (blockA.xCoord - blockB.xCoord == 0?0:-1f/16f);
        float ox = (blockA.zCoord - blockB.zCoord == 0?0:1f/16f);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        //you will want to modify these offsets.
        GL11.glVertex3d(blockA.xCoord,blockA.yCoord - 0.5,blockA.zCoord);
        GL11.glVertex3d(blockB.xCoord,blockB.yCoord - 0.5,blockB.zCoord);

        GL11.glEnd();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(InputEvent.KeyInputEvent event) {
        // DEBUG
        //System.out.println("Key Input Event");

        // make local copy of key binding array
        KeyBinding[] keyBindings = ClientProxy.keyBindings;

        // check each enumerated key binding type for pressed and take appropriate action
        if (keyBindings[0].isPressed()) {
            // DEBUG
            //System.out.println("Key binding =" + keyBindings[0].getKeyDescription());

            EntityPlayer player = Minecraft.getMinecraft().player;
            World worldObj = Minecraft.getMinecraft().world;

            EntityPhysicsBlock pb = new EntityPhysicsBlock(worldObj);
            //pb.setPosition(player.posX,player.posY,player.posZ);
            pb.init((float) player.posX - 0.5f, (float) player.posY - 2f, (float) player.posZ - 0.5f, 0, 0, 0, Blocks.STONE.getDefaultState());
            pb.forceSpawn = true;

            // do stuff for this key binding here
            // remember you may need to send packet to server
        }

        if (keyBindings[1].isPressed()) {
            PhysicsMod.instance.infobox.displayInfo(I18n.format("physics.removeblock.text"));

            if(PhysicsMod.blocks.size() > 0) {
                //List<EntityPhysicsBlock> blocks2 = PhysicsMod.blocks;

                for (int i = PhysicsMod.blocks.size() - 1; i != -1; i--) {
                    EntityPhysicsBlock block = PhysicsMod.blocks.get(i);
                    block.setDead();
                }
            }
        }

        if (keyBindings[2].isPressed()) {
            if(PhysicsMod.paused) {
                PhysicsMod.paused = false;
                PhysicsMod.instance.infobox.displayInfo("Unpaused");
            }
            else {
                PhysicsMod.paused = true;
                PhysicsMod.instance.infobox.displayInfo("Paused");
            }
        }
    }
}
