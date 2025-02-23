package com.sihenzhang.crockpot.integration.jei;

import com.sihenzhang.crockpot.CrockPotRegistry;
import com.sihenzhang.crockpot.block.AbstractCrockPotBlock;
import com.sihenzhang.crockpot.client.gui.screen.CrockPotScreen;
import com.sihenzhang.crockpot.recipe.FoodValuesDefinition;
import com.sihenzhang.crockpot.util.RLUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@JeiPlugin
public class ModIntegrationJei implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return RLUtils.createRL("crock_pot");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CrockPotCookingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new FoodValuesCategory(guiHelper));
        registration.addRecipeCategories(new ExplosionCraftingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new PiglinBarteringRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(CrockPotCookingRecipeCategory.CROCK_POT_COOKING_RECIPE_TYPE, recipeManager.getAllRecipesFor(CrockPotRegistry.crockPotCookingRecipeType.get()).stream().filter(r -> r.getResult().getItem() != CrockPotRegistry.avaj.get()).toList());
        registration.addRecipes(FoodValuesCategory.FOOD_VALUES_RECIPE_TYPE, FoodValuesDefinition.getFoodCategoryMatchedItemsList(recipeManager));
        registration.addRecipes(ExplosionCraftingRecipeCategory.EXPLOSION_CRAFTING_RECIPE_TYPE, recipeManager.getAllRecipesFor(CrockPotRegistry.explosionCraftingRecipeType.get()));
        registration.addRecipes(PiglinBarteringRecipeCategory.PIGLIN_BARTERING_RECIPE_TYPE, recipeManager.getAllRecipesFor(CrockPotRegistry.piglinBarteringRecipeType.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ForgeRegistries.BLOCKS.tags().getTag(AbstractCrockPotBlock.CROCK_POT_BLOCK_TAG).stream()
                .filter(block -> block instanceof AbstractCrockPotBlock).map(AbstractCrockPotBlock.class::cast)
                .map(block -> block.asItem().getDefaultInstance())
                .forEach(pot -> registration.addRecipeCatalyst(pot, CrockPotCookingRecipeCategory.CROCK_POT_COOKING_RECIPE_TYPE));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrockPotScreen.class, 80, 43, 24, 18, CrockPotCookingRecipeCategory.CROCK_POT_COOKING_RECIPE_TYPE);
    }
}
