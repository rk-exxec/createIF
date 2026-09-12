package com.rk_exxec.creatif.filter;
import java.util.ArrayList;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.datafixers.types.Type.TypeError;
import com.rk_exxec.creatif.CreateContentFilter;
import com.simibubi.create.content.logistics.filter.*;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

// @Mixin(FilterItemStack.class)
// public class FilterItemStackMixin {

    public class ContentFilterItemStack extends FilterItemStack.ListFilterItemStack{ 
        // public List<FilterItemStack> containedItems;
        // public boolean shouldRespectNBT;
        // public boolean isBlacklist;
        public boolean matchAny;

        public ContentFilterItemStack(ItemStack filter) {
            super(filter);
            boolean defaults = !filter.hasTag();
            matchAny = defaults ? false
                : filter.getTag()
                .getBoolean("Match Any");
        }

        public <T> boolean test(Level world, NonNullList<T> list) {
            int result=0;
            int total=0;
            CreateContentFilter.LOGGER.debug("Made it to CFIS");
            CreateContentFilter.LOGGER.debug(list.toString());
            if(list.get(0) instanceof ItemStack){
                for (T stack : list) {
                    CreateContentFilter.LOGGER.debug("Checking list item "+ stack);
                    //skip air
                    if(Item.getId(((ItemStack)stack).getItem()) == 0) continue;
                    // calls super class FilteringBehaviour method
                    if(test(world, (ItemStack)stack, shouldRespectNBT)){
                        result += 1;
                        CreateContentFilter.LOGGER.debug("Item "+ stack + " matches");
                    }
                    total += 1;
                }
            }
            else if(list.get(0) instanceof FluidStack){
                for (T stack : list) {
                    CreateContentFilter.LOGGER.debug("Checking list fluid "+ stack);
                    if(stack == FluidStack.EMPTY) continue;
                    if(test(world, (FluidStack)stack, shouldRespectNBT)){
                        result += 1;
                        CreateContentFilter.LOGGER.debug("Fluid "+ stack + " matches");
                    }
                    total += 1;
                }
            }
            else throw new IllegalArgumentException("How did we get here? \nContentFilterItemStack.test() was handed a non ItemStack/FluidStack list of items.");
            
            CreateContentFilter.LOGGER.debug(result + " out of " + total);
            if(matchAny){
                CreateContentFilter.LOGGER.debug("Match any");
                return result > 0;
            }
            else{
                CreateContentFilter.LOGGER.debug("Match all");
                return result == total && total > 0;
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
// }

