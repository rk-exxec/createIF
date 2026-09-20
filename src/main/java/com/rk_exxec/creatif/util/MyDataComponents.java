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
