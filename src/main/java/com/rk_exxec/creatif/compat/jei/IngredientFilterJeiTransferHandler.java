package com.rk_exxec.creatif.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.rk_exxec.creatif.util.MyPackets;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

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

	@Override
	public IRecipeTransferError transferRecipe(IngredientFilterMenu menu, Object recipe,
		IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
		List<ItemStack> ingredients = new ArrayList<>();
		for (IRecipeSlotView slot : recipeSlots.getSlotViews()) {
			if (slot.getRole() != RecipeIngredientRole.INPUT
				&& slot.getRole() != RecipeIngredientRole.CATALYST)
				continue;
			Optional<ItemStack> stack = slot.getDisplayedIngredient(VanillaTypes.ITEM_STACK);
			if (stack.isPresent() && !stack.get().isEmpty()
				&& ingredients.size() < menu.ghostInventory.getSlots()) {
				ItemStack copy = stack.get().copy();
				copy.setCount(1);
				ingredients.add(copy);
			}
		}

		if (ingredients.isEmpty())
			return null;

		ItemStack output = recipeSlots.getSlotViews(RecipeIngredientRole.OUTPUT)
			.get(0).getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.EMPTY)
			.copy();
		if (!output.isEmpty())
			output.setCount(1);


		if (doTransfer) {
			menu.applyRecipe(ingredients, output);
			MyPackets.getChannel().sendToServer(
			 	new IngredientFilterScreenPacket(menu.createRecipeData()));
		}
		return null;
	}
}