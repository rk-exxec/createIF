package com.rk_exxec.creatif.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rk_exxec.creatif.util.CreatIFLang;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.*;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;


import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class IngredientFilterItem extends ListFilterItem {

	public IngredientFilterItem(Properties properties){
        super(properties);
    }

	@Override
	public List<Component> makeSummary(ItemStack filter) {
		if (!filter.hasTag()) return Collections.emptyList();

		List<Component> list = new ArrayList<>();

		ItemStackHandler filterItems = getFilterItemHandler(filter);
		boolean blacklist = filter.getOrCreateTag()
			.getBoolean("Blacklist");

		boolean matchany = filter.getOrCreateTag().getBoolean("Match Any");

		list.add((matchany ? CreatIFLang.translate("gui","match_any")
			: CreatIFLang.translate("gui","match_all")).withStyle(ChatFormatting.DARK_AQUA));
			
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

		// TODO: add match all or any to summary
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return IngredientFilterMenu.create(id, inv, player.getMainHandItem());
	}

	@Override
	public IngredientFilterItemStack makeStackWrapper(ItemStack filter) {
		return new IngredientFilterItemStack(filter);
	}

	@Override 
	public ItemStackHandler getFilterItemHandler(ItemStack stack) {
		ItemStackHandler newInv = new ItemStackHandler(20);
		CompoundTag invNBT = stack.getOrCreateTagElement("Items");
		if (!invNBT.isEmpty())
			newInv.deserializeNBT(invNBT);
		return newInv;
	}

	@Override
	public ItemStack[] getFilterItems(ItemStack itemStack) {
		if (itemStack.hasTag() && itemStack.getOrCreateTag().getBoolean("Blacklist"))
			return new ItemStack[0];
		return ItemHelper.getNonEmptyStacks(getFilterItemHandler(itemStack)).toArray(ItemStack[]::new);
	}
 
	public static boolean testDirect(ItemStack filter, ItemStack stack, boolean matchNBT) {
		if (matchNBT) {
			if (PackageItem.isPackage(filter) && PackageItem.isPackage(stack))
				return doPackagesHaveSameData(filter, stack);

			return ItemHandlerHelper.canItemStacksStack(filter, stack);
		}

		if (PackageItem.isPackage(filter) && PackageItem.isPackage(stack))
			return true;

		return ItemHelper.sameItem(filter, stack);
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
