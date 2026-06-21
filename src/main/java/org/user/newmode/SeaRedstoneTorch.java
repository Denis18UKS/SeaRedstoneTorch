package org.user.newmode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SeaRedstoneTorch.MODID)
public class SeaRedstoneTorch {

    public static final String MODID = "newmode";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredBlock<RedstoneTorchBlock> SEA_REDSTONE_TORCH =
            BLOCKS.register("sea_redstone_torch",
                    () -> new WaterSafeRedstoneTorchBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.FIRE)
                                    .noCollission()
                                    .instabreak()
                                    .lightLevel(state -> 7)
                    )
            );

    public static final DeferredItem<BlockItem> SEA_REDSTONE_TORCH_ITEM =
            ITEMS.register("sea_redstone_torch",
                    () -> new BlockItem(SEA_REDSTONE_TORCH.get(), new Item.Properties())
            );

    public SeaRedstoneTorch(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(SEA_REDSTONE_TORCH_ITEM.get());
        }
    }

    // =========================================================
    // 💧 WATERLOGGED REDSTONE TORCH
    // =========================================================
    public static class WaterSafeRedstoneTorchBlock extends RedstoneTorchBlock implements SimpleWaterloggedBlock {

        public static final BooleanProperty WATERLOGGED = net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

        public WaterSafeRedstoneTorchBlock(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(WATERLOGGED, false)
                    .setValue(LIT, true)
            );
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(WATERLOGGED);
        }

        @Override
        public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            return super.getStateForPlacement(context)
                    .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }

        @Override
        public FluidState getFluidState(BlockState state) {
            return state.getValue(WATERLOGGED)
                    ? Fluids.WATER.getSource(false)
                    : super.getFluidState(state);
        }

        @Override
        public BlockState updateShape(BlockState state,
                                      Direction direction,
                                      BlockState neighborState,
                                      LevelAccessor level,
                                      BlockPos pos,
                                      BlockPos neighborPos) {

            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }

            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }

        @Override
        public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return direction == Direction.DOWN ? 0 : super.getSignal(state, level, pos, direction);
        }
    }
}