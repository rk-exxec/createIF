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

import com.rk_exxec.creatif.gui.IngredientStack;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Overload of Slot and replacement for SlotItemHandler for rendering mixed items and fluids in the same inventory
 * IngredientSlot
 */
public class IngredientSlot extends Slot {

    private final IngredientStackHandler handler;
    private final int index;

    public IngredientSlot(
            IngredientStackHandler handler,
            int index,
            int x,
            int y) {
        super(new SimpleContainer(handler.getSlots()), index, x, y);
        this.handler = handler;
        this.index = index;
    }

    public IngredientStack getIngredientStack() {
        return handler.getIngredientStackInSlot(index);
    }

    @Override
    public ItemStack getItem() {
        IngredientStack stack = handler.getIngredientStackInSlot(index);
        return stack.isFluid() ? ItemStack.EMPTY : stack.getItemStack().orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean hasItem() {
        return !handler.getIngredientStackInSlot(index).isEmpty();
    }

    public void setIngredientStack(IngredientStack stack) {
        handler.setStackInSlot(index, stack);
        setChanged();
    }
}