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

import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.rk_exxec.creatif.CreatIF;
import com.simibubi.create.AllDataComponents;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.component.DataComponentType.Builder;

public class MyDataComponents extends AllDataComponents {

    private static final DeferredRegister.DataComponents MY_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreatIF.MODID);
    public static final DataComponentType<ItemContainerContents> FILTER_OUTPUT = register(
			"filter_output",
			builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC)
	);

    public static final DataComponentType<Boolean> FILTER_MATCH_ANY = register(
			"filter_match_all",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
	);


    private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder) {
		DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
		MY_DATA_COMPONENTS.register(name, () -> type);
		return type;
	}

    @Internal
	public static void register(IEventBus modEventBus) {
		MY_DATA_COMPONENTS.register(modEventBus);
	}
}
