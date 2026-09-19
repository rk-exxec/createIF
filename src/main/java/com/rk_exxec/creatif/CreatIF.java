package com.rk_exxec.creatif;

import com.mojang.logging.LogUtils;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.filter.IngredientFilterScreen;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.util.MyGuiTextures;
import com.rk_exxec.creatif.util.MyItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.rk_exxec.creatif.filter.IngredientFilterItem;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import net.minecraftforge.network.NetworkEvent.Context;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

import java.util.Properties;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreatIF.MODID)
public class CreatIF
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "creatif";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // public static final RegistryObject<IngredientFilterItem> INGREDIENT_FILTER_ITEM = ITEMS.register("ingredient_filter",
        // () -> new IngredientFilterItem(new Item.Properties().stacksTo(1)));

    

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
		.defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
		.setTooltipModifierFactory(item ->
			new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item)))
		);

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final RegistryObject<MenuType<IngredientFilterMenu>> INGREDIENT_FILTER_MENU = MENU_TYPES.register("ingredient_filter", CreatIF::createIngredientFilterMenu);
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(MODID, "main"),
        () -> "1",
        "1"::equals,
        "1"::equals);

    public static final int I_SCREEN_Y_OFFSET = -10;

    public static final boolean WAILA_ACTIVE = false;
                // ModList.get().isLoaded("waila") ||
                // ModList.get().isLoaded("hwyla") ||
                // ModList.get().isLoaded("jade"); // this doesnt work i think


    private static MenuType<IngredientFilterMenu> createIngredientFilterMenu() {
        return IForgeMenuType.create((id, inventory, buffer) -> new IngredientFilterMenu(INGREDIENT_FILTER_MENU.get(), id, inventory, buffer));
    }

    public CreatIF(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        // ITEMS.register(modEventBus);

        MyItems.register();
        // () -> new IngredientFilterItem(new Item.Properties().stacksTo(1)))

        MENU_TYPES.register(modEventBus);
        // CHANNEL.registerMessage(0, SetMatchAnyPacket.class, SetMatchAnyPacket::encode, SetMatchAnyPacket::decode, SetMatchAnyPacket::handle);

        // CHANNEL.registerMessage(0, IngredientFilterScreenPacket.class, IngredientFilterScreenPacket::new, null, PLAY_TO_SERVER);
        CHANNEL.messageBuilder(IngredientFilterScreenPacket.class, 0, PLAY_TO_SERVER)
				.encoder(IngredientFilterScreenPacket::write)
				.decoder(IngredientFilterScreenPacket::new)
				.consumerNetworkThread((packet, contextSupplier) -> {
                        Context ncontext = contextSupplier.get();
                        if (packet.handle(ncontext)) {
                            ncontext.setPacketHandled(true);
                        }
			        })
				.add();

        modEventBus.addListener(this::addCreative);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        // context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("Sorry, you're not getting in tonight!");

        // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(MyItems.INGREDIENT_FILTER_ITEM.asItem());
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> registerIngredientFilterScreen());
            // Some client setup code
            // LOGGER.info("HELLO FROM CLIENT SETUP");
            // LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static void registerIngredientFilterScreen()
        {
            MenuScreens.register((MenuType) INGREDIENT_FILTER_MENU.get(), (ScreenConstructor) (menu, inventory, title) -> new IngredientFilterScreen((IngredientFilterMenu) menu, inventory, title));
        }
    }

    public static CreateRegistrate getRegistrate(){
        return REGISTRATE;
    }
}
