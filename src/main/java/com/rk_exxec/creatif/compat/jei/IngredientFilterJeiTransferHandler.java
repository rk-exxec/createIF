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
import com.rk_exxec.creatif.filter.IngredientStack;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.rk_exxec.creatif.util.MyPackets;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IngredientFilterJeiTransferHandler implements IUniversalRecipeTransferHandler<IngredientFilterMenu> {

	public IngredientFilterJeiTransferHandler()
	{

	}

	@Override
	public Class<? extends IngredientFilterMenu> getContainerClass() {
		return IngredientFilterMenu.class;
	}

	@Override
	public Optional<MenuType<IngredientFilterMenu>> getMenuType() {
		return Optional.of(MyMenuTypes.INGREDIENT_FILTER.get());
	}

	<I> void readSlot(List<I> ingredients, List<IngredientStack> target, int maxSize){
			for(I stack : ingredients){
				if (target.size() < maxSize) {
					IngredientStack copy;
					if(stack instanceof FluidStack fluid)
						copy = IngredientStack.of(fluid);
					else
						copy = IngredientStack.of(((ItemStack) stack).copyWithCount(1));
					// dont copy duplicates
					if(!target.stream().anyMatch(i -> i.equals(copy))) {
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
			List<IngredientStack> ingredients = 
			extracted(menu, recipeSlots, menu.ghostInventory.getSlots());
			if (ingredients.isEmpty())
				return null;

			List<IngredientStack> outputs = 
			extracted(menu, recipeSlots, menu.outputGhostInventory.getSlots());
			if (doTransfer) {
				CreatIF.LOGGER.debug("transferring " + ingredients.toString() + outputs.toString());
				menu.applyRecipe(ingredients, outputs);
					MyPackets.getChannel().sendToServer(
						new IngredientFilterScreenPacket(menu.createRecipeData()));
				}
			return null;
		}catch(Exception e){
			CreatIF.LOGGER.error("Error in setting items", e);
			throw e;
		}
	}

	private List<IngredientStack> extracted(IngredientFilterMenu menu, IRecipeSlotsView recipeSlots, int slotCount) {
		List<IngredientStack> results = new ArrayList<>();
		for(IRecipeSlotView slot : recipeSlots.getSlotViews()){		
			if (slot.getRole() == RecipeIngredientRole.INPUT || slot.getRole() == RecipeIngredientRole.CATALYST){
				List<ItemStack> stackList = slot.getItemStacks().toList();//getDisplayedIngredient(VanillaTypes.ITEM_STACK);
				if(!stackList.isEmpty()) {
					readSlot(stackList, results, slotCount);
				} else {
					List<FluidStack> fluidsList = slot.getIngredients(ForgeTypes.FLUID_STACK).toList();
					if(!fluidsList.isEmpty()) {
						readSlot(fluidsList, results, slotCount);
					}
				}
			}
		}
		return results;
	}
}