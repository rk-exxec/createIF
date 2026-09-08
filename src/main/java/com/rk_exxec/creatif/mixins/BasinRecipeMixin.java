package com.rk_exxec.creatif.mixins;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rk_exxec.creatif.filter.ContentFilterItem;
import com.rk_exxec.creatif.filter.ContentFilterItemStack;
import com.rk_exxec.creatif.filter.InputFilteringBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

@Mixin(BasinRecipe.class)
public class BasinRecipeMixin {

    // @Shadow(remap = false) private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {return false;}
    
    // @Redirect(method = "matchBasinRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"), remap = false)
	// @Overwrite(remap = false)
    // @Inject(at = { @At(value = "TAIL") })
    @WrapMethod(method = { "apply" }, remap=false)
    private boolean checkIngredientFilter(BasinBlockEntity basin, Recipe<?> recipe, boolean test, Operation<Boolean> original) {
		FilteringBehaviour filter = basin.getFilter();
		// if (filter == null)
		// 	return false;

        // custom code for checking Input items instead of recipe results
        ItemStack filterStack = filter.getFilter();
        if(ContentFilterItemStack.class.isAssignableFrom(filterStack.getClass())){
            NonNullList<ItemStack> inputInv = NonNullList.create();
            for (int i = 0; i<basin.getInputInventory().getSlots(); i+=1) {
                inputInv.add(basin.getInputInventory().getStackInSlot(i));
            }

            // NonNullList<FluidStack> fluidInputInv = NonNullList.create();
            // for (int i = 0; i<basin.getTanks().getFirst().getPrimaryHandler().getCapacity(); i+=1) {
            //     fluidInputInv.add(basin.getTanks().getFirst().getPrimaryHandler().getFluidInTank(i));
            // }

            if (((InputFilteringBehaviour)filter).test(inputInv)) return original.call(basin, recipe, test);
            else return false;
            // if (recipe instanceof BasinRecipe basinRecipe) {
            //     if (basinRecipe.getRollableResults()
            //         .isEmpty()
            //         && !basinRecipe.getFluidResults()
            //         .isEmpty())
            //         filterTest = ((InputFilteringBehaviour)filter).testFluidIngredients(fluidInputInv);
            // }
        } 
        return original.call(basin, recipe, test);
        // // Vanilla code
        // else { 
            
        //     filterTest = filter.test(recipe.getResultItem(basin.getLevel()
        //         .registryAccess()));
        //     if (recipe instanceof BasinRecipe basinRecipe) {
        //         if (basinRecipe.getRollableResults()
        //             .isEmpty()
        //             && !basinRecipe.getFluidResults()
        //             .isEmpty())
        //             filterTest = filter.test(basinRecipe.getFluidResults()
        //                 .get(0));
        //     }
        // }

		// if (!filterTest)
		// 	return false;

		// return BasinRecipe.apply(basin, recipe);
        
	}
}
