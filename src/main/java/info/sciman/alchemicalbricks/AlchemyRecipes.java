package info.sciman.alchemicalbricks;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;

// TODO make this not soley code-based
// Ideally, some sort of JSON or otherwise custom data format
// Something like
// minecraft:stone <> minecraft:cobblestone 20
// minecraft:diamond_block -> minecraft:emerald 5
// Where format is
// <input> -> (one way)/<> (two ways) <output> <entropy>
public class AlchemyRecipes {

    public static final HashMap<Item,Item> RECIPES = new HashMap<>();

    public static void init() {
        System.out.println("Registering transmutation recipes...");
        addRecipe(Blocks.COBBLESTONE.asItem(),Blocks.STONE.asItem(),true);
        addRecipe(Blocks.DIAMOND_BLOCK.asItem(), Items.EMERALD,true);

        // Log cycle
        addRecipe(Blocks.OAK_LOG,Blocks.SPRUCE_LOG);
        addRecipe(Blocks.SPRUCE_LOG,Blocks.BIRCH_LOG);
        addRecipe(Blocks.BIRCH_LOG,Blocks.JUNGLE_LOG);
        addRecipe(Blocks.JUNGLE_LOG,Blocks.ACACIA_LOG);
        addRecipe(Blocks.ACACIA_LOG,Blocks.DARK_OAK_LOG);
        addRecipe(Blocks.DARK_OAK_LOG,Blocks.OAK_LOG);

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
