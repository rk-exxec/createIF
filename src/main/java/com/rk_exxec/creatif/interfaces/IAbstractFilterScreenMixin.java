package com.rk_exxec.creatif.interfaces;

public interface IAbstractFilterScreenMixin {
    // allows skipping init of other items to implement custom ui
    public void onlySuperInit();
}
