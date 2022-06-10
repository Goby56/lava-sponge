package com.goby56.lavasponge.block;

import com.goby56.lavasponge.block.ModBlocks;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.LinkedList;

public class LavaSpongeBlock extends Block {
    public LavaSpongeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.update(world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.update(world, pos);
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    protected void update(World world, BlockPos pos) {
        if (this.absorbLava(world, pos)) {
            world.setBlockState(pos, ModBlocks.WET_LAVA_SPONGE_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(Blocks.LAVA.getDefaultState()));
        }
    }

    private boolean absorbLava(World world, BlockPos pos) {
        LinkedList<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Pair<BlockPos, Integer>(pos, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            Pair pair = (Pair)queue.poll();
            BlockPos blockPos = (BlockPos)pair.getLeft();
            int j = (Integer)pair.getRight();
            for (Direction direction : Direction.values()) {
                BlockPos blockPos2 = blockPos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos2);
                FluidState fluidState = world.getFluidState(blockPos2);
                Material material = blockState.getMaterial();
                if (!fluidState.isIn(FluidTags.LAVA)) continue;
                if (blockState.getBlock() instanceof FluidDrainable && !((FluidDrainable)((Object)blockState.getBlock())).tryDrainFluid(world, blockPos2, blockState).isEmpty()) {
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
                    continue;
                }
                if (blockState.getBlock() instanceof FluidBlock) {
                    world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
                    continue;
                }
                ++i;
                if (j >= 6) continue;
                queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
            }
            if (i <= 64) continue;
            break;
        }
        return i > 0;
    }
}
