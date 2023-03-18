package me.justahuman.slimefun_essentials.compat.rei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.hooks.item.ItemStackHooks;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import me.justahuman.slimefun_essentials.client.SlimefunItemStack;
import me.justahuman.slimefun_essentials.utils.Utils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.AbstractEntryRenderer;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SlimefunEntryDefinition implements EntryDefinition<SlimefunItemStack> {
    private EntryRenderer<SlimefunItemStack> renderer;
    
    public SlimefunEntryDefinition() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> Client.init(this));
    }
    
    private static class Client {
        private static void init(SlimefunEntryDefinition definition) {
            definition.renderer = definition.new SlimefunItemStackRenderer();
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
    
    private List<Text> tryGetItemStackToolTip(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value, TooltipContext context) {
        if (!SEARCH_BLACKLISTED.contains(value.itemStack().getItem()))
            try {
                return value.itemStack().getTooltip(MinecraftClient.getInstance().player, context.getFlag());
            } catch (Throwable e) {
                if (context.isSearch()) throw e;
                e.printStackTrace();
                SEARCH_BLACKLISTED.add(value.itemStack().getItem());
            }
        return Lists.newArrayList(asFormattedText(entry, value, context));
    }
    
    @Override
    public Stream<? extends TagKey<?>> getTagsFor(EntryStack<SlimefunItemStack> entry, SlimefunItemStack value) {
        return Stream.empty();
    }
    
    public class SlimefunItemStackRenderer extends AbstractEntryRenderer<SlimefunItemStack> implements BatchedEntryRenderer<SlimefunItemStack, BakedModel> {
        private static final float SCALE = 20.0F;
        public static final int ITEM_LIGHT = 0xf000f0;
    
        @Override
        public BakedModel getExtraData(EntryStack<SlimefunItemStack> entry) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(entry.getValue().itemStack(), null, null, 0);
        }
    
        @Override
        public void render(EntryStack<SlimefunItemStack> entry, MatrixStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {
            BakedModel model = getExtraData(entry);
            setupGL(entry, model);
            if (!entry.isEmpty()) {
                ItemStack value = entry.getValue().itemStack();
                matrices.push();
                matrices.multiplyPositionMatrix(RenderSystem.getModelViewMatrix());
                matrices.translate(bounds.getCenterX(), bounds.getCenterY(), entry.getZ());
                matrices.scale(bounds.getWidth(), (bounds.getWidth() + bounds.getHeight()) / -2f, 1.0F);
                MatrixStack modelViewStack = RenderSystem.getModelViewStack();
                modelViewStack.push();
                modelViewStack.peek().getPositionMatrix().set(matrices.peek().getPositionMatrix());
                RenderSystem.applyModelViewMatrix();
                VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                MinecraftClient.getInstance().getItemRenderer().renderItem(value, ModelTransformation.Mode.GUI, false, new MatrixStack(), immediate,
                        ITEM_LIGHT, OverlayTexture.DEFAULT_UV, model);
                immediate.draw();
                matrices.pop();
                modelViewStack.pop();
                RenderSystem.applyModelViewMatrix();
            }
            MatrixStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.push();
            modelViewStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            modelViewStack.translate(bounds.x, bounds.y, 0);
            modelViewStack.scale(bounds.width / 16f, (bounds.getWidth() + bounds.getHeight()) / 2f / 16f, 1.0F);
            RenderSystem.applyModelViewMatrix();
            renderOverlay(entry, bounds);
            modelViewStack.pop();
            endGL(entry, model);
            RenderSystem.applyModelViewMatrix();
        }
    
        @Override
        public int getBatchIdentifier(EntryStack<SlimefunItemStack> entry, Rectangle bounds, BakedModel model) {
            return 1738923 + (model.isSideLit() ? 1 : 0);
        }
    
        @Override
        public void startBatch(EntryStack<SlimefunItemStack> entry, BakedModel model, MatrixStack matrices, float delta) {
            setupGL(entry, model);
            MatrixStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.push();
            modelViewStack.scale(SCALE, -SCALE, 1.0F);
            RenderSystem.applyModelViewMatrix();
        }
    
        public void setupGL(EntryStack<SlimefunItemStack> entry, BakedModel model) {
            MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            boolean sideLit = model.isSideLit();
            if (!sideLit) DiffuseLighting.disableGuiDepthLighting();
        }
    
        @Override
        public void renderBase(EntryStack<SlimefunItemStack> entry, BakedModel model, MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            if (!entry.isEmpty()) {
                ItemStack value = entry.getValue().itemStack();
                matrices.push();
                matrices.translate(bounds.getCenterX() / SCALE, bounds.getCenterY() / -SCALE, entry.getZ());
                matrices.scale(bounds.getWidth() / SCALE, (bounds.getWidth() + bounds.getHeight()) / 2f / SCALE, 1.0F);
                MinecraftClient.getInstance().getItemRenderer().renderItem(value, ModelTransformation.Mode.GUI, false, matrices, immediate,
                        ITEM_LIGHT, OverlayTexture.DEFAULT_UV, model);
                matrices.pop();
            }
        }
    
        @Override
        public void afterBase(EntryStack<SlimefunItemStack> entry, BakedModel model, MatrixStack matrices, float delta) {
            endGL(entry, model);
            RenderSystem.getModelViewStack().pop();
            RenderSystem.applyModelViewMatrix();
        }
    
        @Override
        public void renderOverlay(EntryStack<SlimefunItemStack> entry, BakedModel model, MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            MatrixStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.push();
            modelViewStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            modelViewStack.translate(bounds.x, bounds.y, 0);
            modelViewStack.scale(bounds.width / 16f, (bounds.getWidth() + bounds.getHeight()) / 2f / 16f, 1.0F);
            RenderSystem.applyModelViewMatrix();
            renderOverlay(entry, bounds);
            modelViewStack.pop();
            RenderSystem.applyModelViewMatrix();
        }
    
        public void renderOverlay(EntryStack<SlimefunItemStack> entry, Rectangle bounds) {
            if (!entry.isEmpty()) {
                MinecraftClient.getInstance().getItemRenderer().zOffset = entry.getZ();
                MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, entry.getValue().itemStack(), 0, 0, null);
                MinecraftClient.getInstance().getItemRenderer().zOffset = 0.0F;
            }
        }
    
        @Override
        public void endBatch(EntryStack<SlimefunItemStack> entry, BakedModel model, MatrixStack matrices, float delta) {
        }
    
        public void endGL(EntryStack<SlimefunItemStack> entry, BakedModel model) {
            RenderSystem.enableDepthTest();
            boolean sideLit = model.isSideLit();
            if (!sideLit) DiffuseLighting.enableGuiDepthLighting();
        }
    
        @Override
        @Nullable
        public Tooltip getTooltip(EntryStack<SlimefunItemStack> entry, TooltipContext context) {
            if (entry.isEmpty())
                return null;
            Tooltip tooltip = Tooltip.create();
            Optional<TooltipData> component = entry.getValue().itemStack().getTooltipData();
            List<Text> components = tryGetItemStackToolTip(entry, entry.getValue(), context);
            if (!components.isEmpty()) {
                tooltip.add(components.get(0));
            }
            component.ifPresent(tooltip::add);
            for (int i = 1; i < components.size(); i++) {
                tooltip.add(components.get(i));
            }
            return tooltip;
        }
    }
    
}
