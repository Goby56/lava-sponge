package com.goby56.lavasponge.block;

import com.goby56.lavasponge.world.ModWorldEvents;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.LinkedList;

public class WetLavaSpongeBlock extends Block {

    public WetLavaSpongeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.update(world, pos);
        if (this.absorbWater(world, pos)) {
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
            world.syncWorldEvent(ModWorldEvents.WET_LAVA_SPONGE_EVAPORATES_WATER, pos, 0);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, (1.0f + world.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction direction = Direction.random(random);
        if (direction == Direction.UP) {
            return;
        }
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (state.isOpaque() && blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
            return;
        }
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        if (direction == Direction.DOWN) {
            e -= 0.05;
            d += random.nextDouble();
            f += random.nextDouble();
        } else {
            e += random.nextDouble() * 0.8;
            if (direction.getAxis() == Direction.Axis.X) {
                f += random.nextDouble();
                d = direction == Direction.EAST ? (d += 1.1) : (d += 0.05);
            } else {
                d += random.nextDouble();
                f = direction == Direction.SOUTH ? (f += 1.1) : (f += 0.05);
            }
        }
        world.addParticle(ParticleTypes.DRIPPING_LAVA, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.update(world, pos);
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    protected void update(World world, BlockPos pos) {
        if (world.getBlockState(pos.down()).isOf(Blocks.BLUE_ICE)) {
            world.setBlockState(pos, ModBlocks.DRY_LAVA_SPONGE_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
            world.syncWorldEvent(ModWorldEvents.WET_LAVA_SPONGE_COOLS_DOWN, pos, 0);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, (1.0f + world.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
    }

    private boolean absorbWater(World world, BlockPos pos) {
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
                if (!fluidState.isIn(FluidTags.WATER)) continue;
                if (blockState.getBlock() instanceof FluidDrainable && !((FluidDrainable)((Object)blockState.getBlock())).tryDrainFluid(world, blockPos2, blockState).isEmpty()) {
                    ++i;
                    if (j >= 2) continue;
                    queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
                    continue;
                }
                if (blockState.getBlock() instanceof FluidBlock) {
                    world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    ++i;
                    if (j >= 2) continue;
                    queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
                    continue;
                }
                if (material != Material.UNDERWATER_PLANT && material != Material.REPLACEABLE_UNDERWATER_PLANT) continue;
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
                SpongeBlock.dropStacks(blockState, world, blockPos2, blockEntity);
                world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                ++i;
                if (j >= 2) continue;
                queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
            }
            if (i <= 64) continue;
            break;
        }
        return i > 0;
    }
}
