package com.rk_exxec.creatif.util;

import org.checkerframework.checker.units.qual.C;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.filter.IngredientFilterScreen;
import com.simibubi.create.AllMenuTypes;
import com.tterrag.registrate.builders.MenuBuilder.ForgeMenuFactory;
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MyMenuTypes extends AllMenuTypes{


	public static final MenuEntry<IngredientFilterMenu> INGREDIENT_FILTER =
		register("ingredient_filter", IngredientFilterMenu::new, () -> IngredientFilterScreen::new);
    
    @SuppressWarnings("hiding")
    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
		String name, ForgeMenuFactory<C> factory, NonNullSupplier<ScreenFactory<C, S>> screenFactory) {
		return CreatIF.getRegistrate()
			.menu(name, factory, screenFactory)
			.register();
	}

	public static void register() {
	}

}