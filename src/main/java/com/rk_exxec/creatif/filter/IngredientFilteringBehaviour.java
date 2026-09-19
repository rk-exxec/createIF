package com.rk_exxec.creatif.filter;

import com.rk_exxec.creatif.CreatIF;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;



public class IngredientFilteringBehaviour extends FilteringBehaviour{

    public static final BehaviourType<IngredientFilteringBehaviour> TYPE = new BehaviourType<>();

    public <T> boolean test(NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        if(itemStacks.isEmpty() && fluidStacks.isEmpty()) return false;
        if(filter == null){
            CreatIF.LOGGER.debug("filter is null");
            return  false;
        }
        IngredientFilterItemStack filterItemStack = (IngredientFilterItemStack)filter;

		return !isActive() || filterItemStack.testIngredients(blockEntity.getLevel(), itemStacks, fluidStacks);
	}

    public IngredientFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    public IngredientFilterItemStack getFilterStack() {
        return (IngredientFilterItemStack) filter;
    }    
}
