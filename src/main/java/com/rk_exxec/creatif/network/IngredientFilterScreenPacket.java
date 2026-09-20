package com.rk_exxec.creatif.network;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.util.MyPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;


public record IngredientFilterScreenPacket(IngOption option, CompoundTag data) implements ServerboundPacketPayload{
public static final StreamCodec<ByteBuf, IngredientFilterScreenPacket> STREAM_CODEC = StreamCodec.composite(
		IngOption.STREAM_CODEC, IngredientFilterScreenPacket::option,
		CatnipStreamCodecBuilders.nullable(ByteBufCodecs.COMPOUND_TAG), IngredientFilterScreenPacket::data,
		IngredientFilterScreenPacket::new
	);


	public IngredientFilterScreenPacket(IngOption option) {
		this(option, null);
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return MyPackets.CONFIGURE_ING_FILTER;
	}


	@Override
	public void handle(ServerPlayer player) {
		CompoundTag tag = this.data == null ? new CompoundTag() : this.data;
		CreatIF.LOGGER.debug("Enter packet handler");

		if (player == null)
			return;
		
		if (player.containerMenu instanceof IngredientFilterMenu c){
			CreatIF.LOGGER.debug("Option is " + this.option);
			if (this.option == IngOption.INGR_MATCHALL)
				c.matchAny = false;
			if (this.option == IngOption.INGR_MATCHANY)
				c.matchAny = true;
			if (this.option == IngOption.FILL_RECIPE)
				c.applyRecipeData(tag);
		}
	}

	public enum IngOption {
		INGR_MATCHANY, INGR_MATCHALL, FILL_RECIPE;
		public static final StreamCodec<ByteBuf, IngOption> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(IngOption.class);
	}
}
