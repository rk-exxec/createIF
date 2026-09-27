package com.rk_exxec.creatif.filter;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IngredientStackHandler {

    
    protected NonNullList<IngredientStack> stacks;

    public IngredientStackHandler()
    {
        this(1);
    }

    public IngredientStackHandler(int size)
    {
        stacks = NonNullList.withSize(size, IngredientStack.EMPTY);
    }

    public IngredientStackHandler(NonNullList<IngredientStack> stacks)
    {
        this.stacks = stacks;
    }

    public void setSize(int size)
    {
        stacks = NonNullList.withSize(size, IngredientStack.EMPTY);
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack)
    {
        validateSlotIndex(slot);
        this.stacks.set(slot, IngredientStack.of(stack));
        onContentsChanged(slot);
    }

    public void setStackInSlot(int slot, @NotNull FluidStack stack)
    {
        validateSlotIndex(slot);
        this.stacks.set(slot, IngredientStack.of(stack));
        onContentsChanged(slot);
    }


    public void setStackInSlot(int slot, @NotNull IngredientStack stack)
    {
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }


    public int getSlots()
    {
        return stacks.size();
    }

    @NotNull
    public ItemStack getStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return this.stacks.get(slot).itemStack;
    }

    public IngredientStack getIngredientStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }


    public int getSlotLimit(int slot)
    {
        return 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack)
    {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return true;
    }

    public boolean isItemValid(int slot, @NotNull FluidStack stack)
    {
        return true;
    }

    public CompoundTag serializeNBT()
    {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (!stacks.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt)
    {
        setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                IngredientStack ingredient = IngredientStack.of(itemTags);
                stacks.set(slot, ingredient);
            }
        }
        onLoad();
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }

    public List<ItemStack> getNonNullNonFluidItemStacks(){
        ArrayList<ItemStack> list = new ArrayList<>();
        for(IngredientStack ingredientStack : stacks){
            if(!ingredientStack.isEmpty() && !ingredientStack.isFluid) list.add(ingredientStack.itemStack);

        }
        return list;
    }

    protected void onLoad()
    {

    }

    protected void onContentsChanged(int slot)
    {

    }

    // Just for Interface compliance, never called

    // @Override
    // public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
    //     throw new UnsupportedOperationException("Unimplemented method 'insertItem'");
    // }

    // @Override
    // public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
    //     throw new UnsupportedOperationException("Unimplemented method 'extractItem'");
    // }
    
}
