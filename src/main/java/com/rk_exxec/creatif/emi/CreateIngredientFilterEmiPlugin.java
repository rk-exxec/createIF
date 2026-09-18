package com.rk_exxec.creatif.emi;

import com.rk_exxec.creatif.CreateIngredientFilter;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class CreateIngredientFilterEmiPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addRecipeHandler(CreateIngredientFilter.CONTENT_FILTER_MENU.get(),
			new IngredientFilterRecipeHandler());
	}
}