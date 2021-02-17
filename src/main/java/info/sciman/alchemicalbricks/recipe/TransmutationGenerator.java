package info.sciman.alchemicalbricks.recipe;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.mixin.RuntimeResourcePackImplAccessorMixin;
import net.devtech.arrp.json.recipe.JIngredient;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.stream.Stream;


/**
 * Used to be able to generate transmutation recipes at runtime
 * as well as generating all the base recipes so we don't need
 * 10 billion JSON files or something
 */
public class TransmutationGenerator {

    private static int generatedTransmutationCount = 0;

    public static void addBaseTransmutation() {
        // Add recipes

        // Core recipes
        addLinearTransmutation(Blocks.ENCHANTING_TABLE,AlchemicalBricksMod.ALCHEMIC_ALTAR,0, TransmutationRecipe.AlchemyContext.BRICK);
        addLinearTransmutation(AlchemicalBricksMod.UNSTABLE_BLOCK.asItem(),AlchemicalBricksMod.UNSTABLE_MATTER,100, TransmutationRecipe.AlchemyContext.ANY);

        // Basic recipes
        addSymmetricalTransmutation(Blocks.SAND,Blocks.RED_SAND,1, TransmutationRecipe.AlchemyContext.ANY);
        addSymmetricalTransmutation(Blocks.COBBLESTONE,Blocks.STONE,1, TransmutationRecipe.AlchemyContext.ANY);

        // Ore sequence
        addLinearTransmutation(Blocks.COAL_BLOCK,Blocks.IRON_ORE,5, TransmutationRecipe.AlchemyContext.ANY);
        addLinearTransmutation(Blocks.IRON_BLOCK,Blocks.GOLD_ORE,8, TransmutationRecipe.AlchemyContext.ANY);
        addLinearTransmutation(Blocks.GOLD_BLOCK,Blocks.EMERALD_ORE,20, TransmutationRecipe.AlchemyContext.ANY);
        addLinearTransmutation(Blocks.EMERALD_BLOCK,Blocks.DIAMOND_ORE,40, TransmutationRecipe.AlchemyContext.ANY);
        addLinearTransmutation(Blocks.DIAMOND_BLOCK,Blocks.COAL_BLOCK,0, TransmutationRecipe.AlchemyContext.ANY);

        // Wood transformations
        addCyclicalTransmutation(1, TransmutationRecipe.AlchemyContext.ANY,
                Blocks.OAK_LOG,Blocks.SPRUCE_LOG,Blocks.BIRCH_LOG,Blocks.JUNGLE_LOG,Blocks.ACACIA_LOG,Blocks.DARK_OAK_LOG);
        addCyclicalTransmutation(1, TransmutationRecipe.AlchemyContext.ANY,
                Blocks.OAK_PLANKS,Blocks.SPRUCE_PLANKS,Blocks.BIRCH_PLANKS,Blocks.JUNGLE_PLANKS,Blocks.ACACIA_PLANKS,Blocks.DARK_OAK_PLANKS);
        addCyclicalTransmutation(1, TransmutationRecipe.AlchemyContext.ANY,
                Blocks.STRIPPED_OAK_LOG,Blocks.STRIPPED_SPRUCE_LOG,Blocks.STRIPPED_BIRCH_LOG,Blocks.STRIPPED_JUNGLE_LOG,Blocks.STRIPPED_ACACIA_LOG,Blocks.STRIPPED_DARK_OAK_LOG);
        addCyclicalTransmutation(1, TransmutationRecipe.AlchemyContext.ANY,
                Blocks.OAK_WOOD,Blocks.SPRUCE_WOOD,Blocks.BIRCH_WOOD,Blocks.JUNGLE_WOOD,Blocks.ACACIA_WOOD,Blocks.DARK_OAK_WOOD);
        addCyclicalTransmutation(1, TransmutationRecipe.AlchemyContext.ANY,
                Blocks.STRIPPED_OAK_WOOD,Blocks.STRIPPED_SPRUCE_WOOD,Blocks.STRIPPED_BIRCH_WOOD,Blocks.STRIPPED_JUNGLE_WOOD,Blocks.STRIPPED_ACACIA_WOOD,Blocks.STRIPPED_DARK_OAK_WOOD);

        // Misc
        addLinearTransmutation(Blocks.GRASS_BLOCK,Blocks.MYCELIUM,5, TransmutationRecipe.AlchemyContext.ANY);
        addSymmetricalTransmutation(Items.REDSTONE,Items.GLOWSTONE,5, TransmutationRecipe.AlchemyContext.ALTAR);
    }


    // Add a recipe (item -> item)
    public static void addLinearTransmutation(Item input, Item output, int entropy, TransmutationRecipe.AlchemyContext context) {
        // Create a (hopefully) unique id for this recipe
        Identifier inputId = Registry.ITEM.getId(input);
        Identifier outputId = Registry.ITEM.getId(output);
        String path = String.format("generated%d_%s_to_%s", generatedTransmutationCount, inputId.getPath(),outputId.getPath());
        generatedTransmutationCount++;
        Identifier id = AlchemicalBricksMod.id(path);

        // Add it
        addTransmutation(id, JTransmutation.transmutation(
                JIngredient.ingredient().item(input),
                outputId.toString(),
                entropy,
                context
        ));
    }

    // Add a symmetrical recipe
    public static void addSymmetricalTransmutation(Item input, Item output, int entropy, TransmutationRecipe.AlchemyContext context) {
        addLinearTransmutation(input,output,entropy,context);
        addLinearTransmutation(output,input,entropy,context);
    }

    // Add a cyclical recipe
    public static void addCyclicalTransmutation(int entropy, TransmutationRecipe.AlchemyContext context, Item... items) {
        for (int i=0;i<items.length;i++) {
            Item a = items[i];
            Item b = i < items.length-1 ? items[i+1] : items[0];

            addLinearTransmutation(a,b,entropy,context);
        }
    }

    // Block alternatives
    public static void addLinearTransmutation(Block input, Block output, int entropy, TransmutationRecipe.AlchemyContext context) {
        addLinearTransmutation(input.asItem(),output.asItem(),entropy,context);
    }
    public static void addSymmetricalTransmutation(Block input, Block output, int entropy, TransmutationRecipe.AlchemyContext context) {
        addSymmetricalTransmutation(input.asItem(),output.asItem(),entropy,context);
    }
    public static void addCyclicalTransmutation(int entropy, TransmutationRecipe.AlchemyContext context, Block... blocks) {
        Item[] items = Stream.of(blocks).map(b -> b.asItem()).toArray(Item[]::new);
        addCyclicalTransmutation(entropy,context,items);
    }

    // Adds a transmutation via JTransmutationRecipe
    public static byte[] addTransmutation(Identifier id, JTransmutation recipe) {
        Identifier id2 = RuntimeResourcePackImplAccessorMixin.invokeFix(id,"recipes","json");
        byte[] data = RuntimeResourcePackImplAccessorMixin.invokeSerialize(recipe);

        return AlchemicalBricksMod.RESOURCE_PACK.addData(id2,data);
    }
}
