package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.rk_exxec.creatif.interfaces.IFilteringBehaviourMixin;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

@Mixin(FilteringBehaviour.class)
public class FilteringBehaviourMixin implements IFilteringBehaviourMixin {
    @Shadow(remap=false)
    FilterItemStack filter;

    public FilterItemStack getFilterStack() {
        return filter;
    }    
}
