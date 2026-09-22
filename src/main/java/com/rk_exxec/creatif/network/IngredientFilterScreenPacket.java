package com.rk_exxec.creatif.network;

import com.rk_exxec.creatif.CreatIF;
import com.rk_exxec.creatif.filter.IngredientFilterItem;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent.Context;


public class IngredientFilterScreenPacket extends SimplePacketBase {

	public enum IngOption {
		INGR_MATCHANY, INGR_MATCHALL, FILL_RECIPE;
	}

	private final IngOption option;
	private final CompoundTag data;

	public IngredientFilterScreenPacket(IngOption option) {
		this(option, new CompoundTag());
	}

	public IngredientFilterScreenPacket(IngOption option, CompoundTag data) {
		this.option = option;
		this.data = data;
	}

	public IngredientFilterScreenPacket(CompoundTag data) {
		this(IngOption.FILL_RECIPE, data);
	}

	public IngredientFilterScreenPacket(FriendlyByteBuf buffer) {
		option = IngOption.values()[buffer.readInt()];
		data = buffer.readNbt();
	}


	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(option.ordinal());
		buffer.writeNbt(data);
	}

	@Override
	public boolean handle(Context context) {
		CreatIF.LOGGER.debug("Enter packet handler");
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null)
				return;
			
            if (player.containerMenu instanceof IngredientFilterMenu c){
				CreatIF.LOGGER.debug("Option is " + option);
                if (option == IngOption.INGR_MATCHALL)
					c.matchAny = false;
				if (option == IngOption.INGR_MATCHANY)
					c.matchAny = true;
				if (option == IngOption.FILL_RECIPE){
					c.applyRecipeData(data);

            	}
			}

		});
		return true;
	}

}
