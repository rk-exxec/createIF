package com.rk_exxec.creatif.compat.emi;

import com.rk_exxec.creatif.gui.IngredientFilterMenu;
import com.rk_exxec.creatif.gui.IngredientFilterScreen;
import com.rk_exxec.creatif.gui.IngredientSlot;
import com.rk_exxec.creatif.gui.IngredientStack;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.util.MyPackets;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class IngredientFilterEmiDragDropHandler
		extends EmiDragDropHandler.SlotBased<IngredientFilterScreen> {

	public IngredientFilterEmiDragDropHandler() {
		super((screen, slot) -> slot instanceof IngredientSlot,
			(screen, slot, ingredient) -> {
				IngredientStack stack = toIngredientStack(ingredient);
				if (stack == null)
					return;

				((IngredientSlot) slot).setIngredientStack(stack);
				sendUpdate(screen.getMenu());
			});
	}

	private static IngredientStack toIngredientStack(EmiIngredient ingredient) {
		if (ingredient.isEmpty() || ingredient.getEmiStacks().isEmpty())
			return null;

		EmiStack stack = ingredient.getEmiStacks().get(0);
		if (stack.getKey() instanceof Fluid fluid)
			return IngredientStack.of(new FluidStack(fluid, 1, stack.getNbt()));

		ItemStack itemStack = stack.getItemStack();
		return itemStack.isEmpty() ? null : IngredientStack.of(itemStack.copyWithCount(1));
	}

	private static void sendUpdate(IngredientFilterMenu menu) {
		MyPackets.getChannel().sendToServer(
			new IngredientFilterScreenPacket(menu.createRecipeData()));
	}
}