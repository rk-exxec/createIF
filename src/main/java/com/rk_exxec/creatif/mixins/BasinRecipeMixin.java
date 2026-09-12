package com.rk_exxec.creatif.mixins;

import java.io.ObjectInputFilter.FilterInfo;
import java.nio.file.DirectoryStream.Filter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rk_exxec.creatif.CreateContentFilter;
import com.rk_exxec.creatif.filter.IContentFilterBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

@Mixin(BasinOperatingBlockEntity.class)
public class BasinRecipeMixin {
    // TODO: inject on return(second and tail sibnce you can use ordinals?) https://mixins.microcontrollers.dev/mixinextras/modifyreturnvalue/
    // TODO: oder nochmal match wrappen weil hat ja wrsl am filter type nicht assigned gelegen

    @WrapOperation(method = "matchBasinRecipe", remap = false,
        at = @At(value = "INVOKE",
        target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"
    ))

    private static <P> boolean checkIngredientFilter( BasinBlockEntity basin, Recipe<?> recipe, Operation<Boolean> original) {
        CreateContentFilter.LOGGER.debug("This is the custom filter mixin");

        FilteringBehaviour filter = basin.getFilter();
        if (filter == null){
            CreateContentFilter.LOGGER.debug("filter is null");
            return false;
        }

        // CreateContentFilter.LOGGER.debug("Filter has class " + filter.getClass());
        try{
            IContentFilterBehaviour inputFilter = ((IContentFilterBehaviour) (Object) filter);
        // if (InputFilteringBehaviour.class.isAssignableFrom(filter.getClass())) {
            // CreateContentFilter.LOGGER.debug("Filter is input filter");
            NonNullList<ItemStack> inputItems = NonNullList.create();
            for (int i = 0; i<basin.getInputInventory().getSlots(); i++) {
                inputItems.add(basin.getInputInventory().getStackInSlot(i));
            }
            CreateContentFilter.LOGGER.debug(inputFilter.toString());
            boolean itemsMatch = inputFilter.test(inputItems);
            CreateContentFilter.LOGGER.debug("Items " + (itemsMatch?"match":"dont match"));

            NonNullList<FluidStack> inputFluids = NonNullList.create();
            for (TankSegment tS : basin.inputTank.getTanks()) {
                inputFluids.add(tS.getRenderedFluid()); // TODO: this is probably not right
            }
            boolean liquidsMatch = inputFilter.test(inputFluids);
            if (itemsMatch && liquidsMatch) return true;
            // else return original.call(basin, recipe);
            else return original.call(basin, recipe);
        } catch(Exception e) {
            CreateContentFilter.LOGGER.debug("Filter could not be detected as input: " + e.getMessage());
            return original.call(basin, recipe);
        }
	}
}
