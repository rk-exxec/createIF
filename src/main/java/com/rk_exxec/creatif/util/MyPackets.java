package com.rk_exxec.creatif.util;

import java.util.Locale;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;


import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public enum MyPackets implements BasePacketPayload.PacketTypeProvider  {
    CONFIGURE_ING_FILTER(IngredientFilterScreenPacket.class, IngredientFilterScreenPacket.STREAM_CODEC);
    
    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload> MyPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		String name = this.name().toLowerCase(Locale.ROOT);
		this.type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CreatIF.MODID, name)),
			clazz, codec
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
		return (CustomPacketPayload.Type<T>) this.type.type();
	}

	public static void register() {
		CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreatIF.MODID, CreatIF.NETWORK_VERSION);
		for (MyPackets packet : MyPackets.values()) {
			packetRegistry.registerPacket(packet.type);
		}
		packetRegistry.registerAllPackets();
	}
}
