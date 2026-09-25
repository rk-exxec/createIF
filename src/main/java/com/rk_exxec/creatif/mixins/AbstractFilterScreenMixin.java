/*=====================================================================
CreatIF- Create: Ingredient Filter 
Adds a new filter type to select basin recipes based on input
Copyright (C) 2026  rk-exxec

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
=====================================================================*/

package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.rk_exxec.creatif.interfaces.IAbstractFilterScreenMixin;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;


@Mixin(AbstractFilterScreen.class)
abstract class AbstractFilterScreenMixin<F extends AbstractFilterMenu> extends AbstractSimiContainerScreen<F> implements IAbstractFilterScreenMixin{

    AbstractFilterScreenMixin (F menu, Inventory inv, Component title, AllGuiTextures background){super(menu, null, title);}

    @Shadow(remap=false)
    protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
        throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
    }

    // skips init of graphical elements and background stuff, 
    // allows custom implementation that uses different layout and such
    public void onlySuperInit() {
		super.init();
	}

    

}
