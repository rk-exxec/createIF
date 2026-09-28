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

package com.rk_exxec.creatif.filter;

import java.util.ArrayList;
import java.util.List;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.gui.IngredientStack;
import com.rk_exxec.creatif.gui.IngredientStackHandler;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

/**
 * This is also where the magic happens
 * 
 * IngredientFilterItemStack
 */
public class IngredientFilterItemStack extends FilterItemStack{ 

    public boolean matchAny;
    public List<IngredientStack> containedItems;
    public boolean shouldRespectNBT;
    public boolean isBlacklist;

    // contains output filter item as separate field
    public IngredientStack containedOutputItem;


    public static IngredientFilterItemStack of(ItemStack filter) {
		if (filter.hasTag() && filter.getItem() instanceof IngredientFilterItem item) {
			trimFilterTag(filter);
			return item.makeStackWrapper(filter);
		}

		return new IngredientFilterItemStack(filter);
	}

    public IngredientFilterItemStack(ItemStack filter) {
        super(filter);
        boolean defaults = !filter.hasTag();

        containedItems = new ArrayList<>();
        IngredientStackHandler items = ((IngredientFilterItem) filter.getItem()).getFilterItemHandler(filter);
        for (int i = 0; i < items.getSlots(); i++) {
            IngredientStack stackInSlot = items.getIngredientStackInSlot(i);
            if (!stackInSlot.isEmpty())
                containedItems.add(stackInSlot);
        }

        shouldRespectNBT = defaults ? false
            : filter.getTag()
            .getBoolean("RespectNBT");
        isBlacklist = defaults ? false
            : filter.getTag()
            .getBoolean("Blacklist");
        IngredientStackHandler output = ((IngredientFilterItem) filter.getItem()).getFilterOutputHandler(filter);
        containedOutputItem = output.getIngredientStackInSlot(0);

        matchAny = defaults ? false
            : filter.getTag()
            .getBoolean("Match Any");
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
            CreatIF.LOGGER.debug("Checking list fluid "+ stack.getDisplayName());
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
		return testIngredients(world, stack, false);
	}
        

    public boolean testIngredients(Level world, ItemStack stack, boolean matchNBT) {
        for (IngredientStack filterItemStack : containedItems)
            if ((filterItemStack.getItem() instanceof FilterItem) && IngredientFilterItemStack.of(filterItemStack.itemStack()).test(world, stack, shouldRespectNBT))
                return !isBlacklist;
            else if(!(IngredientFilterItem.testDirect(filterItemStack, stack, matchNBT) ^ isBlacklist)) continue;
            else return true;
        return isBlacklist;
    }

    public boolean testIngredients(Level world, FluidStack stack, boolean matchNBT) {
        for (IngredientStack filterItemStack : containedItems)
            if ((filterItemStack.getItem() instanceof FilterItem) && IngredientFilterItemStack.of(filterItemStack.itemStack()).test(world, stack, shouldRespectNBT))
                return !isBlacklist;
            else if(!(IngredientFilterItem.testDirect(filterItemStack, stack, matchNBT) ^ isBlacklist)) continue;
            else return true;
        return isBlacklist;
    }
//#endregion

//#region default filter functions
// overriding these makes the filter act normally once used for the actual recipe check by the builtin create functions using the ouput filter
// they dont use blacklist or nbt checks
    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        if(containedOutputItem.getItem() instanceof FilterItem) return IngredientFilterItemStack.of(containedOutputItem.itemStack()).test(world, stack, false);
        if (isEmpty())
			return true;
		return IngredientFilterItem.testDirect(containedOutputItem, stack, matchNBT);
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        if(containedOutputItem.getItem() instanceof FilterItem) return IngredientFilterItemStack.of(containedOutputItem.itemStack()).test(world, stack, false);
        if (isEmpty())
			return true;
		return IngredientFilterItem.testDirect(containedOutputItem, stack, matchNBT);
    }
//#endregion

    private static void trimFilterTag(ItemStack filter) {
		CompoundTag stackTag = filter.getTag();
		stackTag.remove("Enchantments");
		stackTag.remove("AttributeModifiers");
	}

    // private void resolveFluid(Level world) {
	// 	if (!fluidExtracted) {
	// 		fluidExtracted = true;
	// 		if (GenericItemEmptying.canItemBeEmptied(world, filterItemStack))
	// 			filterFluidStack = GenericItemEmptying.emptyItem(world, filterItemStack, true)
	// 				.getFirst();
	// 	}
	// }
}
// }

