package com.github.kedlub.physics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Kubik on 25.09.2017.
 */

@SideOnly(Side.CLIENT)
public class GuiInfobox extends Gui {
    private static final ResourceLocation ACHIEVEMENT_BG = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private final Minecraft mc;
    private int width;
    private int height;
    private String achievementTitle;
    private String achievementDescription;
    //private Achievement theAchievement;
    public long notificationTime;
    //private final RenderItem renderItem;
    private boolean permanentNotification;

    public GuiInfobox(Minecraft mc)
    {
        this.mc = mc;
        //this.renderItem = mc.getRenderItem();
    }

    public void displayInfo(String info)  {
        //System.out.println("Added infobox to queue");

        this.achievementTitle = "";
        this.achievementDescription = info;
        this.notificationTime = Minecraft.getSystemTime();
        //this.theAchievement = ;
        this.permanentNotification = true;
    }

    public void displayInfoTitle(String title, String description, int time)  {
        //System.out.println("Added infobox to queue");

        this.achievementTitle = title;
        this.achievementDescription = description;
        this.notificationTime = Minecraft.getSystemTime() + time;
        //this.theAchievement = ;
        this.permanentNotification = false;
    }

    private void updateAchievementWindowScale()
    {
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)this.width, (double)this.height, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    public void updateAchievementWindow()
    {
        //System.out.println("updateAchievementWindow called and notificationTime = " + this.notificationTime);
        if (this.notificationTime != 0L)
        {
            double d0 = (double)(Minecraft.getSystemTime() - this.notificationTime) / 3000.0D;

            /*if (this.permanentNotification)
            {
                if (d0 > 0.5D)
                {
                    d0 = 0.5D;
                }
            }
            else*/ if (d0 < 0.0D || d0 > 1.0D)
            {
                this.notificationTime = 0L;
                return;
            }

            this.updateAchievementWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            double d1 = d0 * 2.0D;

            if (d1 > 1.0D)
            {
                d1 = 2.0D - d1;
            }

            d1 = d1 * 4.0D;
            d1 = 1.0D - d1;

            if (d1 < 0.0D)
            {
                d1 = 0.0D;
            }

            d1 = d1 * d1;
            d1 = d1 * d1;
            int i = 0;
            int j = 0 - (int)(d1 * 36.0D);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BG);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(i, j, 96, 202, 160, 32);

            //System.out.println("Drawing window");

            if (this.permanentNotification)
            {
                this.mc.fontRendererObj.drawSplitString(this.achievementDescription, i + 10, j + 7, 120, -1);
                //System.out.println("Drawing text");
            }
            else
            {
                this.mc.fontRendererObj.drawString(this.achievementTitle, i + 10, j + 7, -256);
                this.mc.fontRendererObj.drawString(this.achievementDescription, i + 10, j + 18, -1);
                //System.out.println("Drawing text");
            }

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            //this.renderItem.renderItemAndEffectIntoGUI(this.theAchievement.theItemStack, i + 8, j + 8);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }

    public void clearAchievements()
    {
        //this.theAchievement = null;
        this.notificationTime = 0L;
    }
}
