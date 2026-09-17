package com.rk_exxec.creatif.util;

import com.rk_exxec.creatif.CreateIngredientFilter;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class CreatIFLang {
    public static MutableComponent translate(String category, String key) {
        return Component.translatable(category + "." + CreateIngredientFilter.MODID + "." + key);
    }
    public static MutableComponent translateDirect(String prefix, String key) {
        return Component.translatable(prefix + "." + key);
    }
}
