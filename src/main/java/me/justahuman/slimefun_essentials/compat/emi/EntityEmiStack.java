package me.justahuman.slimefun_essentials.compat.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class EntityEmiStack extends EmiStack {
    private final @Nullable Entity entity;
    private final EntityEntry entry;
    private final double scale;
    private final int amount;
    
    public EntityEmiStack(EntityType<?> entityType, int amount) {
        this(entityType.create(MinecraftClient.getInstance().world), amount);
    }
    
    public EntityEmiStack(@Nullable Entity entity, int amount) {
        this(entity, 8.0f, amount);
    }
    
    protected EntityEmiStack(@Nullable Entity entity, double scale, int amount) {
        this.entity = entity;
        this.entry = new EntityEntry(entity);
        this.scale = scale;
        this.amount = amount;
    }
    
    public static EntityEmiStack of(@Nullable Entity entity) {
        return new EntityEmiStack(entity, 1);
    }
    
    public static EntityEmiStack ofScaled(@Nullable Entity entity, double scale) {
        return new EntityEmiStack(entity, scale, 1);
    }
    
    @Override
    public EmiStack copy() {
        EntityEmiStack stack = new EntityEmiStack(this.entity, this.scale, this.amount);
        stack.setRemainder(getRemainder().copy());
        stack.comparison = this.comparison;
        return stack;
    }
    
    @Override
    public boolean isEmpty() {
        return this.entity == null;
    }
    
    @Override
    public void render(MatrixStack matrices, int x, int y, float delta, int flags) {
        if (this.entity != null) {
            if (this.entity instanceof LivingEntity living)
                renderEntity(x + 8, (int) (y + 8 + this.scale), this.scale, living);
            else
                renderEntity((int) (x + (2 * this.scale / 2)), (int) (y + (2 * this.scale)), this.scale,this. entity);
        }
    }
    
    @Override
    public NbtCompound getNbt() {
        throw new UnsupportedOperationException("EntityEmiStack is not intended for NBT handling");
    }
    
    @Override
    public Object getKey() {
        return this.entity;
    }
    
    @Override
    public Entry<?> getEntry() {
        return this.entry;
    }
    
    @Override
    public Identifier getId() {
        if (this.entity == null) throw new RuntimeException("Entity is null");
        return Registries.ENTITY_TYPE.getId(this.entity.getType());
    }
    
    @Override
    public List<Text> getTooltipText() {
        return List.of(getName());
    }
    
    @Override
    public List<TooltipComponent> getTooltip() {
        final List<TooltipComponent> list = new ArrayList<>();
        if (this.entity != null) {
            list.addAll(getTooltipText().stream().map(EmiPort::ordered).map(TooltipComponent::of).toList());
            final String mod;
            if (this.entity instanceof VillagerEntity villager) {
                mod = EmiUtil.getModName(Registries.VILLAGER_PROFESSION.getId(villager.getVillagerData().getProfession()).getNamespace());
            } else {
                mod = EmiUtil.getModName(Registries.ENTITY_TYPE.getId(this.entity.getType()).getNamespace());
            }
            list.add(TooltipComponent.of(EmiPort.ordered(EmiPort.literal(mod, Formatting.BLUE, Formatting.ITALIC))));
            if (!getRemainder().isEmpty()) {
                list.add(new RemainderTooltipComponent(this));
            }
        }
        return list;
    }
    
    @Override
    public Text getName() {
        return this.entity != null ? Text.literal(this.entity.getName().getString() + "x" + this.amount) : EmiPort.literal("yet another missingno");
    }
    
    public static void renderEntity(int x, int y, double size, Entity entity) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
        final Screen screen = client.currentScreen;
        final Mouse mouse = client.mouse;
        float width = screen == null ? 1920 : screen.width;
        float height = screen == null ? 1080 : screen.height;
        float mouseX = (float) ((width + 51) - mouse.getX());
        float mouseY = (float) ((height + 75 - 50) - mouse.getY());
        float f = (float) Math.atan(mouseX / 40.0F);
        float g = (float) Math.atan(mouseY / 40.0F);
    
        final MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
    
        final MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) size, (float) size, (float) size);
        
        final Quaternionf quaternion = new Quaternionf().rotateZ(3.1415927F);
        final Quaternionf quaternion2 = new Quaternionf().rotateX(g * 20.0F * 0.017453292F);
        quaternion.mul(quaternion2);
        matrixStack2.multiply(quaternion);
        
        final float yaw = entity.getYaw();
        final float pitch = entity.getPitch();
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        DiffuseLighting.method_34742();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.setYaw(yaw);
        entity.setPitch(pitch);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }
    
    public static class EntityEntry extends Entry<Entity> {
        public EntityEntry(Entity value) {
            super(value);
        }
        
        @Override
        public Class<? extends Entity> getType() {
            return getValue().getType().getBaseClass();
        }
    }
}