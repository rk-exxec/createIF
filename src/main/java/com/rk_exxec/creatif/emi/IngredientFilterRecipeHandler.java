package com.rk_exxec.creatif.emi;

import java.util.ArrayList;
import java.util.List;

import com.rk_exxec.creatif.CreateIngredientFilter;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;

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
		return recipe.getOutputs().stream().anyMatch(this::isItem)
			&& recipe.getInputs().stream().anyMatch(this::isItem);
			// && (recipe instanceof BasinRecipe || recipe instanceof ShapelessRecipe ||
			// 	recipe instanceof ShapedRecipe || recipe instanceof CompactingRecipe);
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
			ItemStack stack = firstItem(ingredient);
			if (!stack.isEmpty() && ingredients.size() < menu.ghostInventory.getSlots()) {
				stack.setCount(1);
				ingredients.add(stack);
			}
		}

		ItemStack output = recipe.getOutputs().stream()
			.map(EmiStack::getItemStack)
			.filter(stack -> !stack.isEmpty())
			.findFirst()
			.orElse(ItemStack.EMPTY);
		if(!output.isEmpty()) output.setCount(1);
		menu.applyRecipe(ingredients, output);
		CreateIngredientFilter.CHANNEL.sendToServer(
			new IngredientFilterScreenPacket(menu.createRecipeData()));
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