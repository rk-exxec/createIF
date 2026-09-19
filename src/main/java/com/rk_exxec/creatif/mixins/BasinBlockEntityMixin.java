package com.rk_exxec.creatif.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.rk_exxec.creatif.CreateIngredientFilter;
import com.rk_exxec.creatif.filter.IngredientFilterItemStack;
import com.rk_exxec.creatif.interfaces.IBasinBlockEntityMixin;
import com.rk_exxec.creatif.util.CreatIFLang;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.lang.LangBuilder;
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

        if(!CreateIngredientFilter.WAILA_ACTIVE){
            String filterID = filtering.getFilter().getDescriptionId();
            CreateLang.builder().add((CreatIFLang.translateRaw(filterID))
                        .withStyle(ChatFormatting.DARK_AQUA)).forGoggles(tooltip, 0);
            if(filterID.startsWith("item.creatif")){
                IngredientFilterItemStack inputFilter = IngredientFilterItemStack.of(filtering.getFilter());
                if(inputFilter.matchAny)
                    CreateLang.builder().add((CreatIFLang.translate("gui", "match_any"))
                                .withStyle(ChatFormatting.GREEN)).forGoggles(tooltip, 1);
                else
                    CreateLang.builder().add((CreatIFLang.translate("gui", "match_all"))
                                .withStyle(ChatFormatting.YELLOW)).forGoggles(tooltip, 1);
            }
        }
        if(filterRecipeMismatch)
            CreateLang.builder().add((CreatIFLang.translate("gui", "filter_recipe_mismatch"))
					.withStyle(ChatFormatting.RED)).forGoggles(tooltip, 1);

        if(filterIngredientMismatch)
            CreateLang.builder().add((CreatIFLang.translate("gui", "filter_ingredient_mismatch"))
					.withStyle(ChatFormatting.RED)).forGoggles(tooltip, 1);

        return orig_result;
    }
}
