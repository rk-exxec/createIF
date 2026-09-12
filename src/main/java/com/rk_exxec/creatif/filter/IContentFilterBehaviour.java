package com.rk_exxec.creatif.filter;

import net.minecraft.core.NonNullList;

public interface IContentFilterBehaviour {
    public <T> boolean test(NonNullList<T> list);
}
