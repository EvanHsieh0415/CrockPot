package com.sihenzhang.crockpot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sihenzhang.crockpot.recipe.WeightedItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class JsonUtils {
    @Nullable
    public static Item convertToItem(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            String s = json.getAsString();
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
            return item == Items.AIR ? null : item;
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be an item, was " + JSONUtils.getType(json));
        }
    }

    @Nullable
    public static Item getAsItem(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return convertToItem(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find an item");
        }
    }

    @Nonnull
    public static Ingredient getAsIngredient(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return Ingredient.fromJson(json.get(memberName));
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find an ingredient");
        }
    }

    @Nullable
    public static WeightedItem convertToWeightedItem(JsonElement json, String memberName) {
        if (json.isJsonObject()) {
            JsonObject o = json.getAsJsonObject();
            Item item = JsonUtils.getAsItem(o, "item");
            if (item != null) {
                int weight = JSONUtils.getAsInt(o, "weight", 1);
                if (o.has("count")) {
                    JsonElement e = o.get("count");
                    if (e.isJsonObject()) {
                        JsonObject count = e.getAsJsonObject();
                        if (count.has("min") && count.has("max")) {
                            int min = JSONUtils.getAsInt(count, "min");
                            int max = JSONUtils.getAsInt(count, "max");
                            return new WeightedItem(item, min, max, weight);
                        } else if (count.has("min")) {
                            int min = JSONUtils.getAsInt(count, "min");
                            return new WeightedItem(item, min, weight);
                        } else if (count.has("max")) {
                            int max = JSONUtils.getAsInt(count, "max");
                            return new WeightedItem(item, max, weight);
                        } else {
                            return new WeightedItem(item, weight);
                        }
                    } else {
                        int count = JSONUtils.getAsInt(o, "count", 1);
                        return new WeightedItem(item, count, weight);
                    }
                } else {
                    return new WeightedItem(item, weight);
                }
            } else {
                return null;
            }
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a weighted item, was " + JSONUtils.getType(json));
        }
    }

    @Nullable
    public static WeightedItem getAsWeightedItem(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return convertToWeightedItem(json.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a weighted item");
        }
    }
}
