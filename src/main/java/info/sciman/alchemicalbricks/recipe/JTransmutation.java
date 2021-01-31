package info.sciman.alchemicalbricks.recipe;

import net.devtech.arrp.json.recipe.JIngredient;
import net.devtech.arrp.json.recipe.JResult;

public class JTransmutation implements Cloneable {

    protected final String type = "alchemicalbricks:transmutation";

    private final JIngredient input;
    private final String outputItem;
    private final int entropy;
    private final TransmutationRecipe.AlchemyContext context;

    protected String group;

    public JTransmutation(JIngredient input, String outputItem, int entropy, TransmutationRecipe.AlchemyContext context) {
        this.input = input;
        this.outputItem = outputItem;
        this.entropy = entropy;
        this.context = context;
    }

    public static JTransmutation transmutation(JIngredient input, String outputItem, int entropy, TransmutationRecipe.AlchemyContext context) {
        return new JTransmutation(input,outputItem,entropy,context);
    }

    public JTransmutation group(String group) {
        this.group = group;
        return this;
    }

    protected JTransmutation clone() {
        try {
            return (JTransmutation)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new InternalError(var2);
        }
    }
}
