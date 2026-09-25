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

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterItemStack;
import com.rk_exxec.creatif.interfaces.IBasinBlockEntityMixin;
import com.rk_exxec.creatif.interfaces.IFilteringBehaviourMixin;
import com.rk_exxec.creatif.util.CreatIFLang;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin implements IBasinBlockEntityMixin {

    boolean filterRecipeMismatch = false;
    boolean filterIngredientMismatch = false;

    @Shadow(remap=false)
    FilteringBehaviour filtering;


    public void setFilterRecipeStatus(boolean matchesRecipe){
        filterRecipeMismatch = matchesRecipe;
    }

    public void setFilterIngredientStatus(boolean matchesIngredients){
        filterIngredientMismatch = matchesIngredients;
    }
    
    @WrapMethod(remap = false, method="addToGoggleTooltip")
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, Operation<Boolean> original){
        boolean orig_result = original.call(tooltip,isPlayerSneaking);

        FilterItemStack filterItemStack = ((IFilteringBehaviourMixin)(Object)filtering).getFilterStack();
        String filterID = filtering.getFilter().getDescriptionId();
        if((filterItemStack instanceof IngredientFilterItemStack ingredientFilterStack) && !CreatIF.WAILA_ACTIVE){
            
            CreateLang.builder().add((CreatIFLang.translateRaw(filterID))
                            .withStyle(ChatFormatting.DARK_AQUA)).forGoggles(tooltip, 0);
            if(isPlayerSneaking){
                ingredientFilterStack.getFilterItem().makeSummary(ingredientFilterStack.item())
                    .forEach((c) -> CreateLang.builder().add(c).forGoggles(tooltip,1));
            }
            else{
                CreateLang.builder().add(ingredientFilterStack.getFilterItem().matchingLabel(ingredientFilterStack.matchAny))
                    .forGoggles(tooltip, 1);
            }
        }
        // if(filterRecipeMismatch)
        //     CreateLang.builder().add((CreatIFLang.translate("gui", "filter_recipe_mismatch"))
		// 			.withStyle(ChatFormatting.RED)).forGoggles(tooltip, 1);

        // if(filterIngredientMismatch)
        //     CreateLang.builder().add((CreatIFLang.translate("gui", "filter_ingredient_mismatch"))
		// 			.withStyle(ChatFormatting.RED)).forGoggles(tooltip, 1);

        return orig_result;
    }
}
