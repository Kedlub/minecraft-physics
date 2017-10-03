package com.github.kedlub.physics.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Created by Kubik on 16.06.2017.
 */
public class ModelCube extends ModelBase {

    ModelRenderer cube;

    void ModelCube() {
        this.cube = new ModelRenderer(this, 0, 0);
        this.cube.addBox(0, 0, 0, 1, 1, 1, 1);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.cube.render(1);
    }

}
