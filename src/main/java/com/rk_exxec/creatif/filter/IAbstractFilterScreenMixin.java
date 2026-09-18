package com.rk_exxec.creatif.filter;

public interface IAbstractFilterScreenMixin {
    // allows skipping init of other items to implement custom ui
    public void onlySuperInit();
}
