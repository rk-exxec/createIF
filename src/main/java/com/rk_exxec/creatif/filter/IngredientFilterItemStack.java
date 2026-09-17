package com.rk_exxec.creatif.filter;

import java.util.ArrayList;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.datafixers.types.Type.TypeError;
import com.rk_exxec.creatif.CreateIngredientFilter;
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

    public class IngredientFilterItemStack extends FilterItemStack.ListFilterItemStack{ 
        // public List<FilterItemStack> containedItems;
        // public boolean shouldRespectNBT;
        // public boolean isBlacklist;
        public boolean matchAny;

        public IngredientFilterItemStack(ItemStack filter) {
            super(filter);
            boolean defaults = !filter.hasTag();
            matchAny = defaults ? false
                : filter.getTag()
                .getBoolean("Match Any");
        }

        public <T> boolean test(Level world, NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
            int result=0;
            int total=0;
            // CreateIngredientFilter.LOGGER.debug("Made it to CFIS");
            // CreateIngredientFilter.LOGGER.debug(list.toString());

            for (ItemStack stack : itemStacks) {
                // CreateIngredientFilter.LOGGER.debug("Checking list item "+ stack);
                //skip air
                if(Item.getId(stack.getItem()) == 0) continue;
                // calls super class FilteringBehaviour method
                if(test(world, stack, shouldRespectNBT)){
                    result += 1;
                    CreateIngredientFilter.LOGGER.debug("Item "+ stack + " matches");
                }
                total += 1;
            }
        

            for (FluidStack stack : fluidStacks) {
                // CreateIngredientFilter.LOGGER.debug("Checking list fluid "+ stack);
                if(stack == FluidStack.EMPTY) continue;
                if(test(world, stack, shouldRespectNBT)){
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

