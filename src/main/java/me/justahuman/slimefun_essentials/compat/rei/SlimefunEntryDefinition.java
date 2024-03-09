package me.justahuman.slimefun_essentials.compat.rei;

import dev.architectury.hooks.item.ItemStackHooks;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.BatchedEntryRenderer;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.entry.ItemEntryDefinition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class SlimefunEntryDefinition implements EntryDefinition<SlimefunItemStack> {
    private EntryRenderer<SlimefunItemStack> renderer;
    
    public SlimefunEntryDefinition() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> Client.init(this));
    }
    
    private static class Client {
        private static void init(SlimefunEntryDefinition definition) {
            definition.renderer = new SlimefunItemStackRenderer();
        }
    }
    
    @Override
    public Class<SlimefunItemStack> getValueType() {
        return SlimefunItemStack.class;
    }
    
    @Override
    public EntryType<SlimefunItemStack> getType() {
        return ReiIntegration.SLIMEFUN;
    }
    
    @Override
    public EntryRenderer<SlimefunItemStack> getRenderer() {
        return renderer;
    }
    
    @Override
    @Nullable
    public Identifier getIdentifier(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return Utils.newIdentifier(value.id());
    }
    
    @Override
    public boolean isEmpty(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return value.itemStack().getCount() < 1;
    }
    
    @Override
    public SlimefunItemStack copy(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return value.copy();
    }
    
    @Override
    public SlimefunItemStack normalize(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return value.copy().setAmount(1);
    }
    
    @Override
    public SlimefunItemStack wildcard(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return normalize(entry, value);
    }
    
    @Override
    @Nullable
    public ItemStack cheatsAs(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return value.itemStack().copy();
    }
    
    @Nullable
    @Override
    public SlimefunItemStack add(SlimefunItemStack o1, SlimefunItemStack o2) {
        return new SlimefunItemStack(o1.id(), ItemStackHooks.copyWithCount(o1.itemStack(), o1.itemStack().getCount() + o2.itemStack().getCount()));
    }
    
    @Override
    public long hash(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value, ComparisonContext context) {
        int code = 1;
        code = 31 * code + System.identityHashCode(value.itemStack().getItem());
        code = 31 * code + Long.hashCode(ItemComparatorRegistry.getInstance().hashOf(context, value.itemStack()));
        return code;
    }
    
    @Override
    public boolean equals(SlimefunItemStack o1, SlimefunItemStack o2, ComparisonContext context) {
        if (o1.itemStack().getItem() != o2.itemStack().getItem()) {
            return false;
        }
        
        final boolean initial = ItemComparatorRegistry.getInstance().hashOf(context, o1.itemStack()) == ItemComparatorRegistry.getInstance().hashOf(context, o2.itemStack());
        return context.isExact() ? initial : initial && Utils.equalSlimefunIds(o1.itemStack(), o2.itemStack());
    }
    
    @Override
    @Nullable
    public EntrySerializer<SlimefunItemStack> getSerializer() {
        return null;
    }
    
    private static final ReferenceSet<Item> SEARCH_BLACKLISTED = new ReferenceOpenHashSet<>();
    
    @Override
    public Text asFormattedText(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        TooltipContext context = TooltipContext.of();
        if (!SEARCH_BLACKLISTED.contains(value.itemStack().getItem())) {
            try {
                return value.itemStack().getName();
            } catch (Throwable e) {
                if (context != null && context.isSearch()) throw e;
                e.printStackTrace();
                SEARCH_BLACKLISTED.add(value.itemStack().getItem());
            }
        }
        try {
            return Text.literal(I18n.translate("item." + Registries.ITEM.getKey(value.itemStack().getItem()).toString().replace(":", ".")));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return Text.literal("ERROR");
    }

    @Override
    public Stream<? extends TagKey<?>> getTagsFor(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return Stream.empty();
    }

    public static class SlimefunItemStackRenderer implements BatchedEntryRenderer<SlimefunItemStack, BakedModel> {
        private final ItemEntryDefinition.ItemEntryRenderer itemStackEntryRenderer;

        public SlimefunItemStackRenderer() {
            this.itemStackEntryRenderer = (ItemEntryDefinition.ItemEntryRenderer) EntryStacks.of(ItemStack.EMPTY).getRenderer();
        }

        @Override
        public BakedModel getExtraData(EntryStack<SlimefunItemStack> entry) {
            return itemStackEntryRenderer.getExtraData(ReiIntegration.unwrap(entry));
        }

        @Override
        public void render(EntryStack<SlimefunItemStack> entry, DrawContext graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
            itemStackEntryRenderer.render(ReiIntegration.unwrap(entry), graphics, bounds, mouseX, mouseY, delta);
        }

        @Override
        public int getBatchIdentifier(EntryStack<SlimefunItemStack> entry, Rectangle bounds, BakedModel model) {
            return itemStackEntryRenderer.getBatchIdentifier(ReiIntegration.unwrap(entry), bounds, model);
        }

        @Override
        public void startBatch(EntryStack<SlimefunItemStack> entry, BakedModel model, DrawContext graphics, float delta) {
            itemStackEntryRenderer.startBatch(ReiIntegration.unwrap(entry), model, graphics, delta);
        }

        @Override
        public void renderBase(EntryStack<SlimefunItemStack> entry, BakedModel model, DrawContext graphics, VertexConsumerProvider.Immediate immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            itemStackEntryRenderer.renderBase(ReiIntegration.unwrap(entry), model, graphics, immediate, bounds, mouseX, mouseY, delta);
        }

        @Override
        public void afterBase(EntryStack<SlimefunItemStack> entry, BakedModel model, DrawContext graphics, float delta) {
            itemStackEntryRenderer.afterBase(ReiIntegration.unwrap(entry), model, graphics, delta);
        }

        @Override
        public void renderOverlay(EntryStack<SlimefunItemStack> entry, BakedModel model, DrawContext graphics, VertexConsumerProvider.Immediate immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            itemStackEntryRenderer.renderOverlay(ReiIntegration.unwrap(entry), model, graphics, immediate, bounds, mouseX, mouseY, delta);
        }

        @Override
        public void endBatch(EntryStack<SlimefunItemStack> entry, BakedModel model, DrawContext graphics, float delta) {}

        @Override
        @Nullable
        public Tooltip getTooltip(EntryStack<SlimefunItemStack> entry, TooltipContext context) {
            return itemStackEntryRenderer.getTooltip(ReiIntegration.unwrap(entry), context);
        }
    }
}
