package com.rk_exxec.creatif.filter;

import java.util.ArrayList;

import java.util.List;
import com.simibubi.create.content.logistics.filter.*;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

public class ContentFilterItemStack extends FilterItemStack {

    public List<FilterItemStack> containedItems;
    public boolean shouldRespectNBT;
    public boolean isBlacklist;
    public boolean matchAny;

    public ContentFilterItemStack(ItemStack filter) {
        super(filter);
        boolean defaults = !filter.hasTag();

        containedItems = new ArrayList<>();
        ItemStackHandler items = ((ListFilterItem) filter.getItem()).getFilterItemHandler(filter);
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (!stackInSlot.isEmpty())
                containedItems.add(FilterItemStack.of(stackInSlot));
        }

        shouldRespectNBT = defaults ? false
            : filter.getTag()
            .getBoolean("RespectNBT");
        isBlacklist = defaults ? false
            : filter.getTag()
            .getBoolean("Blacklist");
        matchAny = defaults ? false
            : filter.getTag()
            .getBoolean("Match Any");
    }

    public boolean test(Level world, NonNullList<Ingredient> list) {
        int result=0;
        int total=0;
        for (Ingredient ingredient : list) {
            for (ItemStack stack : ingredient.getItems()) {
                if(test(world, stack, shouldRespectNBT)){
                    result += 1;
                }
                total += 1;
            }
        }
        if(matchAny){
            return result > 0;
        }
        else{
            return result == total;
        }
    }

    public boolean testFluid(Level world, NonNullList<FluidIngredient> list) {
        int result=0;
        int total=0;
        for (FluidIngredient ingredient : list) {
            for (FluidStack stack : ingredient.getMatchingFluidStacks()) {
                if(test(world, stack, shouldRespectNBT)){
                    result += 1;
                }
                total += 1;
            }
        }
        if(matchAny){
            return result > 0;
        }
        else{
            return result == total;
        }
    }
}

