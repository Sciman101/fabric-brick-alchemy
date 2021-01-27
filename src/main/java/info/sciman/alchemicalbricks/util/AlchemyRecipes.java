package info.sciman.alchemicalbricks.util;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;

import java.util.HashMap;

// TODO make this not soley code-based
// Ideally, some sort of JSON or otherwise custom data format
// Something like
// minecraft:stone <> minecraft:cobblestone 20
// minecraft:diamond_block -> minecraft:emerald 5
// Where format is
// <input> -> (one way)/<> (two ways) <output> <entropy>
public class AlchemyRecipes {

    public enum AlchemyContext {
        THROWN_BRICK, // this recipe only works by throwing a brick by hand
        ALTAR, // this recipe only works at an altar
        ANY // This recipe works anywhere
    }

    public static final HashMap<Item,Item> RECIPES = new HashMap<>();

    public static void init() {
        System.out.println("Registering transmutation recipes...");

        // This is how you get unstable blocks
        addRecipe(Blocks.CRYING_OBSIDIAN.asItem(), AlchemicalBricksMod.UNSTABLE_BLOCK.asItem(),false);
        // And this is how you get unstable bricks
        addRecipe(AlchemicalBricksMod.UNSTABLE_BLOCK.asItem(), AlchemicalBricksMod.UNSTABLE_MATTER,false);
        // And this is how you get the workbench
        addRecipe(Blocks.ENDER_CHEST, AlchemicalBricksMod.ALCHEMICAL_WORKBENCH);

        // Normal recipes
        addRecipe(Blocks.COBBLESTONE.asItem(),Blocks.STONE.asItem(),true);
        addRecipe(Blocks.SAND.asItem(),Blocks.RED_SAND.asItem(),true);

        // Stone cycle
        addRecipe(Blocks.DIORITE,Blocks.ANDESITE);
        addRecipe(Blocks.ANDESITE,Blocks.GRANITE);
        addRecipe(Blocks.GRANITE,Blocks.DIORITE);

        // Log cycle
        addRecipe(Blocks.OAK_LOG,Blocks.SPRUCE_LOG);
        addRecipe(Blocks.SPRUCE_LOG,Blocks.BIRCH_LOG);
        addRecipe(Blocks.BIRCH_LOG,Blocks.JUNGLE_LOG);
        addRecipe(Blocks.JUNGLE_LOG,Blocks.ACACIA_LOG);
        addRecipe(Blocks.ACACIA_LOG,Blocks.DARK_OAK_LOG);
        addRecipe(Blocks.DARK_OAK_LOG,Blocks.OAK_LOG);

        // Plank cycle
        addRecipe(Blocks.OAK_PLANKS,Blocks.SPRUCE_PLANKS);
        addRecipe(Blocks.SPRUCE_PLANKS,Blocks.BIRCH_PLANKS);
        addRecipe(Blocks.BIRCH_PLANKS,Blocks.JUNGLE_PLANKS);
        addRecipe(Blocks.JUNGLE_PLANKS,Blocks.ACACIA_PLANKS);
        addRecipe(Blocks.ACACIA_PLANKS,Blocks.DARK_OAK_PLANKS);
        addRecipe(Blocks.DARK_OAK_PLANKS,Blocks.OAK_PLANKS);

    }

    // Get the resultant item from a conversion
    public static Item getConversion(Item input) {
        if (RECIPES.containsKey(input)) {
            return RECIPES.get(input);
        }else{
            return null;
        }
    }

    // Add a new recipe to the list
    private static void addRecipe(Item a, Item b,boolean symmetrical) {
        RECIPES.put(a,b);
        if (symmetrical) {
            RECIPES.put(b,a);
        }
    }
    private static void addRecipe(Item a, Item b) {
        addRecipe(a,b,false);
    }
    private static void addRecipe(Block a, Block b) {
        addRecipe(a.asItem(),b.asItem(),false);
    }

}
