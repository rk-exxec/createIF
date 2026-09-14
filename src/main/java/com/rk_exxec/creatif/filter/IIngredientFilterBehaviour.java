package com.rk_exxec.creatif.filter;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IIngredientFilterBehaviour {
    public <T> boolean test(NonNullList<ItemStack> itemList,NonNullList<FluidStack> fluidList);
}
