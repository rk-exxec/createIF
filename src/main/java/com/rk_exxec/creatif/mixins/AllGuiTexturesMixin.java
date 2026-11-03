package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.rk_exxec.creatif.interfaces.IGuiRenderable;
import com.simibubi.create.foundation.gui.AllGuiTextures;

@Mixin(AllGuiTextures.class)
public interface AllGuiTexturesMixin extends IGuiRenderable {
    
}
