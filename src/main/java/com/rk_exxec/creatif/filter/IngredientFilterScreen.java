package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.interfaces.IAbstractFilterScreenMixin;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket.IngOption;
import com.rk_exxec.creatif.util.CreatIFLang;
import com.rk_exxec.creatif.util.MyGuiTextures;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket.Option;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;


import java.util.Arrays;
import java.util.List;

public class IngredientFilterScreen extends AbstractFilterScreen<IngredientFilterMenu> {

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

    MyGuiTextures background;

    public IngredientFilterScreen(IngredientFilterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, AllGuiTextures.FILTER);
		this.background = MyGuiTextures.CREATIF_INGREDIENT_FILTER;
    }

    @Override
    protected void init() {
        setWindowOffset(-11, CreatIF.I_SCREEN_Y_OFFSET);
		setWindowSize(Math.max(background.getWidth(), PLAYER_INVENTORY.getWidth()),
			background.getHeight() + 4 + PLAYER_INVENTORY.getHeight());
		((IAbstractFilterScreenMixin) (Object) this).onlySuperInit();

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

    @Override
	protected List<IconButton> getTooltipButtons() {
		return Arrays.asList(blacklist, whitelist, respectNBT, ignoreNBT,matchAnyButton,matchAllButton);
	}

	@Override
	protected List<MutableComponent> getTooltipDescriptions() {
		return Arrays.asList(denyDESC.plainCopy(), allowDESC.plainCopy(), respectDataDESC.plainCopy(), ignoreDataDESC.plainCopy(),
        matchAnyDESC.plainCopy(), matchAllDESC.plainCopy());
	}

    @Override
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
            return !((IngredientFilterMenu) menu).matchAny; // this seems the wrong way aroung but in the AbstractFilterScreen it gets inverted again, idk why
        if (button == matchAllButton)
            return ((IngredientFilterMenu) menu).matchAny;
        return true;
    }

    protected void sendOptionUpdate(IngOption option) {
		CreatIF.CHANNEL
			.sendToServer(new IngredientFilterScreenPacket(option));
	}

    @Override
	protected int getTitleColor() {
		return 0x00302B;
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return List.of(new Rect2i(leftPos + background.getWidth(), topPos + background.getHeight() - 40, 80, 48));
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

		GuiGameElement.of(menu.contentHolder).<GuiGameElement
			.GuiRenderBuilder>at(x + background.getWidth() + 8, y + background.getHeight() - 52, -200)
			.scale(4)
			.render(graphics);
	}
}