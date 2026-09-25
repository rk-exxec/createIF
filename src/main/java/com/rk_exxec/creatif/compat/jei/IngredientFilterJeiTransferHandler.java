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

package com.rk_exxec.creatif.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket.IngOption;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.rk_exxec.creatif.util.MyPackets;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import net.minecraft.client.gui.screens.Screen;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class IngredientFilterJeiTransferHandler implements IUniversalRecipeTransferHandler<IngredientFilterMenu> {
	@Override
	public Class<? extends IngredientFilterMenu> getContainerClass() {
		return IngredientFilterMenu.class;
	}

	@Override
	public Optional<MenuType<IngredientFilterMenu>> getMenuType() {
		return Optional.of(MyMenuTypes.INGREDIENT_FILTER.get());
	}

	<I> void readSlot(List<I> ingredients, List<ItemStack> target, int maxSize){
			for(I stack : ingredients){
				if (target.size() < maxSize) {
					ItemStack copy;
					if(stack instanceof FluidStack fluid)
						copy = FluidUtil.getFilledBucket(fluid);
					else
						copy = ((ItemStack) stack).copyWithCount(1);
					// dont copy duplicates
					if(!target.stream().anyMatch(i -> i.is(copy.getItemHolder()))) {
						target.add(copy);
					}
					if(!Screen.hasShiftDown()) break; // shift needed to get all variants
				}
			}
		
	}

	@Override
	public IRecipeTransferError transferRecipe(IngredientFilterMenu menu, Object recipe,
		IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {

		try{
			List<ItemStack> ingredients = new ArrayList<>();
			for(IRecipeSlotView slot : recipeSlots.getSlotViews()){		
				if (slot.getRole() == RecipeIngredientRole.INPUT || slot.getRole() == RecipeIngredientRole.CATALYST){
					List<ItemStack> stackList = slot.getItemStacks().toList();//getDisplayedIngredient(VanillaTypes.ITEM_STACK);
					if(!stackList.isEmpty()) {
						readSlot(stackList, ingredients, menu.ghostInventory.getSlots());
					} else {
						List<FluidStack> fluidsList = slot.getIngredients(ForgeTypes.FLUID_STACK).toList();
						if(!fluidsList.isEmpty()) {
							readSlot(fluidsList, ingredients, menu.ghostInventory.getSlots());
						}
					}
				}
			}
			

			if (ingredients.isEmpty())
				return null;

			IRecipeSlotView outSlot = recipeSlots.getSlotViews(RecipeIngredientRole.OUTPUT).get(0);
			ItemStack output = ItemStack.EMPTY;
			Optional<ItemStack> oItemStack = outSlot.getDisplayedItemStack();
			if(!oItemStack.isEmpty()){
				output = oItemStack.get().copyWithCount(1);
			}
			else{
				Optional<FluidStack> oFluidStack = outSlot.getDisplayedIngredient(ForgeTypes.FLUID_STACK);
				if(oFluidStack.isPresent())
					output = FluidUtil.getFilledBucket(oFluidStack.get()).copyWithCount(1);
			}

			if (doTransfer) {
				CreatIF.LOGGER.debug("transferring " + ingredients.toString() + output.toString());
				menu.applyRecipe(ingredients, output);
					CatnipServices.NETWORK.sendToServer(
						new IngredientFilterScreenPacket(IngOption.FILL_RECIPE, menu.createRecipeData()));
				}

			
			return null;
		}catch(Exception e){
			CreatIF.LOGGER.error("Error in setting items", e);
			throw e;
		}
	}
}