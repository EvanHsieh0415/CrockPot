package com.sihenzhang.crockpot.client;

import com.sihenzhang.crockpot.CrockPot;
import com.sihenzhang.crockpot.CrockPotRegistry;
import com.sihenzhang.crockpot.client.gui.screen.CrockPotScreen;
import com.sihenzhang.crockpot.client.model.MilkmadeHatModel;
import com.sihenzhang.crockpot.client.renderer.entity.layers.MilkmadeHatLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CrockPot.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Register Screen
            MenuScreens.register(CrockPotRegistry.crockPotMenu.get(), CrockPotScreen::new);
            // Register RenderType
            ItemBlockRenderTypes.setRenderLayer(CrockPotRegistry.unknownCropsBlock.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CrockPotRegistry.asparagusBlock.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CrockPotRegistry.cornBlock.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CrockPotRegistry.eggplantBlock.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CrockPotRegistry.onionBlock.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CrockPotRegistry.pepperBlock.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CrockPotRegistry.tomatoBlock.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MilkmadeHatModel.LAYER_LOCATION, MilkmadeHatModel::createLayer);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(name -> {
            LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>> renderer = event.getSkin(name);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new MilkmadeHatLayer<>(playerRenderer, event.getEntityModels()));
            }
        });
        // TODO: Super Hacky Way! Maybe use ArmorItem and getArmorModel instead.
        Minecraft.getInstance().getEntityRenderDispatcher().renderers.values().forEach(renderer -> {
            if (renderer instanceof ArmorStandRenderer armorStandRenderer) {
                armorStandRenderer.addLayer(new MilkmadeHatLayer<>(armorStandRenderer, event.getEntityModels()));
            }
            if (renderer instanceof HumanoidMobRenderer) {
                @SuppressWarnings("unchecked") HumanoidMobRenderer<Mob, HumanoidModel<Mob>> humanoidMobRenderer = (HumanoidMobRenderer<Mob, HumanoidModel<Mob>>) renderer;
                humanoidMobRenderer.addLayer(new MilkmadeHatLayer<>(humanoidMobRenderer, event.getEntityModels()));
            }
        });
    }
}
