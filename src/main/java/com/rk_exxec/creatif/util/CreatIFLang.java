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
