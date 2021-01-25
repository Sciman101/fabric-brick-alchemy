package info.sciman.alchemicalbricks;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;

public class AlchemyRecipes {

    public static final HashMap<Item,Item> RECIPES = new HashMap<>();

    public static void init() {
        addRecipe(Blocks.COBBLESTONE.asItem(),Blocks.STONE.asItem(),true);
        addRecipe(Blocks.DIAMOND_BLOCK.asItem(), Items.EMERALD,false);
    }

    public static Item getConversion(Item input) {
        if (RECIPES.containsKey(input)) {
            return RECIPES.get(input);
        }else{
            return null;
        }
    }

    private static void addRecipe(Item a, Item b,boolean symmetrical) {
        RECIPES.put(a,b);
        if (symmetrical) {
            RECIPES.put(b,a);
        }
    }

}
