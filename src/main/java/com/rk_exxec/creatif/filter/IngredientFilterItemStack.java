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


public class IngredientFilterItemStack extends FilterItemStack.ListFilterItemStack{ 

    public boolean matchAny;
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

    public <T> boolean testIngredients(Level world, NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        int result=0;
        int total=0;
        // CreateIngredientFilter.LOGGER.debug("Made it to CFIS");
        // CreateIngredientFilter.LOGGER.debug(list.toString());

        

        for (ItemStack stack : itemStacks) {
            CreateIngredientFilter.LOGGER.debug("Checking list item "+ stack);
            //skip air
            if(stack.isEmpty() || Item.getId(stack.getItem()) == 0) continue;
            // calls super class FilteringBehaviour method
            if(testIngredients(world, stack, shouldRespectNBT)){
                result += 1;
                CreateIngredientFilter.LOGGER.debug("Item "+ stack + " matches");
            }
            total += 1;
        }
    

        for (FluidStack stack : fluidStacks) {
            CreateIngredientFilter.LOGGER.debug("Checking list fluid "+ stack);
            if(stack.isEmpty()) continue;
            if(testIngredients(world, stack, shouldRespectNBT)){
                result += 1;
                CreateIngredientFilter.LOGGER.debug("Fluid "+ stack + " matches");
            }
            total += 1;
        }
        
        CreateIngredientFilter.LOGGER.debug(result + " out of " + total);
        if(matchAny){
            CreateIngredientFilter.LOGGER.debug("Match any");
            return result > 0;
        }
        else{
            CreateIngredientFilter.LOGGER.debug("Match all");
            return result == containedItems.size() && containedItems.size() > 0;
        }
    }

    @Override
    public ItemStack item() {
		return super.item();
	}

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

    // overriding these makes the filter act normally once used for the actual recipe check by the builtin create functions
    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        if (containedOutputItem.test(world, stack, shouldRespectNBT))
            return !isBlacklist;
        return isBlacklist;
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        if (containedOutputItem.test(world, stack, shouldRespectNBT))
            return !isBlacklist;
        return isBlacklist;
    }
    //========

    private static void trimFilterTag(ItemStack filter) {
		CompoundTag stackTag = filter.getTag();
		stackTag.remove("Enchantments");
		stackTag.remove("AttributeModifiers");
	}
}
// }

