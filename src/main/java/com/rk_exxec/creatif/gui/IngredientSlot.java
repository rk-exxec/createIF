package com.rk_exxec.creatif.gui;

import com.rk_exxec.creatif.filter.IngredientStack;
import com.rk_exxec.creatif.filter.IngredientStackHandler;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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