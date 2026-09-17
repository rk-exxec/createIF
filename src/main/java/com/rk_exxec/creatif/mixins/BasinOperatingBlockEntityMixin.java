package com.rk_exxec.creatif.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.rk_exxec.creatif.CreateIngredientFilter;
import com.rk_exxec.creatif.filter.IngredientFilterItemStack;
import com.rk_exxec.creatif.filter.IngredientFilteringBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
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

/**
 * 
 * BasinRecipeMixin
 * 
 * where the magic happens
 */
@Mixin(BasinOperatingBlockEntity.class)
public class BasinOperatingBlockEntityMixin {
    // @WrapOperation(method = "matchBasinRecipe", remap = false,
    //     at = @At(value = "INVOKE",
    //     target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"
    // ))

    // @Inject(method = "updateBasin", at=@At("INVOKE"), 
    // target="Lcom/simibubi/create/content/processing/basin/BasinOperatingBlockEntity;getMatchingRecipes()Ljava/util/List;")
    // public boolean hasIngredients(CallbackInfo ci){
    //     return false;
    // }
    @Shadow 
    protected Optional<BasinBlockEntity> getBasin(){
        throw new AssertionError("Shadow error");
    }

    @WrapMethod(method="getMatchingRecipes", remap=false)
    public List<Recipe<?>> checkIngredients(Operation<List<Recipe<?>>> original){
		Optional<BasinBlockEntity> $basin = getBasin();
		BasinBlockEntity basin;
		if ($basin.isEmpty() || (basin = $basin.get()).isEmpty())
			return new ArrayList<>();
        CreateIngredientFilter.LOGGER.debug("This is the custom filter mixin");

        FilteringBehaviour filter = basin.getFilter();
        if (filter == null){
            CreateIngredientFilter.LOGGER.debug("filter is null");
            return original.call();
        }
        // CreateIngredientFilter.LOGGER.debug("Filter has class " + filter.getClass());
        CreateIngredientFilter.LOGGER.debug("Filter has class " + filter.getFilter().getDescriptionId());
        if (!(filter.getFilter().getDescriptionId().startsWith("item.creatif"))){
            CreateIngredientFilter.LOGGER.debug("Not ingredient filter - vanilla times");
            return original.call();
        }
        IngredientFilterItemStack inputFilter = new IngredientFilterItemStack(filter.getFilter());
        IItemHandler availableItems = basin.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        IFluidHandler availableFluids = basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);

        NonNullList<ItemStack> inputItems = NonNullList.create();
        for (int i = 0; i < availableItems.getSlots(); i++) {
            inputItems.add(availableItems.getStackInSlot(i));
        }
        NonNullList<FluidStack> inputFluids = NonNullList.create();
        for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
            inputFluids.add(availableFluids.getFluidInTank(tank));
        }
        boolean ingredientsMatch = inputFilter.test(basin.getLevel(), inputItems,inputFluids);
        CreateIngredientFilter.LOGGER.debug("Ingredients " + (ingredientsMatch?"match":"dont match"));

        if (!ingredientsMatch)
            return new ArrayList<>();
        else{
            var list = original.call();
            list.sort((r1,r2) -> scoreRecipe(r1, inputFilter) - scoreRecipe(r2, inputFilter)); // recipes that match most with available items match most
            return list;
        }
           
    }

    // scores the matchup of input items and required ingredients
    int scoreRecipe(Recipe<?> recipe, IngredientFilterItemStack filter){
        BasinRecipe basinRecipe = (BasinRecipe) recipe;

        var recipeIngr = basinRecipe.getIngredients();
        // Ingredient test = recipeIngr.get(0).getItems()

        int res = 0;
        int tot = 0;
        for (Ingredient ingredient : recipeIngr) {
            for (ItemStack stack : ingredient.getItems()){
                res += filter.test(getBasin().get().getLevel(),stack)?1:0;
                tot ++;
            }
        }
        return res;
    }
}
