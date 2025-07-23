package me.justahuman.slimefun_essentials.compat.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import me.justahuman.slimefun_essentials.mixins.minecraft.OrbAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class EntityEmiStack extends EmiStack {
    private final EntityType<?> type;
    private final @Nullable Entity entity;
    private final boolean baby;
    private final float scale;

    public EntityEmiStack(EntityType<?> type, boolean baby) {
        this.type = type;
        this.entity = type.create(MinecraftClient.getInstance().world, SpawnReason.COMMAND);
        this.baby = baby && this.entity instanceof MobEntity;

        if (this.baby) {
            this.scale = 12.0F;
            ((MobEntity) this.entity).setBaby(true);
        } else if (this.entity instanceof ExperienceOrbEntity orb) {
            this.scale = 20.0F;
            ((OrbAccessor) orb).setAmount(2477);
        } else {
            this.scale = 8.0F;
        }
    }

    @Override
    public EmiStack copy() {
        EntityEmiStack stack = new EntityEmiStack(this.type, this.baby);
        stack.setRemainder(getRemainder().copy());
        stack.comparison = this.comparison;
        return stack;
    }

    public boolean isLarge() {
        return !this.baby && !(this.entity instanceof ExperienceOrbEntity);
    }

    @Override
    public boolean isEmpty() {
        return entity == null;
    }

    @Override
    public void render(DrawContext draw, int x, int y, float delta, int flags) {
        if (entity != null) {
            Mouse mouse = MinecraftClient.getInstance().mouse;
            if (entity instanceof LivingEntity living) {
                drawLivingEntity(draw, x, this.baby ? y - 3 : y, scale, (float) mouse.getX(), (float) mouse.getY(), living);
            } else {
                drawEntity(draw, x, y, scale, (float) mouse.getX(), (float) mouse.getY(), entity);
            }

            if (this.amount > 1) {
                EmiRenderHelper.renderAmount(EmiDrawContext.wrap(draw), x, y, Text.literal(String.valueOf(this.amount)));
            }
        }
    }

    @Override
    public ComponentChanges getComponentChanges() {
        return ComponentChanges.EMPTY;
    }

    @Override
    public Object getKey() {
        return entity;
    }

    @Override
    public Identifier getId() {
        return Registries.ENTITY_TYPE.getId(this.type);
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
        return entity != null ? entity.getName() : EmiPort.literal("yet another missingno");
    }

    public static void drawLivingEntity(DrawContext ctx, int x, int y, float size, float mouseX, float mouseY, LivingEntity entity) {
        float mouseX0 = (ctx.getScaledWindowWidth() + 51) - mouseX;
        float mouseY0 = (ctx.getScaledWindowHeight() + 75 - 50) - mouseY;
        float f = (float) Math.atan(mouseX0 / 40.0F);
        float g = (float) Math.atan(mouseY0 / 40.0F);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        draw(ctx, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    public static void drawEntity(DrawContext ctx, int x, int y, float size, float mouseX, float mouseY, Entity entity) {
        float mouseX0 = (ctx.getScaledWindowWidth() + 51) - mouseX;
        float mouseY0 = (ctx.getScaledWindowHeight() + 75 - 50) - mouseY;
        float f = (float) Math.atan(mouseX0 / 40.0F);
        float g = (float) Math.atan(mouseY0 / 40.0F);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float i = entity.getYaw();
        float j = entity.getPitch();
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        draw(ctx, x, y, size, quaternionf, quaternionf2, entity);
        entity.setYaw(i);
        entity.setPitch(j);
    }

    @SuppressWarnings("deprecation")
    private static void draw(DrawContext ctx, int x, int y, float size, Quaternionf quaternion, @Nullable Quaternionf quaternion2, Entity entity) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x + 8, y + 16, 50.0);
        ctx.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling(size, size, -size));
        ctx.getMatrices().multiply(quaternion);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternion2 != null) {
            quaternion2.conjugate();
            dispatcher.setRotation(quaternion2);
        }

        dispatcher.setRenderShadows(false);
        ctx.draw(vertexConsumers -> dispatcher.render(entity, 0.0, 0.0, 0.0, 1.0F, ctx.getMatrices(), vertexConsumers, 15728880));
        dispatcher.setRenderShadows(true);
        ctx.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }
}