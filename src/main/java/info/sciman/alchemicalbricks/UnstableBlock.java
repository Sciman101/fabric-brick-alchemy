package info.sciman.alchemicalbricks;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class UnstableBlock extends Block {

    private static final Identifier TAG_PROTECTED = AlchemicalBricksMod.id("unstable_protected");
    private final Tag<Block> PROTECTED_BLOCKS;

    public static final IntProperty AGE = IntProperty.of("age",0,15);


    public UnstableBlock(Settings settings) {
        super(settings);
        // Get protected blocks
        PROTECTED_BLOCKS = TagRegistry.block(TAG_PROTECTED);
        // Setup property
        this.setDefaultState(this.getStateManager().getDefaultState().with(AGE,0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);

        BlockPos newPos = findValidAdjacentBlock((World) world,pos);
        // Move
        if (!newPos.equals(pos)) {
            world.setBlockState(newPos,this.getDefaultState());
        }
    }

    // Spread on random tick
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        BlockPos newPos = findValidAdjacentBlock((World) world,pos);
        // Clone and age
        int newAge = state.get(AGE)+1;
        boolean success = !newPos.equals(pos) && (newAge < 16);

        // Update block in world
        world.setBlockState(newPos,success ? state.with(AGE, newAge) :  Blocks.AIR.getDefaultState());
    }

    private BlockPos findValidAdjacentBlock(World world, BlockPos original) {

        ArrayList<BlockPos> validPos = new ArrayList<>();

        // Loop over adjacent blocks
        for (int x=-1;x<2;x++) {
            for (int y=-1;y<2;y++) {
                for (int z=-1;z<2;z++) {
                    // Don't check the center
                    if (!(x == 0 && z == 0 && y == 0)) {
                        // Check for valid block
                        BlockPos p = original.add(x,y,z);
                        BlockState s = world.getBlockState(p);
                        if (!s.isAir() && !PROTECTED_BLOCKS.contains(s.getBlock())) {
                            validPos.add(p);
                        }
                    }
                }
            }
        }
        if (validPos.isEmpty()) {
            return original;
        }else{
            // Return random position
            return validPos.get(world.random.nextInt(validPos.size()));
        }
    }
}
