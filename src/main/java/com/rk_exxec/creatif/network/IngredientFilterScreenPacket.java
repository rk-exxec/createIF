/*=====================================================================
CreatIF - Create: Ingredient Filter 
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
import com.rk_exxec.creatif.gui.IngredientFilterMenu;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;


public class IngredientFilterScreenPacket extends SimplePacketBase {

	public enum IngOption {
		WHITELIST, BLACKLIST, RESPECT_DATA, IGNORE_DATA, UPDATE_FILTER_ITEM, INGR_MATCHANY, INGR_MATCHALL, FILL_RECIPE, RAW_FLUID, ITEM_FLUID;
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
				switch(option){
					case WHITELIST:
						c.blacklist = false;
						break;
					case BLACKLIST:
						c.blacklist = true;
						break;
					case RESPECT_DATA:
						c.respectNBT = true;
						break;
					case IGNORE_DATA:
						c.respectNBT = false;
						break;
					case UPDATE_FILTER_ITEM:
						c.ghostInventory.setStackInSlot(
								data.getInt("Slot"),
								net.minecraft.world.item.ItemStack.of(data.getCompound("Item")));
								break;
					case INGR_MATCHALL:
						c.matchAny = false;
						break;
					case INGR_MATCHANY:
						c.matchAny = true;
						break;
					case FILL_RECIPE:
						c.applyRecipeData(data);
						break;
					case RAW_FLUID:
						c.useRawFluids = true;
						break;
					case ITEM_FLUID:
						c.useRawFluids = false;
						break;
				}
			}

		});
		return true;
	}

}
