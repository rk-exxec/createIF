package com.rk_exxec.creatif;

import com.mojang.logging.LogUtils;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;


import com.rk_exxec.creatif.util.MyItems;
import com.rk_exxec.creatif.util.MyMenuTypes;
import com.rk_exxec.creatif.util.MyPackets;
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

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);    

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
		.defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
		.setTooltipModifierFactory(item ->
			new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item)))
		);

    public static final int I_SCREEN_Y_OFFSET = -10;

    public static final boolean WAILA_ACTIVE = false;
                // ModList.get().isLoaded("waila") ||
                // ModList.get().isLoaded("hwyla") ||
                // ModList.get().isLoaded("jade"); // this doesnt work i think

    public CreatIF(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);

        MyItems.register();
        MyMenuTypes.register();
        MyPackets.registerPackets();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("Sorry, you're not getting in tonight!");
    }

    public static CreateRegistrate getRegistrate(){
        return REGISTRATE;
    }
}
