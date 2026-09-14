package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.CreateIngredientFilter;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket.IngOption;
import com.rk_exxec.creatif.util.CreatIFLang;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.FilterScreen;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class IngredientFilterScreen extends FilterScreen {

    private IconButton matchAnyButton;
    private IconButton matchAllButton;

    public IngredientFilterScreen(IngredientFilterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        matchAnyButton = new IconButton(leftPos + 102, topPos + 75, AllIcons.I_WHITELIST_OR);
        matchAnyButton.setToolTip(CreatIFLang.translate("gui","match_any"));
        matchAnyButton.withCallback(() -> {
            IngredientFilterMenu menu = (IngredientFilterMenu) this.menu;
            menu.matchAny = true;
            // updateButtons();
            sendOptionUpdate(IngOption.INGR_MATCHANY);
            // SetMatchAnyPacket.send(menu.containerId, menu.matchAny);
        });

        matchAllButton = new IconButton(leftPos + 120, topPos + 75, AllIcons.I_WHITELIST_AND);
        matchAllButton.setToolTip(CreatIFLang.translate("gui","match_all"));
        matchAllButton.withCallback(() -> {
            IngredientFilterMenu menu = (IngredientFilterMenu) this.menu;
            menu.matchAny = false;
            // updateButtons();
            sendOptionUpdate(IngOption.INGR_MATCHALL);
            // SetMatchAnyPacket.send(menu.containerId, menu.matchAny);
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
        List<IconButton> buttons = new ArrayList<>(super.getTooltipButtons());
        if (matchAnyButton != null)
            buttons.add(matchAnyButton);
        if (matchAllButton != null)
            buttons.add(matchAllButton);
        return buttons;
    }

    @Override
    protected List<MutableComponent> getTooltipDescriptions() {
        List<MutableComponent> descriptions = new ArrayList<>(super.getTooltipDescriptions());
        if (matchAnyButton != null)
            descriptions.add(CreatIFLang.translate("gui","match_any.description"));
        if (matchAllButton != null)
            descriptions.add(CreatIFLang.translate("gui","match_all.description"));
        return descriptions;
    }

    @Override
    protected boolean isButtonEnabled(IconButton button) {
        if (button == matchAnyButton)
            return !((IngredientFilterMenu) menu).matchAny; // this seems the wrong way aroung but in the AbstractFilterScreen it gets inverted again, idk why
        if (button == matchAllButton)
            return ((IngredientFilterMenu) menu).matchAny;
        return super.isButtonEnabled(button);
    }

    protected void sendOptionUpdate(IngOption option) {
		CreateIngredientFilter.CHANNEL
			.sendToServer(new IngredientFilterScreenPacket(option));
	}

    @Override
	protected int getTitleColor() {
		return 0x00302B;
	}
}