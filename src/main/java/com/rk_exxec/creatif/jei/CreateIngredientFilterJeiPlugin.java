package com.rk_exxec.creatif.jei;

import com.rk_exxec.creatif.CreateIngredientFilter;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class CreateIngredientFilterJeiPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(CreateIngredientFilter.MODID, "jei_plugin");
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addUniversalRecipeTransferHandler(new IngredientFilterJeiTransferHandler());
	}
}