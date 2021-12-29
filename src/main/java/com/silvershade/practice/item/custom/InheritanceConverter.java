package com.silvershade.practice.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class InheritanceConverter extends Item {
    private static final Map<Block, Item> INHERITANCE_CONVERTER_ITEM_CONVERSION =
            new ImmutableMap.Builder<Block, Item>()
                    .put(Blocks.SAND, Blocks.GLASS.asItem())
                    .put(Blocks.CLAY, Items.CLAY_BALL)
                    .build();

    public InheritanceConverter(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn (UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide()) {
            Level level = pContext.getLevel();
            BlockPos positionClicked = pContext.getClickedPos();
            Block blockClicked = level.getBlockState(positionClicked).getBlock();

            if (canBlowTorch(blockClicked)) {
                int dropAmount=1;
                if (blockClicked==Blocks.CLAY)
                    dropAmount=4;
                ItemEntity entityItem = new ItemEntity(level,
                        positionClicked.getX(), positionClicked.getY(), positionClicked.getZ(),
                        new ItemStack(INHERITANCE_CONVERTER_ITEM_CONVERSION.get(blockClicked), dropAmount));

                level.destroyBlock(positionClicked, false);
                level.addFreshEntity(entityItem);
                pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), p -> {
                    p.broadcastBreakEvent(pContext.getHand());
                });
            } else {
                pContext.getPlayer().sendMessage(new TextComponent("Cannot interact with this block"),
                        Util.NIL_UUID);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private boolean canBlowTorch(Block block) {
        return INHERITANCE_CONVERTER_ITEM_CONVERSION.containsKey(block);
    }
}
