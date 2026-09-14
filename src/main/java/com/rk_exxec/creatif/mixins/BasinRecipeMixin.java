package com.rk_exxec.creatif.mixins;

import java.io.ObjectInputFilter.FilterInfo;
import java.nio.file.DirectoryStream.Filter;
import java.util.Arrays;

import org.openjdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rk_exxec.creatif.CreateIngredientFilter;
import com.rk_exxec.creatif.filter.IIngredientFilterBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

@Mixin(BasinOperatingBlockEntity.class)
public class BasinRecipeMixin {
    // TODO: inject on return(second and tail sibnce you can use ordinals?) https://mixins.microcontrollers.dev/mixinextras/modifyreturnvalue/
    // TODO: oder nochmal match wrappen weil hat ja wrsl am filter type nicht assigned gelegen

    @WrapOperation(method = "matchBasinRecipe", remap = false,
        at = @At(value = "INVOKE",
        target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"
    ))

    private static <P> boolean checkIngredientFilter( BasinBlockEntity basin, Recipe<?> recipe, Operation<Boolean> original) {
        CreateIngredientFilter.LOGGER.debug("This is the custom filter mixin");

        FilteringBehaviour filter = basin.getFilter();
        if (filter == null){
            CreateIngredientFilter.LOGGER.debug("filter is null");
            return false;
        }

        // CreateIngredientFilter.LOGGER.debug("Filter has class " + filter.getClass());
        try{
            IIngredientFilterBehaviour inputFilter = ((IIngredientFilterBehaviour) (Object) filter);
            if ((recipe instanceof BasinRecipe basinRecipe && !basinRecipe.getRollableResults().isEmpty()) ||
                (recipe instanceof ShapelessRecipe shapelessRecipe && !shapelessRecipe.isIncomplete())) 
                {
                    CreateIngredientFilter.LOGGER.debug(recipe.getId().toString());

                    IItemHandler availableItems = basin.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
		            IFluidHandler availableFluids = basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);

                    // for some reason, idk why, create tries to match mud even though ingredients dont match.
                    // this tries to fix that.
                    if(recipe.getId().toString().equals("create:mixing/mud_by_mixing")){
                        CreateIngredientFilter.LOGGER.debug("mud?");
                        boolean actuallyMud = false;
                        for (Ingredient ingredient : recipe.getIngredients()) {
                            for (int i = 0; i<availableItems.getSlots();i++){
                                actuallyMud |= ingredient.test(availableItems.getStackInSlot(i));
                            }
                        } 
                        if(!actuallyMud){
                            CreateIngredientFilter.LOGGER.debug("Not mud, applying fix");
                            return original.call(basin, recipe);
                        }
                    }

                    NonNullList<ItemStack> inputItems = NonNullList.create();
                    for (int i = 0; i < availableItems.getSlots(); i++) {
                        inputItems.add(availableItems.getStackInSlot(i));
                    }
                    // CreateIngredientFilter.LOGGER.debug(basinRecipe.getIngredients().toString());
                    NonNullList<FluidStack> inputFluids = NonNullList.create();
                    for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                        inputFluids.add(availableFluids.getFluidInTank(tank));
                    }
                    boolean ingredientsMatch = inputFilter.test(inputItems,inputFluids);
                    CreateIngredientFilter.LOGGER.debug("Ingredients " + (ingredientsMatch?"match":"dont match"));
                    // basinRecipe.getIngredients()
                    
                    if (ingredientsMatch) return true;
                // }
            }
            return original.call(basin,recipe);
        } catch(Exception e) {
            CreateIngredientFilter.LOGGER.debug("Filter could not be detected as input: " + e.getMessage());
            return original.call(basin, recipe);
        }
	}
}
