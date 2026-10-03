/*=====================================================================
CreatIF - Create: Ingredient Filter 
Adds a new filter type to select basin recipes based on input
Copyright (C) 2026  rk-exxec

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
=====================================================================*/

package com.rk_exxec.creatif.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.gui.IngredientFilterMenu;
import com.rk_exxec.creatif.gui.IngredientStack;
import com.rk_exxec.creatif.gui.IngredientStackHandler;
import com.rk_exxec.creatif.util.CreatIFLang;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.*;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;


import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class IngredientFilterItem extends FilterItem {

	public IngredientFilterItem(Properties properties){
        super(properties);
    }

	@Override
	public List<Component> makeSummary(ItemStack filter) {
		if (!filter.hasTag()) return Collections.emptyList();

		List<Component> list = new ArrayList<>();

		IngredientStackHandler filterItems = getFilterItemHandler(filter);
		boolean blacklist = filter.getOrCreateTag().getBoolean("Blacklist");
		boolean matchany = filter.getOrCreateTag().getBoolean("Match Any");
		boolean rawFluid = filter.getOrCreateTag().getBoolean("Raw Fluid");

		if(!getFilterOutputItem(filter).isEmpty())
		{
			list.add(outputLabel(filter));
		}

		list.add(matchingLabel(matchany));

		list.add(rawFluid ? CreatIFLang.translate("gui","item_fluid").withStyle(ChatFormatting.GRAY)
			: CreatIFLang.translate("gui","raw_fluid").withStyle(ChatFormatting.AQUA));
			
		list.add((blacklist ? CreateLang.translateDirect("gui.filter.deny_list")
			: CreateLang.translateDirect("gui.filter.allow_list")).withStyle(ChatFormatting.GOLD));


		int count = 1;
		for (int i = 0; i < filterItems.getSlots(); i++) {
			if (count > 3) {
				list.add(Component.literal("- ...")
					.withStyle(ChatFormatting.DARK_GRAY));
				break;
			}

			IngredientStack filterStack = filterItems.getIngredientStackInSlot(i);
			if (filterStack.isEmpty())
				continue;
			list.add(Component.literal("- ")
				.append(filterStack.getHoverName())
				.withStyle(ChatFormatting.GRAY));
			count++;
		}

		if (count == 0)
			return Collections.emptyList();

		return list;
	}

	public Component matchingLabel(boolean matchany){
		return (matchany ? CreatIFLang.translate("gui","match_any").withStyle(ChatFormatting.GREEN)
			: CreatIFLang.translate("gui","match_all").withStyle(ChatFormatting.RED));
	}

	public Component outputLabel(ItemStack filter){
		return Component.literal("< ")
				.append(getFilterOutputItem(filter).getHoverName())
				.withStyle(ChatFormatting.AQUA);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return IngredientFilterMenu.create(id, inv, player.getMainHandItem());
	}

	@Override
	public IngredientFilterItemStack makeStackWrapper(ItemStack filter) {
		return new IngredientFilterItemStack(filter);
	}

	public IngredientStackHandler getFilterItemHandler(ItemStack stack) {
		IngredientStackHandler newInv = new IngredientStackHandler(20);
		CompoundTag invNBT = stack.getOrCreateTagElement("Items");
		if (!invNBT.isEmpty())
			newInv.deserializeNBT(invNBT);
		return newInv;
	}

	public IngredientStackHandler getFilterOutputHandler(ItemStack stack) {
		IngredientStackHandler newInv = new IngredientStackHandler(1);
		CompoundTag invNBT = stack.getOrCreateTagElement("Output");
		if (!invNBT.isEmpty())
			newInv.deserializeNBT(invNBT);
		return newInv;
	}

	@Override
	public ItemStack[] getFilterItems(ItemStack itemStack) {
		if (itemStack.hasTag() && itemStack.getOrCreateTag().getBoolean("Blacklist"))
			return new ItemStack[0];
		
		return getFilterItemHandler(itemStack).getNonNullNonFluidItemStacks().toArray(ItemStack[]::new);
	}

	public IngredientStack getFilterOutputItem(ItemStack itemStack) {
		return getFilterOutputHandler(itemStack).getIngredientStackInSlot(0);
	}
 
	public static boolean testDirect(IngredientStack filter, ItemStack stack, boolean matchNBT) {
		if (matchNBT) {
			if (PackageItem.isPackage(filter.itemStack()) && PackageItem.isPackage(stack))
				return doPackagesHaveSameData(filter.itemStack(), stack);

			if(filter.isFluid()) return false;
			else return ItemHandlerHelper.canItemStacksStack(filter.itemStack().copyWithCount(1), stack);
		}

		if (PackageItem.isPackage(filter.itemStack()) && PackageItem.isPackage(stack))
			return true;

		return !filter.isFluid() && ItemHelper.sameItem(filter.itemStack(), stack);
	}

	public static boolean testDirect(IngredientStack filter, FluidStack stack, boolean matchNBT) {
		if (matchNBT) {
			if(filter.isFluid()) return filter.isFluidEqual(stack);
			else return false;
		}

		return filter.isFluid() && filter.getFluid().isSame(stack.getFluid());
	}

	public static boolean testDirect(FluidStack filter, FluidStack stack, boolean matchNBT) {
		if(filter.isEmpty()) return false;
		if (matchNBT) {
			return filter.isFluidEqual(stack);
		}

		return filter.getFluid().isSame(stack.getFluid());
	}

	public static boolean doPackagesHaveSameData(ItemStack a, ItemStack b) {
		if (a.isEmpty() || a.hasTag() != b.hasTag())
			return false;
		if (!a.hasTag())
			return true;
		if (!a.areCapsCompatible(b))
			return false;
		for (String key : a.getTag()
			.getAllKeys()) {
			if (key.equals("Fragment"))
				continue;
			if (!Objects.equals(a.getTag()
					.get(key),
				b.getTag()
					.get(key)))
				return false;
		}
		return true;
	}

}
