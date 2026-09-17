package com.rk_exxec.creatif.filter;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.rk_exxec.creatif.CreateIngredientFilter;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;


// @Mixin(FilteringBehaviour.class)
public class IngredientFilteringBehaviour extends FilteringBehaviour{//} BlockEntityBehaviour implements ValueSettingsBehaviour, IIngredientFilterBehaviour {

    // @Shadow(remap=false) public boolean isActive() {return true;}

    // @Shadow(remap=false) public FilterItemStack filter;

    public static final BehaviourType<IngredientFilteringBehaviour> TYPE = new BehaviourType<>();
    // @Shadow(remap=false) public SmartBlockEntity blockEntity;

    // IngredientFilterItemStack filter;

    public <T> boolean test(NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        if(itemStacks.isEmpty() && fluidStacks.isEmpty()) return false;
        if(filter == null){
            CreateIngredientFilter.LOGGER.debug("filter is null");
            return  false;
        }
        IngredientFilterItemStack filterItemStack = (IngredientFilterItemStack)filter;
        // CreateIngredientFilter.LOGGER.debug(filterItemStack.toString());
		return !isActive() || filterItemStack.test(blockEntity.getLevel(), itemStacks, fluidStacks);
	}

    public IngredientFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    // @Shadow(remap=false)
    // public boolean testHit(Vec3 hit) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'testHit'");
    // }

    // @Shadow(remap=false)
    // public ValueBoxTransform getSlotPositioning() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getSlotPositioning'");
    // }

    // @Shadow(remap=false)
    // public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'createBoard'");
    // }

    // @Shadow(remap=false)
    // public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'setValueSettings'");
    // }

    // @Shadow(remap=false)
    // public ValueSettings getValueSettings() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getValueSettings'");
    // }

    // @Shadow(remap=false)
    // public BehaviourType<?> getType() {
    //     // TODO Auto-generated method stub
    //     return BehaviourType<IIngredientFilteringBehaviour>;
    // }

    public IngredientFilterItemStack getFilterStack() {
        // TODO Auto-generated method stub
        return (IngredientFilterItemStack) filter;
    }    
}
