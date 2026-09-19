package com.rk_exxec.creatif.compat.emi;

import com.rk_exxec.creatif.CreatIF;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class CreateIngredientFilterEmiPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addRecipeHandler(CreatIF.INGREDIENT_FILTER_MENU.get(),
			new IngredientFilterRecipeHandler());
	}
}