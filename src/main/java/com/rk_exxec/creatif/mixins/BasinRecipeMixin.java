package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rk_exxec.creatif.CreateIngredientFilter;
import com.rk_exxec.creatif.filter.IngredientFilterItemStack;
import com.rk_exxec.creatif.filter.IngredientFilteringBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

@Mixin (BasinRecipe.class)
public class BasinRecipeMixin {

    @Shadow 
    private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test){
        throw new AssertionError("Shadow error");
    }

    @WrapMethod(method = "match", remap = false)
    private static boolean checkIngredientFilter( BasinBlockEntity basin, Recipe<?> recipe, Operation<Boolean> original) {
        CreateIngredientFilter.LOGGER.debug("This is the custom filter mixin");

        FilteringBehaviour filter = basin.getFilter();
        if (filter == null){
            CreateIngredientFilter.LOGGER.debug("filter is null");
            return false;
        }
         if (!(filter.getFilter().getDescriptionId().startsWith("item.creatif"))){
            CreateIngredientFilter.LOGGER.debug("Not ingredient filter - vanilla times");
            return original.call(basin, recipe);
        }
        // Skips filter check, bc we already tested for input in call stack
        // TODO: change when filter also implements specifying output
        return apply(basin, recipe, true);
        // IngredientFilteringBehaviour inputFilter = ((IngredientFilteringBehaviour) (Object) filter);
        // // ItemStackHandler items = inputFilter.getIFItem().getFilterItemHandler(filter.getFilter());
        // if ((recipe instanceof BasinRecipe basinRecipe && !basinRecipe.getRollableResults().isEmpty()) ||
        //     (recipe instanceof ShapelessRecipe shapelessRecipe && !shapelessRecipe.isIncomplete())) 
        //     {
        //         CreateIngredientFilter.LOGGER.debug(recipe.getId().toString());

        //         BasinRecipe basinRecipe = (BasinRecipe) recipe;

        //         var recipeIngr = basinRecipe.getIngredients();
        //         // Ingredient test = recipeIngr.get(0).getItems()

        //         int res = 0;
        //         int tot = 0;
        //         for (Ingredient ingredient : recipeIngr) {
        //             for (ItemStack stack : ingredient.getItems()){
        //                 res += filter.test(stack)?1:0;
        //                 tot ++;
        //             }
        //         }
                
        //         if (inputFilter.getFilterStack().matchAny) return apply(basin, recipe, true);
        //     // }
        // }
        // CreateIngredientFilter.LOGGER.debug(recipe.getId().toString());
        // return original.call(basin,recipe);
	}
}
