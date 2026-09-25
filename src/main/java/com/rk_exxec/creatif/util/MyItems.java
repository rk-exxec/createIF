/*=====================================================================
CreatIF- Create: Ingredient Filter 
Adds a new filter type to select basin recipes based on input
Copyright (C) 2026  rk-exxec

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
=====================================================================*/

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
