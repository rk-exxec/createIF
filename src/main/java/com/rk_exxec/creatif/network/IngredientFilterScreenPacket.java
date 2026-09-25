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
