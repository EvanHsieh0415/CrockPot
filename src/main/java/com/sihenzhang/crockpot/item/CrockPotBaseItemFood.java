package com.sihenzhang.crockpot.item;

import com.sihenzhang.crockpot.CrockPot;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;

public class CrockPotBaseItemFood extends Item {
    public CrockPotBaseItemFood(int hunger, float saturation) {
        super(new Properties().group(CrockPot.ITEM_GROUP).food(new Food.Builder().hunger(hunger).saturation(saturation).build()));
    }

    public CrockPotBaseItemFood(int hunger, float saturation, EffectInstance effect) {
        super(new Properties().group(CrockPot.ITEM_GROUP).food(new Food.Builder().hunger(hunger).saturation(saturation).effect(effect, 1).build()));
    }
}
