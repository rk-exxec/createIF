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

package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.util.MyDataComponents;
import com.rk_exxec.creatif.util.MyItems;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;

import java.util.List;


public class IngredientFilterMenu extends AbstractFilterMenu {
    boolean respectNBT;
	boolean blacklist;
    public boolean matchAny;
	public ItemStackHandler outputGhostInventory;


	public IngredientFilterMenu(int id, Inventory inventory, ItemStack stack){
		this(MyMenuTypes.INGREDIENT_FILTER.get(), id, inventory, stack);
	}

    public IngredientFilterMenu(MenuType<?> type, int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        super(type, id, inventory, buffer);
    }

    public IngredientFilterMenu(MenuType<?> type, int id, Inventory inventory, ItemStack filter) {
        super(type, id, inventory, filter);
    }

	public static IngredientFilterMenu create(int id, Inventory inventory, ItemStack filter) {
        return new IngredientFilterMenu(MyMenuTypes.INGREDIENT_FILTER.get(), id, inventory, filter);
    }


	@Override
	protected void saveData(ItemStack filterItem) {
		super.saveData(filterItem);
		filterItem.set(AllDataComponents.FILTER_ITEMS_RESPECT_NBT, respectNBT);
		filterItem.set(AllDataComponents.FILTER_ITEMS_BLACKLIST, blacklist);
		filterItem.set(MyDataComponents.FILTER_MATCH_ANY, matchAny);
		saveOutputData(filterItem);
		if (respectNBT || blacklist)
			return;
		for (int i = 0; i < ghostInventory.getSlots(); i++)
			if (!ghostInventory.getStackInSlot(i)
				.isEmpty())
				return;

		filterItem.remove(AllDataComponents.FILTER_ITEMS_RESPECT_NBT);
		filterItem.remove(AllDataComponents.FILTER_ITEMS_BLACKLIST);
		filterItem.remove(MyDataComponents.FILTER_MATCH_ANY);
	}

    @Override
    protected void initAndReadInventory(ItemStack filter) {
        super.initAndReadInventory(filter);

		respectNBT = filter.getOrDefault(AllDataComponents.FILTER_ITEMS_RESPECT_NBT, false);
		blacklist = filter.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, false);
        matchAny = filter.getOrDefault(MyDataComponents.FILTER_MATCH_ANY, false);
    }

	protected void saveOutputData(ItemStack contentHolder) {
		for (int i = 0; i < outputGhostInventory.getSlots(); i++) {
			if (!outputGhostInventory.getStackInSlot(i).isEmpty()) {
				contentHolder.set(MyDataComponents.FILTER_OUTPUT, ItemHelper.containerContentsFromHandler(outputGhostInventory));
				return;
			}
		}
		contentHolder.remove(MyDataComponents.FILTER_OUTPUT);
	}

    public void setMatchAny(boolean value) {
        matchAny = value;
        saveData((ItemStack) contentHolder);
    }



	@Override
	protected int getPlayerInventoryXOffset() {
		return 38;
	}

	@Override
	protected int getPlayerInventoryYOffset() {
		return 134+22;
	}

	@Override
	protected void addFilterSlots() {
		int x = 23;
		int y = 25;
		int nCols = 5;
		
		for (int row = 0; row < 4; ++row)
			for (int col = 0; col < 5; ++col)
				this.addSlot(new SlotItemHandler(ghostInventory, col + row * nCols, x + col * 18, y + row * 18));
		this.addSlot(new SlotItemHandler(outputGhostInventory, 0, 164, 52));
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (this.isInSlot(slotId) && clickTypeIn == ClickType.THROW && clickTypeIn == ClickType.CLONE) 
			return;

		ItemStack held = getCarried();
		
		int slot = slotId - 36;

		if(slot>0 && held.getItem() instanceof IngredientFilterItem) return; // prevent nesting
		// check if output inventory was clicked, if yes write to that isnted of normal ghostInventory
		if(slot >= 0 && ghostInventory.getSlots() - slot <= 0 ) {

			if (clickTypeIn == ClickType.CLONE) {
				if (player.isCreative() && held.isEmpty()) {
					ItemStack stackInSlot = outputGhostInventory.getStackInSlot(0)
							.copy();
					stackInSlot.setCount(stackInSlot.getMaxStackSize());
					setCarried(stackInSlot);
					return;
				}
				return;
			}

			ItemStack insert;
			if (held.isEmpty()) {
				insert = ItemStack.EMPTY;
			} else {
				insert = held.copy();
				insert.setCount(1);
			}
			outputGhostInventory.setStackInSlot(0, insert);
			getSlot(slotId).setChanged();
		}
		else super.clicked(slotId, dragType, clickTypeIn, player);
	}

	
	@Override
	protected ItemStackHandler createGhostInventory() {
		outputGhostInventory = MyItems.INGREDIENT_FILTER_ITEM.get().getFilterOutputHandler(contentHolder);
		return MyItems.INGREDIENT_FILTER_ITEM.get().getFilterItemHandler(contentHolder);
	}


	@Override
	public void clearContents() {
		for (int i = 0; i < outputGhostInventory.getSlots(); i++)
			outputGhostInventory.setStackInSlot(i, ItemStack.EMPTY);
		super.clearContents();
	}

	public void applyRecipe(List<ItemStack> ingredients, ItemStack output) {
		for (int i = 0; i < ghostInventory.getSlots(); i++)
			ghostInventory.setStackInSlot(i, i < ingredients.size() ? ingredients.get(i).copy() : ItemStack.EMPTY);
		outputGhostInventory.setStackInSlot(0, output.copy());
		saveData((ItemStack) contentHolder);
	}


	public CompoundTag createRecipeData() {
		CompoundTag data = new CompoundTag();
		data.put("Items", ghostInventory.serializeNBT(player.registryAccess()));
		data.put("Output", outputGhostInventory.serializeNBT(player.registryAccess()));
		return data;
	}

	public void applyRecipeData(CompoundTag tag) {
		ghostInventory.deserializeNBT(player.registryAccess(), tag.getCompound("Items"));
		outputGhostInventory.deserializeNBT(player.registryAccess(), tag.getCompound("Output"));
		saveData((ItemStack) contentHolder);
	}

}