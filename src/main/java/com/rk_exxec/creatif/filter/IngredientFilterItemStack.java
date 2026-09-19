package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.CreateIngredientFilter;
import com.simibubi.create.content.logistics.filter.FilterItemStack;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

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
		if (filter.hasTag() && filter.getItem() instanceof IngredientFilterItem item) {
			trimFilterTag(filter);
			return item.makeStackWrapper(filter);
		}

		return new IngredientFilterItemStack(filter);
	}

    public IngredientFilterItemStack(ItemStack filter) {
        super(filter);
        boolean defaults = !filter.hasTag();
        ItemStackHandler output = ((IngredientFilterItem) filter.getItem()).getFilterOutputHandler(filter);
        containedOutputItem = FilterItemStack.of(output.getStackInSlot(0));

        matchAny = defaults ? false
            : filter.getTag()
            .getBoolean("Match Any");
    }

    @Override
    public ItemStack item() {
		return super.item();
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
            CreateIngredientFilter.LOGGER.debug("Checking list item "+ stack);
            //skip air
            if(stack.isEmpty() || Item.getId(stack.getItem()) == 0) continue;
            // calls super class FilteringBehaviour method
            if(testIngredients(world, stack, shouldRespectNBT)){
                result += 1;
                CreateIngredientFilter.LOGGER.debug("Item "+ stack + " matches");
            }
        }
    

        for (FluidStack stack : fluidStacks) {
            CreateIngredientFilter.LOGGER.debug("Checking list fluid "+ stack);
            if(stack.isEmpty()) continue;
            if(testIngredients(world, stack, shouldRespectNBT)){
                result += 1;
                CreateIngredientFilter.LOGGER.debug("Fluid "+ stack + " matches");
            }
        }
        
        CreateIngredientFilter.LOGGER.debug(result + " out of " + total);
        if(matchAny){
            CreateIngredientFilter.LOGGER.debug("Match any");
            return result > 0;
        }
        else{
            CreateIngredientFilter.LOGGER.debug("Match all");
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

    private static void trimFilterTag(ItemStack filter) {
		CompoundTag stackTag = filter.getTag();
		stackTag.remove("Enchantments");
		stackTag.remove("AttributeModifiers");
	}
}
// }

