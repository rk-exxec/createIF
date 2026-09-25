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

package com.rk_exxec.creatif.compat.emi;

import java.util.ArrayList;
import java.util.List;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.util.CreatIFLang;
import com.rk_exxec.creatif.util.MyPackets;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;


public class IngredientFilterRecipeHandler implements EmiRecipeHandler<IngredientFilterMenu> {
	@Override
	public EmiPlayerInventory getInventory(AbstractContainerScreen<IngredientFilterMenu> screen) {
		List<EmiStack> stacks = new ArrayList<>();
		if (Minecraft.getInstance().player != null) {
			for (ItemStack stack : Minecraft.getInstance().player.getInventory().items)
				stacks.add(EmiStack.of(stack));
		}
		return new EmiPlayerInventory(stacks);
	}

	@Override 
	public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<IngredientFilterMenu> context) {
		ArrayList<ClientTooltipComponent> tooltip = new ArrayList<>();
		if (!canCraft(recipe, context)) {
			tooltip.add(ClientTooltipComponent.create(CreatIFLang.translate("gui","emi.tooltip.needsinputoutput").getVisualOrderText()));
		}
		if(Screen.hasShiftDown()){
			tooltip.add(ClientTooltipComponent.create(Component.keybind("shift").getVisualOrderText()));
			tooltip.add(ClientTooltipComponent.create(CreatIFLang.translate("gui","emi.tooltip.shift").getVisualOrderText()));
		}
		return tooltip;
	}

	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return !recipe.getInputs().isEmpty() && !recipe.getOutputs().isEmpty();
	}

	@Override
	public boolean canCraft(EmiRecipe recipe, EmiCraftContext<IngredientFilterMenu> context) {
		return supportsRecipe(recipe);
	}


	@Override
	public boolean craft(EmiRecipe recipe, EmiCraftContext<IngredientFilterMenu> context) {
		IngredientFilterMenu menu = context.getScreenHandler();
		List<ItemStack> ingredients = new ArrayList<>();
		for (EmiIngredient ingredient : recipe.getInputs()) {
			if (!ingredient.isEmpty() && ingredients.size() < menu.ghostInventory.getSlots())
			for (EmiStack emiStack : ingredient.getEmiStacks()){
				ItemStack itemStack;
				// get bucket of fluid
				if(emiStack.getKey() instanceof Fluid fluid)
					itemStack = FluidUtil.getFilledBucket(new FluidStack(fluid, 1, emiStack.getNbt()));
				else
					itemStack = emiStack.getItemStack().copyWithCount(1);
				if (!ingredients.stream().anyMatch(i -> i.is(itemStack.getItemHolder()))) {
					ingredients.add(itemStack);
				}
				if(!Screen.hasShiftDown()) break; // if not shift pressed only use first item in variations
			}
		}
		EmiStack outStack = recipe.getOutputs().get(0);
		ItemStack output;
		if(outStack.getKey() instanceof Fluid fluid)
			output = FluidUtil.getFilledBucket(new FluidStack(fluid, 1, outStack.getNbt()));
		else
			output = outStack.getItemStack().copyWithCount(1);

		try{
			menu.applyRecipe(ingredients, output);
			MyPackets.getChannel().sendToServer(
				new IngredientFilterScreenPacket(menu.createRecipeData()));
		}
		catch(Exception e){
			CreatIF.LOGGER.error("Error in setting items", e);
			throw e;
		}
		return true;
	}
}