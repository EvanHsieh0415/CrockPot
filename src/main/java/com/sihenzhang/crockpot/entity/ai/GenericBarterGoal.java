/*
  Copyright (c) 2020, Elucent
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
      * Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
      * Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
      * Neither the name of the copyright holder nor the names of its
        contributors may be used to endorse or promote products derived from
        this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.sihenzhang.crockpot.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenericBarterGoal<E extends CreatureEntity> extends Goal {
    static Random rand = new Random();
    Predicate<ItemStack> valid;
    Function<ItemStack, ItemStack> result;
    int progress = 0, cooldown = 0, lastTick = 0;
    E entity;
    ItemStack backupHack = ItemStack.EMPTY;

    public GenericBarterGoal(E entity, Predicate<ItemStack> valid, Function<ItemStack, ItemStack> result) {
        this.entity = entity;
        this.valid = valid;
        this.result = result;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean isPreemptible() {
        return false;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            return;
        }
        if (progress > 0 && !backupHack.isEmpty()) {
            entity.setHeldItem(Hand.MAIN_HAND, backupHack);
        }

        entity.setAttackTarget(null);
        if (progress > 0) {
            progress--;
            entity.getNavigator().clearPath();
            if (progress == 0) {
                if (!entity.world.isRemote) {
                    entity.world.addEntity(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + 0.1, entity.getPosZ(), result.apply(entity.getHeldItemMainhand().copy())));
                }
                entity.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                cooldown = 600;
            }
        } else {
            List<ItemEntity> items = entity.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(entity.getPosition().add(-8, -8, -8), entity.getPosition().add(8, 8, 8)), (item) -> valid.test(item.getItem()));
            ItemEntity nearest = items.stream().min(Comparator.comparingDouble(a -> a.getDistanceSq(entity))).get();
            if (nearest.getDistanceSq(entity) < 2.25) {
                progress = 100;
                entity.setHeldItem(Hand.MAIN_HAND, nearest.getItem());
                nearest.remove();
                entity.world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.HOSTILE, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            entity.getNavigator().tryMoveToXYZ(nearest.getPosX(), nearest.getPosY(), nearest.getPosZ(), 1.0f);
        }

        if (!entity.getHeldItemMainhand().isEmpty()) {
            backupHack = entity.getHeldItemMainhand();
        }
    }

    @Override
    public boolean shouldExecute() {
        if (--cooldown > 0) {
            return false;
        }
        if (progress > 0 || entity.ticksExisted < lastTick + 20) {
            return false;
        }
        lastTick = entity.ticksExisted;
        List<ItemEntity> items = entity.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(entity.getPosition().add(-8, -8, -8), entity.getPosition().add(8, 8, 8)), (item) -> valid.test(item.getItem()));
        return items.size() > 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (progress > 0) {
            return true;
        } else { // walking towards item
            List<ItemEntity> items = entity.world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(entity.getPosition().add(-8, -8, -8), entity.getPosition().add(8, 8, 8)), (item) -> valid.test(item.getItem()));
            return items.size() > 0;
        }
    }
}
