package info.sciman.alchemicalbricks.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TransmutationRecipeSerializer implements RecipeSerializer<TransmutationRecipe> {

    private TransmutationRecipeSerializer() {

    }

    public static final TransmutationRecipeSerializer INSTANCE = new TransmutationRecipeSerializer();
    public static final Identifier ID = AlchemicalBricksMod.id("transmutation");

    @Override
    public TransmutationRecipe read(Identifier id, JsonObject json) {
        TransmutationRecipeJsonFormat recipeJson = new Gson().fromJson(json,TransmutationRecipeJsonFormat.class);

        if (recipeJson.input == null) {
            throw new JsonSyntaxException("Input attribute is missing!");
        }

        Ingredient input = Ingredient.fromJson(recipeJson.input);
        Item outputItem = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem)).
                orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem);

        return new TransmutationRecipe(input,output,recipeJson.entropy,recipeJson.context,id);
    }

    @Override
    public TransmutationRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        int entropy = buf.readInt();
        TransmutationRecipe.AlchemyContext context = buf.readEnumConstant(TransmutationRecipe.AlchemyContext.class);

        return new TransmutationRecipe(input,output,entropy,context,id);
    }

    @Override
    public void write(PacketByteBuf buf, TransmutationRecipe recipe) {
        recipe.getInput().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeInt(recipe.getEntropy());
        buf.writeEnumConstant(recipe.getContext());
    }

    class TransmutationRecipeJsonFormat {
        JsonObject input;
        String outputItem;
        int entropy;
        TransmutationRecipe.AlchemyContext context;
    }
}
