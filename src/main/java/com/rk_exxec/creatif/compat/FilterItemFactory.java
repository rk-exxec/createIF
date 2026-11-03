package com.rk_exxec.creatif.compat;

import java.util.List;

import com.simibubi.create.content.logistics.filter.FilterItemStack.AttributeFilterItemStack;
import com.simibubi.create.content.logistics.filter.FilterItemStack.ListFilterItemStack;

import net.minecraft.tags.TagKey;
import net.minecraftforge.common.extensions.IForgeItemStack;

public class FilterItemFactory {
    public static ListFilterItemStack toListFilterItemStack(List<? extends IForgeItemStack> items){
        return null;
    }

    public static AttributeFilterItemStack toAttributeFilterItemStack(TagKey<?> tagKey){
        	// protected void saveData(ItemStack filterItem) {
	// 	filterItem.getOrCreateTag()
	// 			.putInt("WhitelistMode", whitelistMode.ordinal());
	// 	ListTag attributes = new ListTag();
	// 	selectedAttributes.forEach(at -> {
	// 		if (at == null)
	// 			return;

	// 		CompoundTag compoundNBT = ItemAttribute.saveStatic(at.getFirst());
	// 		compoundNBT.putBoolean("Inverted", at.getSecond());
	// 		attributes.add(compoundNBT);
	// 	});
	// 	filterItem.getOrCreateTag()
	// 		.put("MatchedAttributes", attributes);

	// 	if (attributes.isEmpty() && whitelistMode == WhitelistMode.WHITELIST_DISJ)
	// 		filterItem.setTag(null);
	// }
        return null;
    }
}
