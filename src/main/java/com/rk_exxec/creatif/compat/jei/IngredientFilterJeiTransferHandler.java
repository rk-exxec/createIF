package com.rk_exxec.creatif.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket.IngOption;
import com.rk_exxec.creatif.util.MyMenuTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class IngredientFilterJeiTransferHandler implements IUniversalRecipeTransferHandler<IngredientFilterMenu> {
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

		ItemStack output = recipeSlots.getSlotViews(RecipeIngredientRole.OUTPUT).stream()
			.map(slot -> slot.getDisplayedIngredient(VanillaTypes.ITEM_STACK))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst()
			.orElse(ItemStack.EMPTY)
			.copy();
		if (!output.isEmpty())
			output.setCount(1);

		if (ingredients.isEmpty())
			return null;
		if (doTransfer) {
			menu.applyRecipe(ingredients, output);
			CatnipServices.NETWORK.sendToServer(
				new IngredientFilterScreenPacket(IngOption.FILL_RECIPE, menu.createRecipeData()));
		}
		return null;
	}
}