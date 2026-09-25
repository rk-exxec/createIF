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

package com.rk_exxec.creatif.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterItemStack;
import com.rk_exxec.creatif.interfaces.IFilteringBehaviourMixin;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 
 * BasinRecipeMixin
 * 
 * where the magic happens
 */
@Mixin(BasinOperatingBlockEntity.class)
public class BasinOperatingBlockEntityMixin {

    @Shadow(remap=false) 
    protected Optional<BasinBlockEntity> getBasin(){
        throw new AssertionError("Shadow error");
    }

    int numValidItems = 0;
    int numValidFluids = 0;

    // wrap to check if ingredient filter is used, and if yes if conditions satisfied.
    @WrapMethod(method="getMatchingRecipes", remap=false)
    public List<Recipe<?>> checkIngredients(Operation<List<Recipe<?>>> original){
        numValidItems = 0;
        numValidFluids = 0;
        // check basin validity, copied from wrapped method
		Optional<BasinBlockEntity> $basin = getBasin();
		BasinBlockEntity basin;
		if ($basin.isEmpty() || (basin = $basin.get()).isEmpty())
			return new ArrayList<>();


        CreatIF.LOGGER.debug("This is the custom filter mixin");

        FilteringBehaviour filter = basin.getFilter();
        if (filter == null){
            CreatIF.LOGGER.debug("filter is null");
            return original.call();
        }
        FilterItemStack filterStack = ((IFilteringBehaviourMixin) (Object) filter).getFilterStack();
        // Check if the filter is of my type
        CreatIF.LOGGER.debug("Filter has class " + filter.getFilter().getDescriptionId());
        if (!(filterStack instanceof IngredientFilterItemStack inputFilter)){
            CreatIF.LOGGER.debug("Not ingredient filter - vanilla times");
            return original.call();
        }
        // cast filter to make custom functions available
        // IngredientFilterItemStack inputFilter = IngredientFilterItemStack.of(filter.getFilter());

        Level level = basin.getLevel();
        IItemHandler availableItems = level.getCapability(ItemHandler.BLOCK, basin.getBlockPos(), null);
        IFluidHandler availableFluids = level.getCapability(FluidHandler.BLOCK, basin.getBlockPos(), null);


        // build list of available liquids and fluids in the basin
        NonNullList<ItemStack> inputItems = NonNullList.create();
        for (int i = 0; i < availableItems.getSlots(); i++) {
            var stack = availableItems.getStackInSlot(i);
            if(stack.isEmpty() || Item.getId(stack.getItem()) == 0) continue;
            inputItems.add(stack);
        }
        NonNullList<FluidStack> inputFluids = NonNullList.create();
        for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
            var stack = availableFluids.getFluidInTank(tank);
            if(stack.isEmpty()) continue;
            inputFluids.add(stack);
        }
        numValidItems = inputItems.size();
        numValidFluids = inputFluids.size();
        // test all liquids and fluids for requirement
        boolean ingredientsMatch = inputFilter.testIngredients(basin.getLevel(), inputItems, inputFluids);
        CreatIF.LOGGER.debug("Ingredients " + (ingredientsMatch?"match":"dont match"));

        // ((IBasinBlockEntityMixin)(Object)basin).setFilterIngredientStatus(ingredientsMatch);
        if (!ingredientsMatch)
            // required ingredients are not available, skip recipe check
            return new ArrayList<>();
        else{
            
            var list = original.call();
            CreatIF.LOGGER.debug("Scoring recipe...");
            // originally this is sorted by least amount of ingredients first, which is not what I want
            list.sort((r1,r2) -> scoreRecipe(r2, inputFilter) - scoreRecipe(r1, inputFilter)); // recipes that match most with available items will be selected
            return list;
        }
           // after this function returns, the output match is done with the builtin functionality
    }

    // scores the overlap of input items and required ingredients
    int scoreRecipe(Recipe<?> recipe, IngredientFilterItemStack filter){
        int res = 0;
        CreatIF.LOGGER.debug("Scoring recipe...");
        try{
            var recipeIngr = recipe.getIngredients();
            

            for (Ingredient ingredient : recipeIngr) {
                for (ItemStack stack : ingredient.getItems()){
                    // if any type of ingredient is specified in the filter, check next one
                    if(filter.testIngredients(getBasin().get().getLevel(),stack)){
                        res ++;
                        break;
                    } 
                }
            }
        } catch (Exception e) {
            CreatIF.LOGGER.debug("Scoring recipe failed: " + e.getCause() + e.getLocalizedMessage());
        }

        CreatIF.LOGGER.debug("Recipe Score: " + recipe.toString() + " = " + res);
        return res;
    }


    // validate recipe ingredient count for matchAll type filtering
    // @WrapOperation(remap=false, method = "matchBasinRecipe", 
    // at = @At(value="INVOKE", 
    // target="Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"))
    // public boolean validateNumIngredients(BasinBlockEntity basin, Recipe<?> recipe, Operation<Boolean> original){
    //     boolean matchAll = ! IngredientFilterItemStack.of(basin.getFilter().getFilter()).matchAny;
    //     CreateIngredientFilter.LOGGER.debug("Matching recipe type: " + recipe.toString());   
    //     boolean result = true;
    //     // only match if matchAll is selected and basin has items
    //     if(matchAll && numValidItems != 0){
    //         result &= recipe.getIngredients().size() == numValidItems;
    //         CreateIngredientFilter.LOGGER.debug("Number of items: " + recipe.getIngredients().size() + "/" + numValidItems);    
    //         CreateIngredientFilter.LOGGER.debug("items: " + recipe.getIngredients());   
    //         // if its a basin recipe, check fluids, otherwise could be a shapeless which only has item inputs        
    //         if(recipe instanceof BasinRecipe bRecipe && numValidFluids != 0){
    //             CreateIngredientFilter.LOGGER.debug("Number of fluids: " + bRecipe.getFluidIngredients().size() + "/" + numValidFluids);
    //             CreateIngredientFilter.LOGGER.debug("Fluids: " + bRecipe.getFluidIngredients() );
    //             result &= numValidFluids == bRecipe.getFluidIngredients().size();
    //         }
    //     }

    //     ((IBasinBlockEntityMixin) (Object) basin).setFilterRecipeStatus(result);

    //     return result && original.call(basin,recipe);
    // }
}
