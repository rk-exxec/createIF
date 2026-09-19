package com.rk_exxec.creatif.util;

import com.rk_exxec.creatif.CreatIF;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class CreatIFLang {
    public static MutableComponent translate(String category, String key) {
        return Component.translatable(category + "." + CreatIF.MODID + "." + key);
    }
    public static MutableComponent translateDirect(String prefix, String key) {
        return Component.translatable(prefix + "." + key);
    }
    public static MutableComponent translateRaw(String key) {
        return Component.translatable(key);
    }
}
