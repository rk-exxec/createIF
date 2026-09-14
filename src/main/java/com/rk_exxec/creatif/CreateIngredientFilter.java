package com.rk_exxec.creatif;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.rk_exxec.creatif.filter.IngredientFilterMenu;
import com.rk_exxec.creatif.filter.IngredientFilterScreen;
import com.rk_exxec.creatif.filter.IngredientFilterItem;
import com.rk_exxec.creatif.network.IngredientFilterScreenPacket;
import com.rk_exxec.creatif.network.SetMatchAnyPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import net.minecraftforge.network.NetworkEvent.Context;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateIngredientFilter.MODID)
public class CreateIngredientFilter
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "creatif";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> CONTENT_FILTER_ITEM = ITEMS.register("content_filter",
        () -> new IngredientFilterItem(new Item.Properties().stacksTo(1)));

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final RegistryObject<MenuType<IngredientFilterMenu>> CONTENT_FILTER_MENU = MENU_TYPES.register("content_filter", CreateIngredientFilter::createIngredientFilterMenu);
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"),
        () -> "1",
        "1"::equals,
        "1"::equals);


    private static MenuType<IngredientFilterMenu> createIngredientFilterMenu() {
        return IForgeMenuType.create((id, inventory, buffer) -> new IngredientFilterMenu(CONTENT_FILTER_MENU.get(), id, inventory, buffer));
    }

    public CreateIngredientFilter(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

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
        LOGGER.info("HELLO FROM COMMON SETUP");

        // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(CONTENT_FILTER_ITEM);
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
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static void registerIngredientFilterScreen()
        {
            MenuScreens.register((MenuType) CONTENT_FILTER_MENU.get(), (ScreenConstructor) (menu, inventory, title) -> new IngredientFilterScreen((IngredientFilterMenu) menu, inventory, title));
        }
    }
}
