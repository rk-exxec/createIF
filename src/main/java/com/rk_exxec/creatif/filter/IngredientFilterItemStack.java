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
import com.rk_exxec.creatif.util.MyDataComponents;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * This is also where the magic happens
 * 
 * IngredientFilterItemStack
 */
public class IngredientFilterItemStack extends FilterItemStack.ListFilterItemStack{ 

    public boolean matchAny;

    // contains output filter item as separate field
    public FilterItemStack containedOutputItem;


	public static IngredientFilterItemStack of(ItemStack filter) {
		if (!filter.isComponentsPatchEmpty() && filter.getItem() instanceof IngredientFilterItem item) {
			trimFilterComponents(filter);
			return item.makeStackWrapper(filter);
		}

		return new IngredientFilterItemStack(filter);
	}


    public IngredientFilterItemStack(ItemStack filter) {
        super(filter);
        boolean hasFilterItems = filter.has(AllDataComponents.FILTER_ITEMS);
        ItemStackHandler output = ((IngredientFilterItem) filter.getItem()).getFilterOutputHandler(filter);
        containedOutputItem = FilterItemStack.of(output.getStackInSlot(0));

        matchAny = hasFilterItems && filter.getOrDefault(MyDataComponents.FILTER_MATCH_ANY, false);
    }

    @Override
    public ItemStack item() {
		return super.item();
	}

    public IngredientFilterItem getFilterItem() {
		return (IngredientFilterItem) super.item().getItem();
	}

//#region custom filter functions

    /**
     * Tests both items and liquids for filter match
     * @param world
     * @param itemStacks
     * @param fluidStacks
     * @return
     */
    public boolean testIngredients(Level world, NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        int result=0;
        int total = containedItems.size();

        for (ItemStack stack : itemStacks) {
            CreatIF.LOGGER.debug("Checking list item "+ stack);
            //skip air
            if(stack.isEmpty() || Item.getId(stack.getItem()) == 0) continue;
            // calls super class FilteringBehaviour method
            if(testIngredients(world, stack, shouldRespectNBT)){
                result += 1;
                CreatIF.LOGGER.debug("Item "+ stack + " matches");
            }
        }
    

        for (FluidStack stack : fluidStacks) {
            CreatIF.LOGGER.debug("Checking list fluid "+ stack);
            if(stack.isEmpty()) continue;
            if(testIngredients(world, stack, shouldRespectNBT)){
                result += 1;
                CreatIF.LOGGER.debug("Fluid "+ stack + " matches");
            }
        }
        
        CreatIF.LOGGER.debug(result + " out of " + total);
        if(matchAny){
            CreatIF.LOGGER.debug("Match any");
            return result > 0;
        }
        else{
            CreatIF.LOGGER.debug("Match all");
            return result == total && total > 0;
        }
    }

    // below renamed filter functions act only on input filter
    public boolean testIngredients(Level world, ItemStack stack) {
		return testIngredients(world, stack, false);
	}

	public boolean testIngredients(Level world, FluidStack stack) {
		return testIngredients(world, stack, true);
	}
        

    public boolean testIngredients(Level world, ItemStack stack, boolean matchNBT) {
        for (FilterItemStack filterItemStack : containedItems)
            if (filterItemStack.test(world, stack, shouldRespectNBT))
                return !isBlacklist;
        return isBlacklist;
    }

    public boolean testIngredients(Level world, FluidStack stack, boolean matchNBT) {
        for (FilterItemStack filterItemStack : containedItems)
            if (filterItemStack.test(world, stack, shouldRespectNBT))
                return !isBlacklist;
        return isBlacklist;
    }
//#endregion

//#region default filter functions
// overriding these makes the filter act normally once used for the actual recipe check by the builtin create functions using the ouput filter
// they dont use blacklist or nbt checks
    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        return containedOutputItem.test(world, stack, false);
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        return containedOutputItem.test(world, stack, true);
    }
//#endregion

	private static void trimFilterComponents(ItemStack filter) {
		filter.remove(DataComponents.ENCHANTMENTS);
		filter.remove(DataComponents.ATTRIBUTE_MODIFIERS);
	}

}
// }

