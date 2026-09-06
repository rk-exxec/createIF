package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.CreateContentFilter;
import com.simibubi.create.content.logistics.filter.FilterMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MenuType;

public class ContentFilterMenu extends FilterMenu {

    public boolean matchAny;

    public ContentFilterMenu(MenuType<?> type, int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(type, id, inventory, buffer);
    }

    public ContentFilterMenu(MenuType<?> type, int id, Inventory inventory, ItemStack filter) {
        super(type, id, inventory, filter);
    }

    @Override
    protected void initAndReadInventory(ItemStack filter) {
        super.initAndReadInventory(filter);
        matchAny = filter.getOrCreateTag().getBoolean("Match Any");
    }

    @Override
    protected void saveData(ItemStack filter) {
        super.saveData(filter);
        CompoundTag tag = filter.getOrCreateTag();
        tag.putBoolean("Match Any", matchAny);
    }

    public void setMatchAny(boolean value) {
        matchAny = value;
        saveData((ItemStack) contentHolder);
    }

    public static ContentFilterMenu create(int id, Inventory inventory, ItemStack filter) {
        return new ContentFilterMenu(CreateContentFilter.CONTENT_FILTER_MENU.get(), id, inventory, filter);
    }
}