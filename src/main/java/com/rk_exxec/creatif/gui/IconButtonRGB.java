package com.rk_exxec.creatif.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rk_exxec.creatif.interfaces.IGuiRenderable;
import com.rk_exxec.creatif.util.MyButtons;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;

public class IconButtonRGB  extends IconButton{
    public static enum Color{
        GREEN, RED, BLUE, YELLOW, NONE;
    } 
    Color color;
	// public boolean green;

    public IconButtonRGB(int x, int y, ScreenElement icon) {
		super(x, y, 18, 18, icon);
        this.color = Color.NONE;
	}

	public IconButtonRGB(int x, int y, ScreenElement icon, Color color) {
		super(x, y, 18, 18, icon);
        this.color = color;
	}

	@Override
	public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;

            IGuiRenderable button = resolveButtonTexture();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			drawBg(graphics, button);
			icon.render(graphics, getX() + 1, getY() + 1);
		}
	}

    IGuiRenderable resolveButtonTexture(){
        int state = 0;
        if(!active) state = 3;
        if(isHovered){
            if(AllKeys.isMouseButtonDown(0)) state = 2;
            else state = 1;
        }
        switch(color){
            case BLUE:
                return MyButtons.BLUE.get(state);
            case GREEN:
                return MyButtons.GREEN.get(state);
            case NONE:
                return MyButtons.GREY.get(state);
            case RED:
                return MyButtons.RED.get(state);
            case YELLOW:
                return MyButtons.YELLOW.get(state);
            default:
                return MyButtons.GREY.get(state);
        }
    }

	protected void drawBg(GuiGraphics graphics, IGuiRenderable button) {
		graphics.blit(button.getLocation(), getX(), getY(), button.getStartX(), button.getStartY(), button.getWidth(),
			button.getHeight());
	}
}
