package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.ImmutableList;
import com.rk_exxec.creatif.filter.IAbstractFilterScreenMixin;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;


@Mixin(AbstractFilterScreen.class)
abstract class AbstractFilterScreenMixin<F extends AbstractFilterMenu> extends AbstractSimiContainerScreen<F> implements IAbstractFilterScreenMixin{
    // @Shadow(remap=false)
    AbstractFilterScreenMixin (F menu, Inventory inv, Component title, AllGuiTextures background){super(menu, null, title);}

    @Shadow(remap=false)
    protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
    }

    public void onlySuperInit() {
		super.init();
	}

    

}
