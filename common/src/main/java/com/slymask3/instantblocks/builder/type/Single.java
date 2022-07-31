package com.slymask3.instantblocks.builder.type;

import com.slymask3.instantblocks.Common;
import com.slymask3.instantblocks.block.InstantBlock;
import com.slymask3.instantblocks.block.entity.ColorBlockEntity;
import com.slymask3.instantblocks.builder.Builder;
import com.slymask3.instantblocks.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Single extends Base<Single> {
    private Single(Builder builder, Level world, int x, int y, int z) {
        super(builder, world, x, y, z);
    }

    public Single offset(Direction direction, int forwardBack, int leftRight) {
        int forward = Helper.isPositive(forwardBack) ? 0 : Math.abs(forwardBack);
        int back = Helper.isPositive(forwardBack) ? forwardBack : 0;
        int left = Helper.isPositive(leftRight) ? leftRight : 0;
        int right = Helper.isPositive(leftRight) ? 0 : Math.abs(leftRight);
        return offset(direction, forward, back, left, right, 0, 0);
    }

    public Single offset(Direction direction, int forward, int back, int left, int right) {
        return offset(direction, forward, back, left, right, 0, 0);
    }

    public Single offset(Direction direction, int forward, int back, int left, int right, int up, int down) {
        this.y = this.y + up - down;
        if(direction == Direction.SOUTH) {
            this.x = this.x - left + right;
            this.z = this.z - forward + back;
        } else if(direction == Direction.WEST) {
            this.x = this.x + forward - back;
            this.z = this.z - left + right;
        } else if(direction == Direction.NORTH) {
            this.x = this.x + left - right;
            this.z = this.z + forward - back;
        } else if(direction == Direction.EAST) {
            this.x = this.x - forward + back;
            this.z = this.z + left - right;
        }
        return this;
    }

    public static Single setup(Builder builder, Level world, int x, int y, int z) {
        return new Single(builder, world, x, y, z);
    }

    public static Single setup(Builder builder, Level world, BlockPos pos) {
        return new Single(builder, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public void queue(int priority, boolean replace) {
        this.setPriority(priority);
        this.replace = replace;
        this.builder.queue(this,this.replace);
    }

    public void build() {
        BlockState state = blockType.getBlockState(world, y);
        Block block = blockType.getBlock(world, y);
        Block getBlock = getBlock();
        if(Common.CONFIG.KEEP_BLOCKS() && getBlock instanceof InstantBlock) {
            return;
        }
        if(world.dimension().equals(Level.NETHER) && block.equals(Blocks.WATER) && !Common.CONFIG.ALLOW_WATER_IN_NETHER()) {
            state = Blocks.AIR.defaultBlockState(); //replace water with air in the nether
        }
        if(canSet(getBlock)) {
            if(block instanceof CrossCollisionBlock) {
                Context context = new Context(world, new BlockPos(x, y, z));
                state = block.getStateForPlacement(context);
                if(state == null) return;
            }
            if(block instanceof SlabBlock && direction == Direction.UP) {
                direction = null;
                state = state.setValue(SlabBlock.TYPE, SlabType.TOP);
            }
            if(direction != null && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, direction);
            }
            if(block == Blocks.FARMLAND) {
                state = state.setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE);
            }
            if(block instanceof BedBlock && direction != null) {
                state = state.setValue(BedBlock.PART, BedPart.HEAD);
            }
            if(block instanceof LeavesBlock) {
                state = state.setValue(LeavesBlock.PERSISTENT, Boolean.TRUE);
            }
            if(blockType.isDoubleChest()) {
                BlockPos right_pos = new BlockPos(x, y, z).relative(direction.getCounterClockWise(), 1);
                world.setBlock(right_pos, state.setValue(ChestBlock.TYPE, ChestType.LEFT), flag);
                state = state.setValue(ChestBlock.TYPE, ChestType.RIGHT);
            }
            world.setBlock(new BlockPos(x, y, z), state, flag);
            if(block instanceof DoorBlock) {
                world.setBlock(new BlockPos(x, y, z).above(), state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
            }
            if(block instanceof BedBlock && direction != null) {
                world.setBlock(new BlockPos(x, y, z).relative(direction.getOpposite(), 1), state.setValue(BedBlock.PART, BedPart.FOOT), 3);
            }
            if(blockType.isColor()) {
                try {
                    ColorBlockEntity entity = (ColorBlockEntity) world.getBlockEntity(new BlockPos(x, y, z));
                    if(entity != null) {
                        entity.color = blockType.getColor();
                    }
                } catch (Exception e) {
                    Common.LOG.info(e.getMessage());
                }
            } else if(blockType.isChest()) {
                ChestBlockEntity blockEntity = (ChestBlockEntity)world.getBlockEntity(this.getBlockPos());
                for(ItemStack itemStack : blockType.getContainerItems()) {
                    Helper.addToChest(blockEntity, itemStack);
                }
            }
        }
    }

    private boolean canSet(Block block) {
        return block.defaultDestroyTime() >= 0F || block.equals(Blocks.AIR);
    }

    public Block getBlock() {
        return world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public BlockEntity getBlockEntity() {
        return world.getBlockEntity(new BlockPos(x, y, z));
    }

    public static class Context extends BlockPlaceContext {
        public Context(Level world, BlockPos pos) {
            super(world, null, InteractionHand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(Vec3.ZERO,Direction.DOWN,pos,false));
        }
    }
}