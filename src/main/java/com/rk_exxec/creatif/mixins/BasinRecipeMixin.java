package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rk_exxec.creatif.CreateContentFilter;
import com.rk_exxec.creatif.filter.InputFilteringBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

@Mixin(BasinRecipe.class)
public class BasinRecipeMixin {
    // TODO: inject on return(second and tail sibnce you can use ordinals?) https://mixins.microcontrollers.dev/mixinextras/modifyreturnvalue/
    // TODO: oder nochmal match wrappen weil hat ja wrsl am filter type nicht assigned gelegen

    @WrapOperation(method = "match", remap = false,
        at = @At(value = "INVOKE",
        target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;test(Lnet/minecraft/world/item/ItemStack;)Z"
    ))
    private static <P> boolean checkIngredientFilter( FilteringBehaviour filter, ItemStack stack,
        Operation<Boolean> original, @Local(argsOnly = true) BasinBlockEntity basin) {
        CreateContentFilter.LOGGER.debug("This is the custom filter mixin");
        CreateContentFilter.LOGGER.debug("Filter has class " + filter.getClass());
        try{
            InputFilteringBehaviour inputFilter = (InputFilteringBehaviour) filter;
        // if (InputFilteringBehaviour.class.isAssignableFrom(filter.getClass())) {
            CreateContentFilter.LOGGER.debug("Filter is input filter");
            NonNullList<ItemStack> inputInv = NonNullList.create();
            for (int i = 0; i<basin.getInputInventory().getSlots(); i+=1) {
                inputInv.add(basin.getInputInventory().getStackInSlot(i));
            }

            boolean itemsMatch = inputFilter.test(inputInv);
            CreateContentFilter.LOGGER.debug("Items " + (itemsMatch?"match":"dont match"));
            if (itemsMatch) return true;
            // else return original.call(basin, recipe);
            else return original.call(filter,stack);
        } catch(Exception e) {
            return original.call(filter,stack);
        }
	}
}
