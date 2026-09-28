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

package com.rk_exxec.creatif.gui;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.gui.IngredientFilterMenu;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket.IngOption;
import com.rk_exxec.creatif.util.CreatIFLang;
import com.rk_exxec.creatif.util.MyGuiTextures;
import com.rk_exxec.creatif.util.MyPackets;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket.Option;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IngredientFilterScreen extends AbstractSimiContainerScreen<IngredientFilterMenu> {

    private static final String CREATE_PREFIX = "gui.filter.";
    private static final String MY_PREFIX = "gui." + CreatIF.MODID;

	private Component allowN = CreateLang.translateDirect(CREATE_PREFIX + "allow_list");
	private Component allowDESC = CreateLang.translateDirect(CREATE_PREFIX + "allow_list.description");
	private Component denyN = CreateLang.translateDirect(CREATE_PREFIX + "deny_list");
	private Component denyDESC = CreateLang.translateDirect(CREATE_PREFIX + "deny_list.description");

	private Component respectDataN = CreateLang.translateDirect(CREATE_PREFIX + "respect_data");
	private Component respectDataDESC = CreateLang.translateDirect(CREATE_PREFIX + "respect_data.description");
	private Component ignoreDataN = CreateLang.translateDirect(CREATE_PREFIX + "ignore_data");
	private Component ignoreDataDESC = CreateLang.translateDirect(CREATE_PREFIX + "ignore_data.description");

    private Component matchAnyN = CreatIFLang.translateDirect(MY_PREFIX, "match_any");
    private Component matchAnyDESC = CreatIFLang.translateDirect(MY_PREFIX, "match_any.description");
    private Component matchAllN = CreatIFLang.translateDirect(MY_PREFIX, "match_all");
    private Component matchAllDESC = CreatIFLang.translateDirect(MY_PREFIX, "match_all.description");

	private IconButton whitelist, blacklist;
	private IconButton respectNBT, ignoreNBT;
    private IconButton matchAnyButton;
    private IconButton matchAllButton;


	private IconButton resetButton;
	private IconButton confirmButton;

	private List<Rect2i> extraAreas = Collections.emptyList();

    MyGuiTextures background;

    public IngredientFilterScreen(IngredientFilterMenu menu, Inventory inventory, Component title) {
        this(menu, inventory, title, MyGuiTextures.CREATIF_INGREDIENT_FILTER);
    }

	protected IngredientFilterScreen(IngredientFilterMenu menu, Inventory inv, Component title, MyGuiTextures background) {
		super(menu, inv, title);
		this.background = background;
	}

    @Override
    protected void init() {
        setWindowOffset(-11, CreatIF.I_SCREEN_Y_OFFSET);
		setWindowSize(Math.max(background.getWidth(), PLAYER_INVENTORY.getWidth()),
			background.getHeight() + 4 + PLAYER_INVENTORY.getHeight());
		super.init();
		extraAreas = List.of(new Rect2i(leftPos + background.getWidth(), topPos + background.getHeight() - 40, 80, 48));

		int x = leftPos;
		int y = topPos;

		resetButton = new IconButton(x + background.getWidth() - 62, y + background.getHeight() - 24, AllIcons.I_TRASH);
		resetButton.withCallback(() -> {
			menu.clearContents();
			contentsCleared();
			menu.sendClearPacket();
		});
		confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
			minecraft.player.closeContainer();
		});

		addRenderableWidget(resetButton);
		addRenderableWidget(confirmButton);

		int top_offset = background.getHeight() - 24;
		int btn_width = 18;
		int btn_spacing = 6;

		blacklist = new IconButton(x + btn_width, y + top_offset, AllIcons.I_BLACKLIST);
		blacklist.withCallback(() -> {
			menu.blacklist = true;
			sendOptionUpdate(Option.BLACKLIST);
		});
		blacklist.setToolTip(denyN);
		whitelist = new IconButton(x + btn_width*2, y + top_offset, AllIcons.I_WHITELIST);
		whitelist.withCallback(() -> {
			menu.blacklist = false;
			sendOptionUpdate(Option.WHITELIST);
		});
		whitelist.setToolTip(allowN);
		addRenderableWidgets(blacklist, whitelist);

		respectNBT = new IconButton(x + btn_spacing + btn_width*3, y + top_offset, AllIcons.I_RESPECT_NBT);
		respectNBT.withCallback(() -> {
			menu.respectNBT = true;
			sendOptionUpdate(Option.RESPECT_DATA);
		});
		respectNBT.setToolTip(respectDataN);
		ignoreNBT = new IconButton(x + btn_spacing + btn_width*4, y + top_offset, AllIcons.I_IGNORE_NBT);
		ignoreNBT.withCallback(() -> {
			menu.respectNBT = false;
			sendOptionUpdate(Option.IGNORE_DATA);
		});
		ignoreNBT.setToolTip(ignoreDataN);
		addRenderableWidgets(respectNBT, ignoreNBT);

        matchAnyButton = new IconButton(x + btn_spacing*2 + btn_width*5, y + top_offset, AllIcons.I_WHITELIST_OR);
        matchAnyButton.setToolTip(matchAnyN);
        matchAnyButton.withCallback(() -> {
            IngredientFilterMenu menu = (IngredientFilterMenu) this.menu;
            menu.matchAny = true;
            sendOptionUpdate(IngOption.INGR_MATCHANY);
        });

        matchAllButton = new IconButton(x + btn_spacing*2 + btn_width*6, y + top_offset, AllIcons.I_WHITELIST_AND);
        matchAllButton.setToolTip(matchAllN);
        matchAllButton.withCallback(() -> {
            IngredientFilterMenu menu = (IngredientFilterMenu) this.menu;
            menu.matchAny = false;
            sendOptionUpdate(IngOption.INGR_MATCHALL);
        });
        addRenderableWidgets(matchAnyButton,matchAllButton);
        handleIndicators();
        
    }

    // private void updateButtons(){
    //     IngredientFilterMenu menu = (IngredientFilterMenu) this.menu;
    //     matchAnyButton.setFocused(menu.matchAny);
    //     matchAllButton.setFocused(!menu.matchAny);
    // }

	protected List<IconButton> getTooltipButtons() {
		return Arrays.asList(blacklist, whitelist, respectNBT, ignoreNBT,matchAnyButton,matchAllButton);
	}

	protected List<MutableComponent> getTooltipDescriptions() {
		return Arrays.asList(denyDESC.plainCopy(), allowDESC.plainCopy(), respectDataDESC.plainCopy(), ignoreDataDESC.plainCopy(),
        matchAnyDESC.plainCopy(), matchAllDESC.plainCopy());
	}

    protected boolean isButtonEnabled(IconButton button) {
        if (button == blacklist)
			return !menu.blacklist;
		if (button == whitelist)
			return menu.blacklist;
		if (button == respectNBT)
			return !menu.respectNBT;
		if (button == ignoreNBT)
			return menu.respectNBT;
        if (button == matchAnyButton)
            return !menu.matchAny; // this seems the wrong way aroung but in the AbstractFilterScreen it gets inverted again, idk why
        if (button == matchAllButton)
            return menu.matchAny;
        return true;
    }

    protected void sendOptionUpdate(IngOption option) {
		MyPackets.getChannel()
			.sendToServer(new IngredientFilterScreenPacket(option));
	}

	protected int getTitleColor() {
		return 0x00302B;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
		int invY = topPos + background.getHeight() + 4;
		renderPlayerInventory(graphics, invX, invY);

		int x = leftPos;
		int y = topPos;

		background.render(graphics, x, y);
		graphics.drawString(font, title, x + (background.getWidth() - 8) / 2 - font.width(title) / 2, y + 4,
			getTitleColor(), false);

		GuiGameElement.of(menu.contentHolder).
		<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth() + 8, y + background.getHeight() - 52, -200)
			.scale(4)
			.render(graphics);
	}

	
	@Override
	protected void containerTick() {
		// if(!menu.stillValid(menu.player))
		// // if (!menu.player.getMainHandItem()
		// // 	.equals(menu.contentHolder, false))
		// 	menu.player.closeContainer();

		super.containerTick();

		handleTooltips();
		handleIndicators();
	}

	protected void handleTooltips() {
		List<IconButton> tooltipButtons = getTooltipButtons();

		for (IconButton button : tooltipButtons) {
			if (!button.getToolTip()
				.isEmpty()) {
				button.setToolTip(button.getToolTip()
					.get(0));
				button.getToolTip()
					.add(TooltipHelper.holdShift(Palette.YELLOW, hasShiftDown()));
			}
		}

		if (hasShiftDown()) {
			List<MutableComponent> tooltipDescriptions = getTooltipDescriptions();
			for (int i = 0; i < tooltipButtons.size(); i++)
				fillToolTip(tooltipButtons.get(i), tooltipDescriptions.get(i));
		}
	}

	public void handleIndicators() {
		for (IconButton button : getTooltipButtons())
			button.green = !isButtonEnabled(button);
	}

	private void fillToolTip(IconButton button, Component tooltip) {
		if (!button.isHoveredOrFocused())
			return;
		List<Component> tip = button.getToolTip();
		tip.addAll(TooltipHelper.cutTextComponent(tooltip, Palette.ALL_GRAY));
	}

	protected void contentsCleared() {}

	protected void sendOptionUpdate(Option option) {
		AllPackets.getChannel()
			.sendToServer(new FilterScreenPacket(option));
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		renderFluids(guiGraphics);
	}

	@Override
	protected void renderTooltip(GuiGraphics gfx, int x, int y) {
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			if(this.hoveredSlot instanceof IngredientSlot ingrSlot){
				IngredientStack stack = ingrSlot.getIngredientStack();
				if(stack.isFluid())
					gfx.renderTooltip(this.font, this.getTooltipFromContainerItem(stack), stack.getTooltipImage(), stack.itemStack(), x, y);
				else
					gfx.renderTooltip(this.font, this.getTooltipFromContainerItem(stack), stack.getTooltipImage(), stack.itemStack(), x, y);
			}
			else super.renderTooltip(gfx, x, y);
		}

	}

	protected List<Component> getTooltipFromContainerItem(IngredientStack stack) {
		return  stack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
	}


	private void renderFluids(GuiGraphics guiGraphics) {
		for (int i = 0; i < menu.ghostInventory.getSlots(); i++) {
			IngredientStack ingredient =
					menu.ghostInventory.getIngredientStackInSlot(i);

			if (!ingredient.isFluid())
				continue;

			int row = i / menu.INGR_INV_N_COLS;
			int col = i % menu.INGR_INV_N_COLS;

			int x = leftPos + menu.INGR_SLOT_OFFSET_X + col * 18;
			int y = topPos + menu.INGR_SLOT_OFFSET_Y + row * 18;

			IngredientStack.renderFluid(
				guiGraphics,
				ingredient.getFluidStack().orElse(FluidStack.EMPTY),
				x,
				y
			);
		}

		for (int i = 0; i < menu.outputGhostInventory.getSlots(); i++) {
			IngredientStack ingredient =
					menu.outputGhostInventory.getIngredientStackInSlot(i);

			if (!ingredient.isFluid())
				continue;

			int row = i / menu.OUTP_INV_N_COLS;
			int col = i % menu.OUTP_INV_N_COLS;

			int x = leftPos + menu.OUTP_SLOT_OFFSET_X + col * 18;
			int y = topPos + menu.OUTP_SLOT_OFFSET_Y + row * 18;

			IngredientStack.renderFluid(
				guiGraphics,
				ingredient.getFluidStack().orElse(FluidStack.EMPTY),
				x,
				y
			);
		}
	}
}