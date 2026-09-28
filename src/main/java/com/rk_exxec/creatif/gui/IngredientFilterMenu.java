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

import com.rk_exxec.creatif.filter.IngredientFilterItem;
import com.rk_exxec.creatif.util.MyItems;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.foundation.gui.menu.IClearableMenu;
import com.simibubi.create.foundation.gui.menu.MenuBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import java.util.List;


public class IngredientFilterMenu extends MenuBase<ItemStack> implements IClearableMenu {
    boolean respectNBT;
	boolean blacklist;

	public IngredientStackHandler ghostInventory;
    public boolean matchAny;
	public IngredientStackHandler outputGhostInventory;
	public boolean useRawFluids;

	private final int PLAYER_INV_SLOTS = 36;
	public final int INPUT_INV_SIZE = 20;
	public final int OUTPUT_INV_SIZE = 0;

	public final int INV_OFFSET_X = 38;
	public final int INV_OFFSET_Y = 134+22;

	public final int INGR_SLOT_OFFSET_X = 23;
	public final int INGR_SLOT_OFFSET_Y = 25;
	public final int INGR_INV_N_COLS = 5;
	public final int INGR_INV_N_ROWS = 4;

	public final int OUTP_SLOT_OFFSET_X = 164;
	public final int OUTP_SLOT_OFFSET_Y = 52;
	public final int OUTP_INV_N_COLS = 1;
	public final int OUTP_INV_N_ROWS = 1;
	private final int SLOT_SPACING = 18;

    public IngredientFilterMenu(MenuType<?> type, int id, Inventory inventory, FriendlyByteBuf buffer) {
		super(type, id, inventory, buffer);
		init(inventory, createOnClient(buffer));
    }

    public IngredientFilterMenu(MenuType<?> type, int id, Inventory inventory, ItemStack filter) {
		super(type, id, inventory, filter);
		init(inventory, filter);
    }

	public static IngredientFilterMenu create(int id, Inventory inventory, ItemStack filter) {
        return new IngredientFilterMenu(MyMenuTypes.INGREDIENT_FILTER.get(), id, inventory, filter);
    }

	//#region data handling

	@Override 
    protected void initAndReadInventory(ItemStack filter) {
        createGhostInventory(filter);
		CompoundTag tag = filter.getOrCreateTag();
		respectNBT = tag.getBoolean("RespectNBT");
		blacklist = tag.getBoolean("Blacklist");
        matchAny = tag.getBoolean("Match Any");

    }

	protected void createGhostInventory(ItemStack contentHolder) {
		outputGhostInventory = MyItems.INGREDIENT_FILTER_ITEM.get().getFilterOutputHandler(contentHolder);
		ghostInventory = MyItems.INGREDIENT_FILTER_ITEM.get().getFilterItemHandler(contentHolder);
	}

	@Override
    protected void saveData(ItemStack filter) {
		CompoundTag tag = filter.getOrCreateTag();
		tag.put("Items", ghostInventory.serializeNBT());
		tag.put("Output", outputGhostInventory.serializeNBT());
		tag.putBoolean("RespectNBT", respectNBT);
		tag.putBoolean("Blacklist", blacklist);
        tag.putBoolean("Match Any", matchAny);
		if (respectNBT || blacklist || matchAny)
			return;
		boolean empty = true;
		for (int i = 0; i < ghostInventory.getSlots(); i++)
			empty &= ghostInventory.getIngredientStackInSlot(i).isEmpty();
		for (int i = 0; i < outputGhostInventory.getSlots(); i++)
			empty &= outputGhostInventory.getIngredientStackInSlot(i).isEmpty();
		if(!empty) return;
		filter.setTag(null);
    }


	@Override 
	@OnlyIn(Dist.CLIENT)
	protected ItemStack createOnClient(FriendlyByteBuf extraData) {
		return extraData.readItem();
	}
	//#endregion

	protected int getPlayerInventoryXOffset() {
		return INV_OFFSET_X;
	}

	protected int getPlayerInventoryYOffset() {
		return INV_OFFSET_Y;
	}



	//#region EMI / JEI compat functions
	public void applyRecipe(List<IngredientStack> ingredients, List<IngredientStack> outputs) {
		for (int i = 0; i < ghostInventory.getSlots(); i++){
			ghostInventory.setStackInSlot(i, i < ingredients.size() ? ingredients.get(i).copy() : IngredientStack.EMPTY);
		}
		for (int i = 0; i < outputGhostInventory.getSlots(); i++)
			outputGhostInventory.setStackInSlot(i, i < outputs.size() ? outputs.get(i).copy() : IngredientStack.EMPTY);
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
		saveData((ItemStack) contentHolder);
	}
//#endregion

//#region slot manipulation
	protected void addFilterSlots() {
		int x = INGR_SLOT_OFFSET_X;
		int y = INGR_SLOT_OFFSET_Y;
		int s= SLOT_SPACING;
		
		for (int row = 0; row < INGR_INV_N_ROWS; ++row)
			for (int col = 0; col < INGR_INV_N_COLS; ++col)
				this.addSlot(new IngredientSlot(
					ghostInventory, col + row * INGR_INV_N_COLS, x + col * s, y + row * s));

		x = OUTP_SLOT_OFFSET_X;
		y = OUTP_SLOT_OFFSET_Y;

		for (int row = 0; row < OUTP_INV_N_ROWS; ++row)
			for (int col = 0; col < OUTP_INV_N_ROWS; ++col)
				this.addSlot(new IngredientSlot(
					outputGhostInventory, col + row * OUTP_INV_N_COLS, x + col * s, y + row * s)); // position of output slot
	}

	@Override 
	protected void addSlots() {
		addPlayerSlots(getPlayerInventoryXOffset(), getPlayerInventoryYOffset());
		addFilterSlots();
	}



	@Override 
	public void clearContents() {
		for (int i = 0; i < outputGhostInventory.getSlots(); i++)
			outputGhostInventory.setStackInSlot(i, ItemStack.EMPTY);
		for (int i = 0; i < ghostInventory.getSlots(); i++)
			ghostInventory.setStackInSlot(i, ItemStack.EMPTY);
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (this.isInSlot(slotId) && (clickTypeIn == ClickType.THROW || clickTypeIn == ClickType.CLONE)) 
			return;

		if (slotId < PLAYER_INV_SLOTS) {
			super.clicked(slotId, dragType, clickTypeIn, player);
			return;
		}
		ItemStack held = getCarried();
		int slot = slotId - PLAYER_INV_SLOTS;
		if(slot>=0 && held.getItem() instanceof IngredientFilterItem) return; // prevent nesting

		if(slot >= 0 && slot < INPUT_INV_SIZE+OUTPUT_INV_SIZE) {
			IngredientStackHandler targetInv;
			// check if output inventory was clicked, if yes write to that isnted of normal ghostInventory
			if(slot >= INPUT_INV_SIZE) {
				slot -= INPUT_INV_SIZE;
				targetInv = outputGhostInventory;
			}
			else if(slot < INPUT_INV_SIZE)
				targetInv = ghostInventory;
			else return;

			if (clickTypeIn == ClickType.CLONE) {
				if (player.isCreative() && held.isEmpty()) {
					IngredientStack stackInSlot;
					stackInSlot = targetInv.getIngredientStackInSlot(slot).copy();
					if(stackInSlot.isFluid())return;
					stackInSlot.setCount(stackInSlot.itemStack().getMaxStackSize());
					setCarried(stackInSlot.itemStack());
					return;
				}
				return;
			}
			IngredientStack insert;
			if (held.isEmpty()) {
				insert = IngredientStack.EMPTY;
			} // right clicking buckets or bottles gives raw liquid
			else if(clickTypeIn == ClickType.PICKUP && dragType == 1 && GenericItemEmptying.canItemBeEmptied(player.level(), held)){
				insert = IngredientStack.of(GenericItemEmptying.emptyItem(player.level(),held.copy(),true).getFirst().copy()).copyWithCount(1);
			}
			else
			{
				insert = IngredientStack.of(held.copy()).copyWithCount(1);
			}
			targetInv.setStackInSlot(slot, insert);
			getSlot(slotId).setChanged();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return playerInventory.getSelected() == contentHolder;
	}

	protected boolean isInSlot(int index) {
		// Inventory has the hotbar as 0-8, but menus put the hotbar at 27-35
		return index >= 27 && index - 27 == playerInventory.selected;
	}


	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
		return slotIn.container == playerInventory && !this.isInSlot(slotIn.index);
	}

	@Override
	public boolean canDragTo(Slot slotIn) {
		return slotIn.container == playerInventory;
	}

	@Override
	protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
		return false;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		if (index < 36) {
			Slot slot = this.slots.get(index);
			ItemStack stackToInsert = slot.getItem();
			for (int i = 0; i < ghostInventory.getSlots(); i++) {
				IngredientStack stack = ghostInventory.getIngredientStackInSlot(i);
				if (ItemHandlerHelper.canItemStacksStack(stack.itemStack(), stackToInsert))
					break;
				if (stack.isEmpty()) {
					ItemStack copy = stackToInsert.copy();
					copy.setCount(1);
					ghostInventory.setStackInSlot(i, copy);
					getSlot(i + 36).setChanged();
					break;
				}
			}
		} else {
			IngredientStack stack = ghostInventory.getIngredientStackInSlot(index - 36);
			stack.itemStack().shrink(1);
			if(!stack.isFluid())
				ghostInventory.setStackInSlot(index - 36, stack.itemStack());
			getSlot(index).setChanged();
		}
		return ItemStack.EMPTY;
	}

//#endregion
}