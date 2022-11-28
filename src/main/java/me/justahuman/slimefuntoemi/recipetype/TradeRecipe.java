package me.justahuman.slimefuntoemi.recipetype;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.justahuman.slimefuntoemi.EntityEmiStack;
import me.justahuman.slimefuntoemi.SlimefunToEMI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradeRecipe implements EmiRecipe {

    private final EmiRecipeCategory emiRecipeCategory;
    private final Identifier id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final EmiStack inputStack;
    private final Text name;

    public TradeRecipe(EmiRecipeCategory emiRecipeCategory, Identifier id, List<EmiIngredient> inputs, List<EmiStack> outputs) {
        this.emiRecipeCategory = emiRecipeCategory;
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        EntityType<?> type = EntityType.PIGLIN;
        MinecraftClient client = MinecraftClient.getInstance();
        Entity entity = type.create(client.world);
        if (entity != null) {
            Box box = entity.getBoundingBox();
            double len = box.getAverageSideLength();
            if (len > 1.05) {
                len = (len + Math.sqrt(len)) / 2.0;
            }
            name = entity.getName();
            double scale = 1.05 / len * 8.0;
            inputStack = EntityEmiStack.ofScaled(entity, scale);
        } else {
            inputStack = EmiStack.EMPTY;
            name = Text.translatable("sftoemi.missing_entity");
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.emiRecipeCategory;
    }

    @Override
    public @Nullable Identifier getId() {

        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {

        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {

        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {

        return 128;
    }

    @Override
    public int getDisplayHeight() {

        return 35;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int offsetX = getDisplayWidth() / 2 - (SlimefunToEMI.slotWidth + SlimefunToEMI.bigSlotWidth + SlimefunToEMI.arrowWidth + SlimefunToEMI.slotWidth + 12) /2;
        final int offsetYS = (getDisplayHeight() - SlimefunToEMI.slotHeight) / 2;
        final int offsetYO = (getDisplayHeight() - SlimefunToEMI.bigSlotHeight) / 2;
        final int offsetYA = (getDisplayHeight() - SlimefunToEMI.arrowHeight) / 2;

        widgets.addSlot(EmiStack.of(Items.GOLD_INGOT), offsetX, offsetYS);
        widgets.addSlot(inputStack, offsetX + SlimefunToEMI.slotWidth + 4, offsetYO).output(true);
        widgets.addFillingArrow(offsetX + SlimefunToEMI.slotWidth + SlimefunToEMI.bigSlotWidth + 8, offsetYA, 2000);
        widgets.addSlot(outputs.get(0), offsetX + SlimefunToEMI.slotWidth + SlimefunToEMI.bigSlotWidth + SlimefunToEMI.arrowWidth + 12, offsetYS);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
