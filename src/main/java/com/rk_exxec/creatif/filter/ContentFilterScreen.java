package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.network.SetMatchAnyPacket;
import com.simibubi.create.content.logistics.filter.FilterScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ContentFilterScreen extends FilterScreen {

    private IconButton matchAnyButton;
    private IconButton matchAllButton;

    public ContentFilterScreen(ContentFilterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        matchAnyButton = new IconButton(leftPos + 102, topPos + 75, AllIcons.I_WHITELIST_OR);
        matchAnyButton.setToolTip(CreateLang.translateDirect("gui.creatif.match_any"));
        matchAnyButton.withCallback(() -> {
            ContentFilterMenu menu = (ContentFilterMenu) this.menu;
            menu.matchAny = true;
            updateButtons();
            SetMatchAnyPacket.send(menu.containerId, menu.matchAny);
        });
        addRenderableWidgets(matchAnyButton);

        matchAllButton = new IconButton(leftPos + 120, topPos + 75, AllIcons.I_WHITELIST_AND);
        matchAllButton.setToolTip(CreateLang.translateDirect("gui.creatif.match_all"));
        matchAllButton.withCallback(() -> {
            ContentFilterMenu menu = (ContentFilterMenu) this.menu;
            menu.matchAny = false;
            updateButtons();
            SetMatchAnyPacket.send(menu.containerId, menu.matchAny);
        });
        addRenderableWidgets(matchAllButton);
    }

    private void updateButtons(){
        ContentFilterMenu menu = (ContentFilterMenu) this.menu;
        matchAnyButton.setFocused(menu.matchAny);
        matchAllButton.setFocused(!menu.matchAny);
    }

    @Override
    protected List<IconButton> getTooltipButtons() {
        List<IconButton> buttons = new ArrayList<>(super.getTooltipButtons());
        if (matchAnyButton != null)
            buttons.add(matchAnyButton);
        return buttons;
    }

    @Override
    protected List<MutableComponent> getTooltipDescriptions() {
        List<MutableComponent> descriptions = new ArrayList<>(super.getTooltipDescriptions());
        if (matchAnyButton != null)
            descriptions.add(CreateLang.translateDirect("gui.creatif.match_any.description"));
        if (matchAllButton != null)
            descriptions.add(CreateLang.translateDirect("gui.creatif.match_all.description"));
        return descriptions;
    }

    @Override
    protected boolean isButtonEnabled(IconButton button) {
        if (button == matchAnyButton)
            return ((ContentFilterMenu) menu).matchAny;
        if (button == matchAllButton)
            return !((ContentFilterMenu) menu).matchAny;
        return super.isButtonEnabled(button);
    }
}