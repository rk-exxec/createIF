package com.rk_exxec.creatif.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.rk_exxec.creatif.gui.IngredientFilterMenu;
import com.rk_exxec.creatif.gui.IngredientFilterScreen;
import com.rk_exxec.creatif.gui.IngredientSlot;
import com.rk_exxec.creatif.gui.IngredientStack;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.util.MyPackets;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IngredientFilterJeiGhostHandler implements IGhostIngredientHandler<IngredientFilterScreen> {
	@Override
	public <I> List<Target<I>> getTargetsTyped(IngredientFilterScreen screen,
		ITypedIngredient<I> ingredient, boolean doStart) {
		if (ingredient.getItemStack().isEmpty()
			&& ingredient.getIngredient(ForgeTypes.FLUID_STACK).isEmpty())
			return List.of();

		IngredientFilterMenu menu = screen.getMenu();
		List<Target<I>> targets = new ArrayList<>();
		for (int slotIndex = 0; slotIndex < menu.ghostInventory.getSlots(); slotIndex++) {
			IngredientSlot slot = (IngredientSlot) menu.slots.get(36 + slotIndex);
			int x = screen.getGuiLeft() + slot.x;
			int y = screen.getGuiTop() + slot.y;
			targets.add(new Target<>() {
				@Override
				public Rect2i getArea() {
					return new Rect2i(x, y, 18, 18);
				}

				@Override
				public void accept(I value) {
					IngredientStack stack = toIngredientStack(value);
					if (stack == null)
						return;
					slot.setIngredientStack(stack);
					sendUpdate(menu);
				}
			});
		}
		return targets;
	}

	@Override
	public void onComplete() {
	}

	private static IngredientStack toIngredientStack(Object ingredient) {
		if (ingredient instanceof ItemStack itemStack)
			return IngredientStack.of(itemStack.copyWithCount(1));
		if (ingredient instanceof FluidStack fluidStack)
			return IngredientStack.of(fluidStack.copy());
		return null;
	}

	private static void sendUpdate(IngredientFilterMenu menu) {
		MyPackets.getChannel().sendToServer(
			new IngredientFilterScreenPacket(menu.createRecipeData()));
	}
}