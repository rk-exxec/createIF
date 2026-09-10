package com.rk_exxec.creatif.filter;

import java.util.ArrayList;

import java.util.List;

import com.mojang.datafixers.types.Type.TypeError;
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

    public <T> boolean test(Level world, NonNullList<T> list) {
        int result=0;
        int total=0;

        if(list.get(0) instanceof ItemStack){
            for (T stack : list) {
                // calls super class FilteringBehaviour method
                if(test(world, (ItemStack)stack, shouldRespectNBT)){
                    result += 1;
                }
                total += 1;
            }
        }
        else if(list.get(0) instanceof FluidStack){
            for (T stack : list) {
                if(test(world, (FluidStack)stack, shouldRespectNBT)){
                    result += 1;
                }
                total += 1;
            }
        }
        else throw new IllegalArgumentException("How did we get here? \nContentFilterItemStack.test() was handed a non ItemStack/FluidStack list of items.");
  
        if(matchAny){
            return result > 0;
        }
        else{
            return result == total;
        }
    }

    public boolean testFluid(Level world, NonNullList<FluidStack> list) {
        int result=0;
        int total=0;

        for (FluidStack stack : list) {
            if(test(world, stack, shouldRespectNBT)){
                result += 1;
            }
            total += 1;
        }
        
        if(matchAny){
            return result > 0;
        }
        else{
            return result == total;
        }
    }
}

