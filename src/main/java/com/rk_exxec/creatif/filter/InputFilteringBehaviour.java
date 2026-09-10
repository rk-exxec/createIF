package com.rk_exxec.creatif.filter;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class InputFilteringBehaviour  extends FilteringBehaviour{

    ContentFilterItemStack filter;

    public InputFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
        //TODO Auto-generated constructor stub
    }

    public <T> boolean test(NonNullList<T> list) {
        if(list.isEmpty()) return false;

		return !isActive() || filter.test(blockEntity.getLevel(), list);
	}

    // public boolean testFluidIngredients(NonNullList<FluidStack> list) {
	// 	return !isActive() || filter.testFluid(blockEntity.getLevel(), list);
	// }


    // public boolean testFluid(FluidIngredient stack) {
	// 	return !isActive() || filter.test(blockEntity.getLevel(), list);
	// }
    
}
