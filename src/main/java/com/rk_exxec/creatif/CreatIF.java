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


package com.rk_exxec.creatif;

import com.mojang.logging.LogUtils;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.slf4j.Logger;

import com.rk_exxec.creatif.util.MyDataComponents;
import com.rk_exxec.creatif.util.MyItems;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.rk_exxec.creatif.util.MyPackets;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.minecraft.resources.ResourceKey;



// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreatIF.MODID)
public class CreatIF
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "creatif";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String NETWORK_VERSION = "1";

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
		.defaultCreativeTab((ResourceKey<CreativeModeTab>) AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
		.setTooltipModifierFactory(item ->
			new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item)))
		);

    public static final int I_SCREEN_Y_OFFSET = -10;

    public static final boolean WAILA_ACTIVE = false;
                // ModList.get().isLoaded("waila") ||
                // ModList.get().isLoaded("hwyla") ||
                // ModList.get().isLoaded("jade"); // this doesnt work i think


    public CreatIF(IEventBus modEventBus, ModContainer container)
    {
        REGISTRATE.registerEventListeners(modEventBus);

        MyItems.register();
        MyDataComponents.register(modEventBus);
        MyMenuTypes.register();
        MyPackets.register();

    }

    @SubscribeEvent 
    private static void commonSetup(FMLLoadCompleteEvent event)
    {
        // Some common setup code
        LOGGER.info("Sorry, you're not getting in tonight!");
    }

    // @SubscribeEvent 
    // private static void addCreative(BuildCreativeModeTabContentsEvent event)
    // {
    //     if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
    //         event.accept(MyItems.INGREDIENT_FILTER_ITEM.asItem());
    // }

    public static CreateRegistrate getRegistrate(){
        return REGISTRATE;
    }
}
