package info.sciman.alchemicalbricks.recipe;

import com.google.gson.annotations.SerializedName;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TransmutationRecipe implements Recipe<SimpleInventory> {

    // Where can this transmutation actually be performed?
    public enum AlchemyContext {
        @SerializedName("any") ANY, // This works anywhere
        @SerializedName("brick") BRICK, // This works when throwing a brick
        @SerializedName("altar") ALTAR // This works at an altar
    }

    private final Ingredient input;
    private final ItemStack output;
    private final int entropy; // How much entropy does this generate?
    private final AlchemyContext context; // where does this recipe work?
    private final Identifier id;

    // Default constructor
    public TransmutationRecipe(Ingredient input, ItemStack output, int entropy, AlchemyContext context, Identifier id) {
        this.input = input;
        this.output = output;
        this.entropy = entropy;
        this.context = context;
        this.id = id;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getEntropy() {
        return entropy;
    }

    public AlchemyContext getContext() {
        return context;
    }

    @Override
    public boolean matches(SimpleInventory inv, World world) {
        if (inv.size() < 1) return false;
        return this.input.test(inv.getStack(0));
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TransmutationRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    // Type class
    public static class Type implements RecipeType<TransmutationRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "transmutation";
    }


    // Unneeded
    @Override
    public ItemStack craft(SimpleInventory inv) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean fits(int width, int height) {
        return false;
    }
}
