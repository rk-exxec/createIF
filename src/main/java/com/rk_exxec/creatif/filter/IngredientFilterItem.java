/*=====================================================================
CreatIF- Create: Ingredient Filter 
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

import com.rk_exxec.creatif.util.CreatIFLang;
import com.rk_exxec.creatif.util.MyDataComponents;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.*;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;


import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.neoforged.neoforge.items.ItemStackHandler;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class IngredientFilterItem extends ListFilterItem {

	public IngredientFilterItem(Properties properties){
        super(properties);
    }

	@Override
	public List<Component> makeSummary(ItemStack filter) {
		List<Component> list = new ArrayList<>();

		ItemStackHandler filterItems = getFilterItemHandler(filter);
		boolean blacklist = filter.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, false);

		boolean matchany =  filter.getOrDefault(MyDataComponents.FILTER_MATCH_ANY, false);

		if(!getFilterOutputItem(filter).isEmpty())
		{
			list.add(outputLabel(filter));
		}

		list.add(matchingLabel(matchany));
			
		list.add((blacklist ? CreateLang.translateDirect("gui.filter.deny_list")
			: CreateLang.translateDirect("gui.filter.allow_list")).withStyle(ChatFormatting.GOLD));
		int count = 0;
		for (int i = 0; i < filterItems.getSlots(); i++) {
			if (count > 3) {
				list.add(Component.literal("- ...")
					.withStyle(ChatFormatting.DARK_GRAY));
				break;
			}

			ItemStack filterStack = filterItems.getStackInSlot(i);
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

	// @Override 
	// public ItemStackHandler getFilterItemHandler(ItemStack stack) {
	// 	ItemStackHandler newInv = new ItemStackHandler(20);
	// 	CompoundTag invNBT = stack.getOrCreateTagElement("Items");
	// 	if (!invNBT.isEmpty())
	// 		newInv.deserializeNBT(invNBT);
	// 	return newInv;
	// }

	@Override 
	public ItemStackHandler getFilterItemHandler(ItemStack stack) {
		ItemStackHandler newInv = new ItemStackHandler(20);
		ItemContainerContents contents = stack.getOrDefault(AllDataComponents.FILTER_ITEMS, ItemContainerContents.EMPTY);
		ItemHelper.fillItemStackHandler(contents, newInv);
		return newInv;
	}

	public ItemStackHandler getFilterOutputHandler(ItemStack stack) {
		ItemStackHandler newInv = new ItemStackHandler(1);
		ItemContainerContents contents = stack.getOrDefault(MyDataComponents.FILTER_OUTPUT,ItemContainerContents.EMPTY);
		ItemHelper.fillItemStackHandler(contents, newInv);
		return newInv;
	}

	@Override
	public ItemStack[] getFilterItems(ItemStack stack) {
		if (stack.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, false))
			return new ItemStack[0];
		return ItemHelper.getNonEmptyStacks(getFilterItemHandler(stack)).toArray(ItemStack[]::new);
	}

	public ItemStack getFilterOutputItem(ItemStack itemStack) {
		// if (itemStack.hasTag() && itemStack.getOrCreateTag().getBoolean("Blacklist"))
		// 	return null;
		
		return getFilterOutputHandler(itemStack).getStackInSlot(0);
	}
 
	public static boolean testDirect(ItemStack filter, ItemStack stack, boolean matchNBT) {
		if (matchNBT) {
			if (PackageItem.isPackage(filter) && PackageItem.isPackage(stack))
				return doPackagesHaveSameData(filter, stack);

			return ItemStack.isSameItemSameComponents(filter, stack);
		}

		if (PackageItem.isPackage(filter) && PackageItem.isPackage(stack))
			return true;

		return ItemHelper.sameItem(filter, stack);
	}
	
	public static boolean doPackagesHaveSameData(@NotNull ItemStack a, @NotNull ItemStack b) {
		if (a.isEmpty())
			return false;
		if (!ItemStack.isSameItemSameComponents(a, b))
			return false;
		for (TypedDataComponent<?> component : a.getComponents()) {
			DataComponentType<?> type = component.type();
			if (type.equals(AllDataComponents.PACKAGE_ORDER_DATA) ||
				type.equals(AllDataComponents.PACKAGE_ORDER_CONTEXT))
				continue;
			if (!Objects.equals(a.get(type), b.get(type)))
				return false;
		}
		return true;
	}

}
