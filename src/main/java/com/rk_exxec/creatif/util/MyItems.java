package com.rk_exxec.creatif.util;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterItem;
import com.simibubi.create.AllCreativeModeTabs;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class MyItems {
    private static final CreateRegistrate REGISTRATE = CreatIF.getRegistrate();

	static {
		REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
	}

    public static final ItemEntry<IngredientFilterItem> INGREDIENT_FILTER_ITEM = REGISTRATE.item("ingredient_filter", IngredientFilterItem::new).properties(p -> p.stacksTo(1))
		.register();

    
	public static void register() {
	}
}
