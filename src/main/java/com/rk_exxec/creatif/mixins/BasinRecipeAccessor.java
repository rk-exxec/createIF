package com.rk_exxec.creatif.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import net.minecraft.world.item.crafting.Recipe;

@Mixin(BasinRecipe.class)
public interface BasinRecipeAccessor {
    
    // Why tf is this private
    @Invoker("apply")
    static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test)
    {
        throw new AssertionError("Untransformed @Accessor");
    }

}
