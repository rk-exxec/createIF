/*=====================================================================
CreatIF - Create: Ingredient Filter 
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

package com.rk_exxec.creatif.util;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.interfaces.IGuiRenderable;

import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class MyButtons implements IGuiRenderable {

	public static record Button(MyButtons def, MyButtons hov, MyButtons pr, MyButtons dis){
		public MyButtons get(int state){
			switch (state) {
				case 0:
					return def;
				case 1:
					return hov;
				case 2:
					return pr;
				case 3:
					return dis;
				default:
					return def;
			}
		}
	}

	public static final ResourceLocation location = ResourceLocation.fromNamespaceAndPath(CreatIF.MODID, "textures/gui/ingredient_filter.png");
	public static final Button
		RED = button( 0, 144,18, 18, 18),
		BLUE = button(18,144,18, 18,18),
		YELLOW = button(36,144,18, 18,18),
		GREEN = button(54,144,18, 18,18),
		GREY = button(72,144,18, 18,18);
    public static final int FONT_COLOR = 0x575F7A;

	private final int width;
	private final int height;
	private final int startX;
	private final int startY;

    MyButtons(int width, int height) {
		this(0, 0, width, height);
	}

	MyButtons(int startX, int startY, int width, int height, int variantDistY) {
		this(startX, startY, width, height);
	}

    MyButtons(int startX, int startY, int width, int height) {
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
	}

	public static MyButtons normal(int x, int y, int w, int h){
		return new MyButtons(x,y, w, h);
	}

	public static Button button(int x, int y, int w, int h, int varSpaceY){
		return new Button( normal(x,y,w,h), normal(x, y+varSpaceY, w, h), normal(x, y+2*varSpaceY, w, h), normal(0, y+3*varSpaceY, 18, 18));
	}

	@Override
	public ResourceLocation getLocation() {
		return location;
	}

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
	}

	@Override
	public int getStartX() {
		return startX;
	}

	@Override
	public int getStartY() {
		return startY;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}
