package com.rk_exxec.creatif.filter;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IIngredientFilterBehaviour {
    public <T> boolean test(NonNullList<ItemStack> itemList,NonNullList<FluidStack> fluidList);

    public IngredientFilterItemStack getFilter();

    public <P extends FilterItem> P getIFItem();

    public BehaviourType<?> getType() ;

}
