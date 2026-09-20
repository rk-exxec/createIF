package com.rk_exxec.creatif.util;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterItem;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

public class MyItems extends AllItems{
    private static final CreateRegistrate REGISTRATE = CreatIF.getRegistrate();

	static {
		REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
	}

	static {
		boolean rareCreated = false;
		boolean normalCreated = false;
		for (PackageStyle style : PackageStyles.STYLES) {
			ItemBuilder<PackageItem, CreateRegistrate> packageItem = BuilderTransformers.packageItem(style);

			if (rareCreated && style.rare() || normalCreated && !style.rare())
				packageItem.setData(ProviderType.LANG, NonNullBiConsumer.noop());

			rareCreated |= style.rare();
			normalCreated |= !style.rare();
			packageItem.register();
		}
	}


    public static final ItemEntry<IngredientFilterItem> INGREDIENT_FILTER_ITEM = REGISTRATE.item("ingredient_filter", IngredientFilterItem::new).properties(p -> p.stacksTo(1))
		.register();

    
	public static void register() {
	}
}
