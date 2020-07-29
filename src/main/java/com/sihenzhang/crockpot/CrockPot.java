package com.sihenzhang.crockpot;

import com.sihenzhang.crockpot.base.CrockPotIngredientManager;
import com.sihenzhang.crockpot.client.gui.screen.CrockPotScreen;
import com.sihenzhang.crockpot.integration.ModIntegrationTheOneProbe;
import com.sihenzhang.crockpot.recipe.RecipeManager;
import com.sihenzhang.crockpot.registry.CrockPotRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrockPot.MOD_ID)
public class CrockPot {
    public static final String MOD_ID = "crockpot";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(CrockPotRegistry.crockPotBasicBlockItem.get());
        }
    };

    public static final CrockPotIngredientManager INGREDIENT_MANAGER = new CrockPotIngredientManager();
    public static final RecipeManager RECIPE_MANAGER = new RecipeManager();

    public CrockPot() {
        CrockPotRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CrockPotRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CrockPotRegistry.TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CrockPotRegistry.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetupEvent);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendIMCMessage);
    }

    public void sendIMCMessage(InterModEnqueueEvent event) {
        ModList modList = ModList.get();
        if (modList.isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", ModIntegrationTheOneProbe::new);
        }
    }

    public void onServerStarting(FMLServerAboutToStartEvent event) {
        event.getServer().getResourceManager().addReloadListener(INGREDIENT_MANAGER);
        event.getServer().getResourceManager().addReloadListener(RECIPE_MANAGER);
    }

    public void onClientSetupEvent(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(CrockPotRegistry.crockPotContainer.get(), CrockPotScreen::new);
    }
}
