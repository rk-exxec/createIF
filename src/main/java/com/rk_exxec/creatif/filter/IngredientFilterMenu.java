package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.CreateIngredientFilter;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.FilterMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.world.inventory.MenuType;

public class IngredientFilterMenu extends AbstractFilterMenu {
    boolean respectNBT;
	boolean blacklist;
    public boolean matchAny;

    public IngredientFilterMenu(MenuType<?> type, int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(type, id, inventory, buffer);
    }

    public IngredientFilterMenu(MenuType<?> type, int id, Inventory inventory, ItemStack filter) {
        super(type, id, inventory, filter);
    }

    @Override
    protected void initAndReadInventory(ItemStack filter) {
        super.initAndReadInventory(filter);
		CompoundTag tag = filter.getOrCreateTag();
		respectNBT = tag.getBoolean("RespectNBT");
		blacklist = tag.getBoolean("Blacklist");
        matchAny = tag.getBoolean("Match Any");
    }

    @Override
    protected void saveData(ItemStack filter) {
        super.saveData(filter);
		CompoundTag tag = filter.getOrCreateTag();
		tag.putBoolean("RespectNBT", respectNBT);
		tag.putBoolean("Blacklist", blacklist);
        tag.putBoolean("Match Any", matchAny);
		if (respectNBT || blacklist)
			return;
		for (int i = 0; i < ghostInventory.getSlots(); i++)
			if (!ghostInventory.getStackInSlot(i)
				.isEmpty())
				return;
		filter.setTag(null);
    }

    public void setMatchAny(boolean value) {
        matchAny = value;
        saveData((ItemStack) contentHolder);
    }

    public static IngredientFilterMenu create(int id, Inventory inventory, ItemStack filter) {
        return new IngredientFilterMenu(CreateIngredientFilter.CONTENT_FILTER_MENU.get(), id, inventory, filter);
    }

	@Override
	protected int getPlayerInventoryXOffset() {
		return 38;
	}

	@Override
	protected int getPlayerInventoryYOffset() {
		return 121;
	}

	@Override
	protected void addFilterSlots() {
		int x = 23;
		int y = 25;
		for (int row = 0; row < 2; ++row)
			for (int col = 0; col < 9; ++col)
				this.addSlot(new SlotItemHandler(ghostInventory, col + row * 9, x + col * 18, y + row * 18));
	}

	@Override
	protected ItemStackHandler createGhostInventory() {
		return CreateIngredientFilter.CONTENT_FILTER_ITEM.get().getFilterItemHandler(contentHolder);
	}

}