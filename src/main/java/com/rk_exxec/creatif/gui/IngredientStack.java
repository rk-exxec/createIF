/*=====================================================================
CreatIF - Create: Ingredient Filter 
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

package com.rk_exxec.creatif.gui;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;


/**
 * A class that combines ItemStack and Forge FluidStack into a single class for use in UI rendering invetories with both items and raw liquids
 * The interfaces are not complete! Do not use this for anything else!
 * IngredientStack
 */
public class IngredientStack {

    public static final Codec<IngredientStack> CODEC = RecordCodecBuilder.create((p_258963_) -> {
      return p_258963_.group(ForgeRegistries.ITEMS.getCodec().fieldOf("id").forGetter(IngredientStack::getItem),ForgeRegistries.FLUIDS.getCodec().fieldOf("id").forGetter(IngredientStack::getFluid), Codec.INT.fieldOf("Count").forGetter(IngredientStack::getCount), Codec.BOOL.fieldOf("isFluid").forGetter(IngredientStack::isFluid),
      CompoundTag.CODEC.optionalFieldOf("tag").forGetter((p_281115_) -> {
         return Optional.ofNullable(p_281115_.getTag());
      })).apply(p_258963_, IngredientStack::new);
   });

    public static final IngredientStack EMPTY = new IngredientStack();
  
    final ItemStack itemStack;
    final FluidStack fluidStack;
    final boolean isFluid;


    public IngredientStack(ItemStack item){
        itemStack = item;
        fluidStack = FluidStack.EMPTY;
        isFluid = false;
    }

    public IngredientStack(FluidStack fluid){
        itemStack = ItemStack.EMPTY;
        fluidStack = fluid;
        isFluid = true;
    }

    public IngredientStack(Item item){
        this(item, 1);
    }

    public IngredientStack(Fluid fluid){
        this(fluid, 1);   
    }

    public IngredientStack(Item item, int amount){
        itemStack = new ItemStack(item, amount);
        fluidStack = FluidStack.EMPTY;
        isFluid = false;
    }

    public IngredientStack(Fluid fluid, int amount){
        itemStack = ItemStack.EMPTY;
        fluidStack = new FluidStack(fluid, amount);
        isFluid = true;
    }

    public IngredientStack(Item item, int amount, Optional<CompoundTag> nbt){
        this(new ItemStack(item, amount, nbt.orElse(null)));
    }

    public IngredientStack(Fluid fluid, int amount, Optional<CompoundTag> nbt){
        this(new FluidStack(fluid, amount, nbt.orElse(null)));
    }

    public IngredientStack(IngredientStack other){
        itemStack = other.itemStack.copy();
        fluidStack = other.fluidStack.copy();
        isFluid = other.isFluid;
    }

    public IngredientStack(){
        itemStack = ItemStack.EMPTY;
        fluidStack = FluidStack.EMPTY;
        isFluid = false;
    }

    public IngredientStack(boolean isFluid, Object ingr, int count, CompoundTag nbt){
        if(!(ingr instanceof Fluid) && !(ingr instanceof Item))
            throw new IllegalArgumentException("Constructor IngredientStack(boolean isFluid, Object ingr, int count, CompoundTag nbt) only takes Fluid or Item objects for ingr!");
        if(isFluid) {
            itemStack = ItemStack.EMPTY;
            fluidStack = new FluidStack((Fluid) ingr, count, nbt);
        }
        else{
            itemStack = new ItemStack((Item) ingr, count, nbt);
            fluidStack = FluidStack.EMPTY;
        }
        this.isFluid = isFluid;
    }

    public IngredientStack(CompoundTag nbt) {
        ResourceLocation loc = ResourceLocation.parse(nbt.getString("id"));
        int count = nbt.getInt("Count");
        CompoundTag nestedNbt = nbt.getCompound("tag");
        if(nbt.getBoolean("isFluid")){
            fluidStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(loc),count, nestedNbt);
            itemStack = ItemStack.EMPTY;
            isFluid = true;
        }
        else {
            itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(loc), count, nestedNbt);
            fluidStack = FluidStack.EMPTY;
            isFluid = false;
        }
    }

    IngredientStack(Item i, Fluid f, Integer c, Boolean isF, Optional<CompoundTag> tag){
        if(isF){
            fluidStack = new FluidStack(f,c, tag.orElse(null));
            itemStack = ItemStack.EMPTY;
            isFluid = true;
        }
        else {
            itemStack = new ItemStack(i, c, tag.orElse(null));
            fluidStack = FluidStack.EMPTY;
            isFluid = false;
        }
    }

    public CompoundTag save(CompoundTag p_41740_) {
        ResourceLocation resourcelocation = !isFluid()?
            ForgeRegistries.ITEMS.getKey(getItem()):
            ForgeRegistries.FLUIDS.getKey(getFluid());
        p_41740_.putBoolean("isFluid", isFluid());
        p_41740_.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        p_41740_.putByte("Count", (byte)this.getCount());
        if (this.getTag() != null) {
            p_41740_.put("tag", this.getTag().copy());
        }
        return p_41740_;
    }

    public static IngredientStack of(CompoundTag nbt){
        return new IngredientStack(nbt);
    }


    public static IngredientStack of(ItemStack item){
        return new IngredientStack(item);
    }

    public static IngredientStack of(FluidStack fluid){
        return new IngredientStack(fluid);
    }

    public boolean isFluid(){
        return fluidStack != FluidStack.EMPTY && itemStack == ItemStack.EMPTY;
    }

    public Optional<ItemStack> getItemStack(){
        if(isEmpty() || isFluid()) return Optional.empty();
        else return Optional.of(itemStack);
    }

    public ItemStack itemStack(){
        return itemStack;
    }

    public Optional<FluidStack> getFluidStack(){
        if(isEmpty() || !isFluid()) return Optional.empty();
        else return Optional.of(fluidStack);
    }

    public int getMaxStackSize(){
        if(isFluid) return 0;
        else return itemStack.getMaxStackSize();
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public Item getItem() {
        return (this.isEmpty() || isFluid()) ? Items.AIR : itemStack.getItem();
    }

    public Fluid getFluid() {
        return (this.isEmpty() || !isFluid()) ? Fluids.EMPTY : fluidStack.getFluid();
    }

    public final Fluid getRawFluid()
    {
        return fluidStack.getFluid();
    }


    public IngredientStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new IngredientStack(this);
        }
    }

    public IngredientStack copyWithCount(int count) {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            IngredientStack itemstack = this.copy();
            itemstack.setCount(count);
            return itemstack;
        }
    }

    public void grow(int amount) {
        if(isFluid()) fluidStack.grow(amount);
        else itemStack.grow(amount);
    }

    public void shrink(int amount) {
        if(isFluid()) fluidStack.shrink(amount);
        else itemStack.shrink(amount);
    }

    public boolean hasTag()
    {
        if(isFluid()) return fluidStack.hasTag();
        else return itemStack.hasTag();
    }

    public CompoundTag getTag()
    {
        if(isFluid()) return fluidStack.getTag();
        else return itemStack.getTag();
    }

    public void setTag(CompoundTag tag)
    {
        if(isFluid()) fluidStack.setTag(tag);
        else itemStack.setTag(tag);
    }

    public CompoundTag getOrCreateTag()
    {
        if(isFluid()) return fluidStack.getOrCreateTag();
        else return itemStack.getOrCreateTag();
    }



    public static boolean matches(IngredientStack a, IngredientStack b) {
        return a.itemStack == b.itemStack && a.fluidStack == b.fluidStack;
    }

    public String toString() {
        return this.getCount() + " " + (isFluid()?getFluid():this.getItem());
    }

    public String getDescriptionId() {
        if(isFluid())
            return this.getFluid().getFluidType().getDescriptionId();
        else
            return this.getItem().getDescriptionId(itemStack);
    }   

    public Component getHoverName() {
        if(isFluid()) return fluidStack.getDisplayName();
        else return itemStack.getHoverName();
    }

    public IngredientStack setHoverName(@Nullable Component p_41715_) {
        if(isFluid()) ;
        else itemStack.setHoverName(p_41715_);

        return this;
    }

    public void resetHoverName() {
        if(isFluid()) ;
        else itemStack.resetHoverName();
    }

    public boolean hasCustomHoverName() {
        if(isFluid()) return false;
        else return itemStack.hasCustomHoverName();
    }

    public List<Component> getTooltipLines(@Nullable Player p_41652_, TooltipFlag p_41653_) {
        if(isFluid()) return getFluidHoverName();
        else return itemStack.getTooltipLines(p_41652_,p_41653_);
    }

    public List<Component> getFluidHoverName() {
        List<Component> list = Lists.newArrayList();
        // list.add(Component.translatable(fluidStack.getTranslationKey()));
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid());

        // Component name = fluidStack.getFluid().getFluidType().getDescription();
        String namespace = id.getNamespace();

        String modName = ModList.get()
                .getModContainerById(namespace)
                .map(container -> container.getModInfo().getDisplayName())
                .orElse(namespace);
        list.add(Component.translatable(fluidStack.getTranslationKey()));
        list.add(Component.literal(modName).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
        return list;
    }


    public Optional<TooltipComponent> getTooltipImage(){
        return Optional.empty();
    }

    public Component getDisplayName() {
        if(isFluid()) return fluidStack.getDisplayName();
        else return itemStack.getDisplayName();
    }

    public int getCount() {
        return this.isEmpty() ? 0 : (isFluid()?fluidStack.getAmount():itemStack.getCount());
    }

    public void setCount(int p_41765_) {
        if(isFluid()) fluidStack.setAmount(p_41765_);
        else itemStack.setCount(p_41765_);
    }

    public int getAmount() {
        return this.getCount();
    }

    public void setAmount(int p_41765_) {
        this.setCount(p_41765_);
    }

    public boolean isFluidEqual(@NotNull FluidStack other)
    {
        return isFluid() && fluidStack.getFluid().getFluidType() == other.getFluid().getFluidType();
    }

    private boolean isFluidStackTagEqual(FluidStack other)
    {
        return isFluid() && fluidStack.isFluidStackIdentical(other);
    }

    public static boolean areFluidStackTagsEqual(@NotNull IngredientStack stack1, @NotNull FluidStack stack2)
    {
        return stack1.isFluidStackTagEqual(stack2);
    }

    public boolean containsFluid(@NotNull FluidStack other)
    {
        return isFluid() && isFluidEqual(other) && fluidStack.getAmount() >= other.getAmount();
    }


    public boolean isFluidStackIdentical(FluidStack other)
    {
        return isFluid() && isFluidEqual(other) && fluidStack.getAmount() >= other.getAmount();
    }

    public boolean isFluidEqual(@NotNull ItemStack other)
    {
        return FluidUtil.getFluidContained(other).map(this::isFluidEqual).orElse(false);
    }

    public final int hashCode()
    {
        int code = isFluid()?1:0;
        code = 31*code + getFluid().hashCode();
        if (getTag() != null)
            code = 31*code + getTag().hashCode();
        return code;
    }

    public final boolean equals(Object o)
    {
        if (o instanceof FluidStack stack)
            return fluidStack == stack;
        else if(o instanceof ItemStack stack)
            return itemStack == stack;
        else if(o instanceof IngredientStack ing)
            return matches(this, ing);
        else
            return false;
    }

    public static void renderIngredient(
        GuiGraphics guiGraphics,
        Minecraft minecraft,
        IngredientStack ingredient,
        int x,
        int y
    ) {
        if (ingredient == null || ingredient.isEmpty())
            return;

        if (ingredient.isFluid()) {
            FluidStack fluid = ingredient.getFluidStack()
                    .orElse(FluidStack.EMPTY);

            if (fluid.isEmpty())
                return;

            IClientFluidTypeExtensions fluidType =
                    IClientFluidTypeExtensions.of(fluid.getFluid());

            ResourceLocation texture =
                    fluidType.getStillTexture(fluid);

            TextureAtlasSprite sprite =
                    minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                            .apply(texture);

            int tint = fluidType.getTintColor(fluid);

            float a = ((tint >> 24) & 0xFF) / 255.0F;
            float r = ((tint >> 16) & 0xFF) / 255.0F;
            float g = ((tint >> 8) & 0xFF) / 255.0F;
            float b = (tint & 0xFF) / 255.0F;

            RenderSystem.setShaderColor(r, g, b, a);

            guiGraphics.blit(
                    x, y,
                    0,
                    16, 16,
                    sprite
            );

            RenderSystem.setShaderColor(1, 1, 1, 1);
        } else {
            ingredient.getItemStack().ifPresent(stack ->
                    guiGraphics.renderItem(stack, x, y)
            );
        }
    }

    public static void renderFluid(
            GuiGraphics guiGraphics,
            FluidStack fluid,
            int x,
            int y
        ) {
        if (fluid.isEmpty())
            return;

        Minecraft minecraft = Minecraft.getInstance();

        IClientFluidTypeExtensions fluidType =
                IClientFluidTypeExtensions.of(fluid.getFluid());

        ResourceLocation texture =
                fluidType.getStillTexture(fluid);

        if (texture == null)
            return;

        TextureAtlasSprite sprite =
                minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);

        int tint = fluidType.getTintColor(fluid);

        float alpha = ((tint >> 24) & 0xFF) / 255.0F;
        float red   = ((tint >> 16) & 0xFF) / 255.0F;
        float green = ((tint >> 8) & 0xFF) / 255.0F;
        float blue  = (tint & 0xFF) / 255.0F;

        RenderSystem.setShaderColor(red, green, blue, alpha);

        guiGraphics.blit(
            x,
            y,
            0,
            16,
            16,
            sprite
        );

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}


