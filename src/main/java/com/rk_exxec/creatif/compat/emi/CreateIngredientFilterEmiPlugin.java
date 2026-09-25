package com.rk_exxec.creatif.compat.emi;

import com.rk_exxec.creatif.util.MyMenuTypes;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class CreateIngredientFilterEmiPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addRecipeHandler(MyMenuTypes.INGREDIENT_FILTER.get(),
			new IngredientFilterRecipeHandler());
	}
}