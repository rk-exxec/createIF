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

import com.rk_exxec.creatif.CreatIF;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;



public class IngredientFilteringBehaviour extends FilteringBehaviour{

    public static final BehaviourType<IngredientFilteringBehaviour> TYPE = new BehaviourType<>();

    public <T> boolean test(NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        if(itemStacks.isEmpty() && fluidStacks.isEmpty()) return false;
        if(filter == null){
            CreatIF.LOGGER.debug("filter is null");
            return  false;
        }
        IngredientFilterItemStack filterItemStack = (IngredientFilterItemStack)filter;

		return !isActive() || filterItemStack.testIngredients(blockEntity.getLevel(), itemStacks, fluidStacks);
	}

    public IngredientFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    public IngredientFilterItemStack getFilterStack() {
        return (IngredientFilterItemStack) filter;
    }    
}
