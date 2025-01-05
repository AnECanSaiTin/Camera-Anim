package cn.anecansaitin.cameraanim.client.gui.screen;

import cn.anecansaitin.cameraanim.InterpolationMath;
import cn.anecansaitin.cameraanim.client.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.TimeBezierController;
import cn.anecansaitin.cameraanim.common.animation.TimeInterpolator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector2f;

public class InterpolationSettingScreen extends Screen {
    private static final Component INTERPOLATION = Component.translatable("gui.camera_anim.interpolation_setting.interpolation");
    private static final Component TYPE = Component.translatable("gui.camera_anim.interpolation_setting.type");
    private static final Component EASY = Component.translatable("gui.camera_anim.interpolation_setting.easy");
    private static final Component EASY_IN = Component.translatable("gui.camera_anim.interpolation_setting.easy_in");
    private static final Component EASY_OUT = Component.translatable("gui.camera_anim.interpolation_setting.easy_out");
    private static final Component EASY_IN_OUT = Component.translatable("gui.camera_anim.interpolation_setting.easy_in_out");
    private static final Component SAVE = Component.translatable("gui.camera_anim.interpolation_setting.save");
    private static final Component TIME = Component.translatable("gui.camera_anim.interpolation_setting.time");
    private static final Component DISTANCE = Component.translatable("gui.camera_anim.interpolation_setting.distance");
    // 1 pos, 2 rot, 3 fov
    private final int valueType;
    private Vector2f zero = new Vector2f(21, 161);
    private Vector2f one = new Vector2f(101, 81);
    private Vector2f left = new Vector2f();
    private Vector2f right = new Vector2f();
    private final Vector2f vCache1 = new Vector2f();
    private final Vector2f vCache2 = new Vector2f();
    private final Vector2f vCache3 = new Vector2f();
    private final Vector2f vCache4 = new Vector2f();
    private final Vector2f vCache5 = new Vector2f();
    private CycleButton<TimeInterpolator> typeSwitch;

    public InterpolationSettingScreen(int valueType) {
        super(INTERPOLATION);
        this.valueType = valueType;
    }

    @Override
    protected void init() {
        CameraAnimIdeCache.SelectedPoint selectedPoint = CameraAnimIdeCache.getSelectedPoint();
        CameraKeyframe point = CameraAnimIdeCache.getPath().getPoint(selectedPoint.getPointTime());

        if (point == null) {
            onClose();
            return;
        }

        int x = 20;
        int y = 20;

        typeSwitch = CycleButton
                .builder(TimeInterpolator::getDisplayName)
                .withValues(TimeInterpolator.values())
                .withInitialValue(TimeInterpolator.LINEAR)
                .create(x + 200, y + 60, 70, 20, TYPE, (b, t) -> {
                });
        addRenderableWidget(typeSwitch);
        TimeBezierController bezier = switch (valueType) {
            case 1 -> {
                typeSwitch.setValue(point.getPosTimeInterpolator());
                yield point.getPosBezier();
            }
            case 2 -> {
                typeSwitch.setValue(point.getRotTimeInterpolator());
                yield point.getRotBezier();
            }
            case 3 -> {
                typeSwitch.setValue(point.getFovTimeInterpolator());
                yield point.getFovBezier();
            }
            default -> null;
        };

        if (bezier == null) {
            onClose();
            return;
        }

        left.set(bezier.getLeft());
        right.set(bezier.getRight());

        Bezier bezier1 = new Bezier(left, zero.x, zero.y);
        Bezier bezier2 = new Bezier(right, zero.x, zero.y);
        addRenderableWidget(bezier1);
        addRenderableWidget(bezier2);
        addRenderableWidget(new ExtendedButton(x + 100, y + 60, 80, 20, EASY_IN, b -> {
            left.set(0.42f, 0);
            right.set(1, 1);
            bezier1.update();
            bezier2.update();
        }));
        addRenderableWidget(new ExtendedButton(x + 100, y + 80, 80, 20, EASY_OUT, b -> {
            left.set(0, 0);
            right.set(0.58f, 1);
            bezier1.update();
            bezier2.update();
        }));
        addRenderableWidget(new ExtendedButton(x + 100, y + 100, 80, 20, EASY_IN_OUT, b -> {
            left.set(0.42f, 0);
            right.set(0.58f, 1);
            bezier1.update();
            bezier2.update();
        }));
        addRenderableWidget(new ExtendedButton(x + 100, y + 120, 80, 20, EASY, b -> {
            left.set(0.25f, 0.1f);
            right.set(0.25f, 1);
            bezier1.update();
            bezier2.update();
        }));
        addRenderableWidget(new ExtendedButton(x + 100, y + 160, 50, 20, SAVE, b -> {
            TimeBezierController controller;

            switch (valueType) {
                case 1 -> {
                    point.setPosTimeInterpolator(typeSwitch.getValue());
                    controller = point.getPosBezier();
                }
                case 2 -> {
                    point.setRotTimeInterpolator(typeSwitch.getValue());
                    controller = point.getRotBezier();
                }
                case 3 -> {
                    point.setFovTimeInterpolator(typeSwitch.getValue());
                    controller = point.getFovBezier();
                }
                default -> {
                    return;
                }
            }

            controller.setLeft(left.x, left.y);
            controller.setRight(right.x, right.y);
            onClose();
        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = 20;
        int y = 20;
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.vLine(x, y + 60, y + 60 + 80, 0xffff6300);
        guiGraphics.vLine(x + 80, y + 60, y + 60 + 80, 0xffff6300);
        guiGraphics.hLine(x, x + 80, y + 60, 0xffff6300);
        guiGraphics.hLine(x, x + 80, y + 60 + 80, 0xffff6300);
        guiGraphics.drawString(font, TIME, x, y + 145, 0xffffffff);
        guiGraphics.drawString(font, "—————————>", x, y + 139, 0xffffffff);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.mulPose(new Quaternionf().rotateZ(-90 * Mth.DEG_TO_RAD));
        font.drawInBatch(DISTANCE, -161, 8, 0xffffffff, false, pose.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        font.drawInBatch("—————————>", -161, 15, 0xffffffff, false, pose.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        pose.popPose();

        MultiBufferSource.BufferSource bufferSource = guiGraphics.bufferSource();
        PoseStack.Pose last = guiGraphics.pose().last();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);
        if (typeSwitch.getValue() == TimeInterpolator.BEZIER) {
            Vector2f pre = vCache2.set(zero),
                    next;
            vCache4.set(left).mul(80, -80f).add(zero);
            vCache5.set(right).mul(80, -80f).add(zero);

            for (int i = 0; i < 20; i++) {
                float t = i * 0.05f;
                next = InterpolationMath.bezier(t, zero, vCache4, vCache5, one, vCache3);
                addLine(buffer, last, pre, next, 0xFFFFFFFF);
                pre.set(next);
            }

            addLine(buffer, last, pre, one, 0xFFFFFFFF);
        } else {
            addLine(buffer, last, zero, one, 0xFFFFFFFF);
        }
        guiGraphics.flush();
    }

    private void addLine(VertexConsumer buffer, PoseStack.Pose pose, Vector2f pos1, Vector2f pos2, int color) {
        Vector2f normalize = vCache1.set(pos2).sub(pos1).normalize();
        buffer.addVertex(pose, pos1.x, pos1.y, 0).setColor(color).setNormal(pose, normalize.x, normalize.y, 0);
        buffer.addVertex(pose, pos2.x, pos2.y, 0).setColor(color).setNormal(pose, normalize.x, normalize.y, 0);
    }

    private static final class Bezier extends AbstractWidget {
        private final Vector2f point;
        private final float zeroX, zeroY;

        private Bezier(Vector2f point, float x, float y) {
            super((int) (point.x * 80 + x - 2), (int) (point.y * -80 + y - 2), 5, 5, Component.literal("贝塞尔点"));
            this.point = point;
            this.zeroX = x;
            this.zeroY = y;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            MultiBufferSource.BufferSource bufferSource = guiGraphics.bufferSource();
            PoseStack.Pose last = guiGraphics.pose().last();
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.GUI);
            buffer.addVertex(last, point.x * 80 + zeroX + 2, point.y * -80 + zeroY + 2, 0).setColor(0xFFc4c4c4);
            buffer.addVertex(last, point.x * 80 + zeroX + 2, point.y * -80 + zeroY - 2, 0).setColor(0xFFc4c4c4);
            buffer.addVertex(last, point.x * 80 + zeroX - 2, point.y * -80 + zeroY - 2, 0).setColor(0xFFc4c4c4);
            buffer.addVertex(last, point.x * 80 + zeroX - 2, point.y * -80 + zeroY + 2, 0).setColor(0xFFc4c4c4);
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            point.add((float) dragX / 100, (float) -dragY / 100);
            point.x = Math.clamp(0, 1, point.x);
            update();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }

        public void update() {
            setX((int) (point.x * 80 + zeroX - 2));
            setY((int) (point.y * -80 + zeroY - 2));
        }
    }
}
