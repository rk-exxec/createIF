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

import com.rk_exxec.creatif.util.MyItems;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;

import java.util.List;


public class IngredientFilterMenu extends AbstractFilterMenu {
    boolean respectNBT;
	boolean blacklist;
    public boolean matchAny;
	public ItemStackHandler outputGhostInventory;

	private final int PLAYER_INV_SLOTS = 36;
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
		outputGhostInventory = MyItems.INGREDIENT_FILTER_ITEM.get().getFilterOutputHandler(contentHolder);
		respectNBT = tag.getBoolean("RespectNBT");
		blacklist = tag.getBoolean("Blacklist");
        matchAny = tag.getBoolean("Match Any");
    }

    @Override
    protected void saveData(ItemStack filter) {
        super.saveData(filter);
		CompoundTag tag = filter.getOrCreateTag();
		tag.put("Output", outputGhostInventory.serializeNBT());
		tag.putBoolean("RespectNBT", respectNBT);
		tag.putBoolean("Blacklist", blacklist);
        tag.putBoolean("Match Any", matchAny);
		if (respectNBT || blacklist || matchAny)
			return;
		for (int i = 0; i < ghostInventory.getSlots(); i++)
			if (!ghostInventory.getStackInSlot(i)
				.isEmpty())
				return;
		if(!outputGhostInventory.getStackInSlot(0).isEmpty()) return;
		tag.remove("Items");
		tag.remove("Output");
		tag.remove("RespectNBT");
		tag.remove("Blacklist");
		tag.remove("Match Any");
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	protected ItemStack createOnClient(FriendlyByteBuf extraData) {
		return extraData.readItem();
	}

    public void setMatchAny(boolean value) {
        matchAny = value;
        saveData((ItemStack) contentHolder);
    }

    public static IngredientFilterMenu create(int id, Inventory inventory, ItemStack filter) {
        return new IngredientFilterMenu(MyMenuTypes.INGREDIENT_FILTER.get(), id, inventory, filter);
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
		int nRows = 4;
		
		for (int row = 0; row < nRows; ++row)
			for (int col = 0; col < nCols; ++col)
				this.addSlot(new SlotItemHandler(ghostInventory, col + row * nCols, x + col * 18, y + row * 18));
		this.addSlot(new SlotItemHandler(outputGhostInventory, 0, 164, 52)); // position of output slot
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (this.isInSlot(slotId) && (clickTypeIn == ClickType.THROW || clickTypeIn == ClickType.CLONE)) 
			return;

		ItemStack held = getCarried();
		
		int slot = slotId - PLAYER_INV_SLOTS;

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
		for (int i = 0; i < ghostInventory.getSlots(); i++){
			ghostInventory.setStackInSlot(i, i < ingredients.size() ? ingredients.get(i).copy() : ItemStack.EMPTY);
			// getSlot(i+PLAYER_INV_SLOTS).setChanged();
		}
		outputGhostInventory.setStackInSlot(0, output.copy());
		// getSlot(ghostInventory.getSlots()+PLAYER_INV_SLOTS).setChanged();
		// saveData((ItemStack) contentHolder.copy());
	}

	public CompoundTag createRecipeData() {
		CompoundTag data = new CompoundTag();
		data.put("Items", ghostInventory.serializeNBT());
		data.put("Output", outputGhostInventory.serializeNBT());
		return data;
	}

	public void applyRecipeData(CompoundTag data) {
		ghostInventory.deserializeNBT(data.getCompound("Items"));
		outputGhostInventory.deserializeNBT(data.getCompound("Output"));
		// saveData((ItemStack) contentHolder.copy());
	}

}