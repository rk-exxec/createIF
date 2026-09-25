package com.rk_exxec.creatif.compat.emi;

import java.util.ArrayList;
import java.util.List;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket.IngOption;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
			CatnipServices.NETWORK.sendToServer(
				new IngredientFilterScreenPacket(IngOption.FILL_RECIPE, menu.createRecipeData()));
		}
		catch(Exception e){
			CreatIF.LOGGER.error("Error in setting items", e);
			throw e;
		}
		return true;
	}

	private boolean isItem(EmiIngredient ingredient) {
		return !firstItem(ingredient).isEmpty();
	}

	private boolean isItem(EmiStack stack) {
		return !stack.getItemStack().isEmpty();
	}

	private ItemStack firstItem(EmiIngredient ingredient) {
		return ingredient.getEmiStacks().stream()
			.map(EmiStack::getItemStack)
			.filter(stack -> !stack.isEmpty())
			.findFirst()
			.orElse(ItemStack.EMPTY);
	}
}