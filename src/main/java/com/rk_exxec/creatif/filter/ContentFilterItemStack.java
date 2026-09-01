package com.rk_exxec.creatif.filter;

import java.util.ArrayList;

import java.util.List;
import com.simibubi.create.content.logistics.filter.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

public class ContentFilterItemStack extends FilterItemStack {

    public List<FilterItemStack> containedItems;
    public boolean shouldRespectNBT;
    public boolean isBlacklist;

    public ContentFilterItemStack(ItemStack filter) {
        super(filter);
        boolean defaults = !filter.hasTag();

        containedItems = new ArrayList<>();
        ItemStackHandler items = ((ListFilterItem) filter.getItem()).getFilterItemHandler(filter);
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack stackInSlot = items.getStackInSlot(i);
            if (!stackInSlot.isEmpty())
                containedItems.add(FilterItemStack.of(stackInSlot));
        }

        shouldRespectNBT = defaults ? false
            : filter.getTag()
            .getBoolean("RespectNBT");
        isBlacklist = defaults ? false
            : filter.getTag()
            .getBoolean("Blacklist");
    }

    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        for (FilterItemStack filterItemStack : containedItems)
            if (filterItemStack.test(world, stack, shouldRespectNBT))
                return !isBlacklist;
        return isBlacklist;
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        for (FilterItemStack filterItemStack : containedItems)
            if (filterItemStack.test(world, stack, shouldRespectNBT))
                return !isBlacklist;
        return isBlacklist;
    }

}

