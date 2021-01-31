package info.sciman.alchemicalbricks.recipe;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.mixin.RuntimeResourcePackImplAccessorMixin;
import net.devtech.arrp.json.recipe.JIngredient;
import net.devtech.arrp.json.recipe.JIngredients;
import net.devtech.arrp.json.recipe.JResult;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


/**
 * Used to be able to generate transmutation recipes at runtime
 * as well as generating all the base recipes so we don't need
 * 10 billion JSON files or something
 */
public class TransmutationGenerator {

    public static void init() {
        addTransmutation(AlchemicalBricksMod.id("test"),JTransmutation.transmutation(
                JIngredient.ingredient().item(Blocks.DIAMOND_BLOCK.asItem()),
                "minecraft:coal_block",
                5,
                TransmutationRecipe.AlchemyContext.ANY
                ));
    }


    // Add a recipe
    public static void addLinearTransmutation(Item input, Item output, int entropy, TransmutationRecipe.AlchemyContext context) {
        String inputId = Registry.ITEM.getId(input).toString();
        String outputId = Registry.ITEM.getId(output).toString();

        Identifier id = AlchemicalBricksMod.id(inputId+"_to_"+outputId);
        addTransmutation(id, JTransmutation.transmutation(
                JIngredient.ingredient().item(input),
                outputId,
                entropy,
                context
        ));
    }

    public static void addSymmetricTransmutation(Item a, Item b, int entropy, TransmutationRecipe.AlchemyContext context) {

    }


    // Adds a transmutation via JTransmutationRecipe
    public static byte[] addTransmutation(Identifier id, JTransmutation recipe) {
        Identifier id2 = RuntimeResourcePackImplAccessorMixin.invokeFix(id,"recipes","json");
        byte[] data = RuntimeResourcePackImplAccessorMixin.invokeSerialize(recipe);

        return AlchemicalBricksMod.RESOURCE_PACK.addData(id2,data);
    }
}
