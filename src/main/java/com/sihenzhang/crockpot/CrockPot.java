package com.sihenzhang.crockpot;

import com.sihenzhang.crockpot.levelgen.CrockPotFeatures;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CrockPot.MOD_ID)
public final class CrockPot {
    public static final String MOD_ID = "crockpot";

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return CrockPotRegistry.crockPotBasicBlockItem.get().getDefaultInstance();
        }
    };

    public CrockPot() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(CrockPotFeatures::onCommonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrockPotConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CrockPotConfig.CLIENT_CONFIG);
        CrockPotRegistry.MOB_EFFECTS.register(modEventBus);
        CrockPotRegistry.ITEMS.register(modEventBus);
        CrockPotRegistry.BLOCKS.register(modEventBus);
        CrockPotRegistry.BLOCK_ENTITIES.register(modEventBus);
        CrockPotRegistry.CONTAINERS.register(modEventBus);
        CrockPotRegistry.ENTITIES.register(modEventBus);
        CrockPotRegistry.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CrockPotRegistry.RECIPE_TYPES.register(modEventBus);
        CrockPotRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        CrockPotRegistry.FEATURES.register(modEventBus);
    }
}
