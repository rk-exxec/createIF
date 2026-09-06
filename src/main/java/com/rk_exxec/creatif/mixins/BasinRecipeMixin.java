package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rk_exxec.creatif.filter.ContentFilterItem;
import com.rk_exxec.creatif.filter.ContentFilterItemStack;
import com.rk_exxec.creatif.filter.InputFilteringBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@Mixin(BasinOperatingBlockEntity.class)
public abstract class BasinRecipeMixin {

    @Shadow private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {return false;}
    
    @Redirect(method = "matchBasinRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"), remap = false)
	public static boolean match(BasinBlockEntity basin, Recipe<?> recipe) {
		FilteringBehaviour filter = basin.getFilter();
		if (filter == null)
			return false;

        boolean filterTest = false;

        // custom code for checking Input items instead of recipe results
        ItemStack filterStack = filter.getFilter();
        if(filterStack.getClass().isAssignableFrom(ContentFilterItemStack.class)){
            filterTest = ((InputFilteringBehaviour)filter).test(recipe.getIngredients());
            if (recipe instanceof BasinRecipe basinRecipe) {
                if (basinRecipe.getRollableResults()
                    .isEmpty()
                    && !basinRecipe.getFluidResults()
                    .isEmpty())
                    filterTest = ((InputFilteringBehaviour)filter).testFluidIngredients(basinRecipe.getFluidIngredients());
            }
        } 
        // Vanilla code
        else { 
            
            filterTest = filter.test(recipe.getResultItem(basin.getLevel()
                .registryAccess()));
            if (recipe instanceof BasinRecipe basinRecipe) {
                if (basinRecipe.getRollableResults()
                    .isEmpty()
                    && !basinRecipe.getFluidResults()
                    .isEmpty())
                    filterTest = filter.test(basinRecipe.getFluidResults()
                        .get(0));
            }
        }

		if (!filterTest)
			return false;

		return apply(basin, recipe, true);
	}
}
