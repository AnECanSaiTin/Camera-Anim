package cn.anecansaitin.cameraanim.client.gui.screen;

import cn.anecansaitin.cameraanim.client.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.gui.widget.NumberEditBox;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.animation.PathInterpolator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.joml.Vector3f;

public class PointSettingScreen extends Screen {
    private final NumberEditBox[] numbers = new NumberEditBox[5];
    private CycleButton<PathInterpolator> type;
    private static final Component POS = Component.translatable("gui.camera_anim.point_setting.pos");
    private static final Component ROT = Component.translatable("gui.camera_anim.point_setting.rot");
    private static final Component ZOOM = Component.translatable("gui.camera_anim.point_setting.zoom");
    private static final Component TIME = Component.translatable("gui.camera_anim.point_setting.time");
    private static final Component SAVE = Component.translatable("gui.camera_anim.point_setting.save");
    private static final Component TYPE = Component.translatable("gui.camera_anim.point_setting.type");
    private static final Component POS_ERROR = Component.translatable("gui.camera_anim.point_setting.pos_error");
    private static final Component ROT_ERROR = Component.translatable("gui.camera_anim.point_setting.rot_error");
    private static final Component ZOOM_ERROR = Component.translatable("gui.camera_anim.point_setting.zoom_error");
    private static final Component TIME_ERROR = Component.translatable("gui.camera_anim.point_setting.time_error");
    private static final Component TIP = Component.translatable("gui.camera_anim.point_setting.tip");
    private static final Component INTERPOLATION = Component.translatable("gui.camera_anim.point_setting.interpolation");

    public PointSettingScreen() {
        super(Component.literal("Point Setting"));
    }

    @Override
    protected void init() {
        CameraAnimIdeCache.SelectedPoint selectedPoint = CameraAnimIdeCache.getSelectedPoint();
        GlobalCameraPath track = CameraAnimIdeCache.getPath();
        int time = selectedPoint.getPointTime();
        Vector3f pos = selectedPoint.getPosition();
        if (pos == null) return;

        int x = 20;
        int y = 20;

        addRenderableOnly(new StringWidget(x + 1, y + 2, 30, 10, POS, font));
        NumberEditBox[] xyz = new NumberEditBox[3];
        xyz[0] = new NumberEditBox(font, x + 37, y + 2, 50, 10, pos.x, Component.literal("x"));
        xyz[1] = new NumberEditBox(font, x + 92, y + 2, 50, 10, pos.y, Component.literal("y"));
        xyz[2] = new NumberEditBox(font, x + 147, y + 2, 50, 10, pos.z, Component.literal("z"));
        addRenderableWidget(xyz[0]);
        addRenderableWidget(xyz[1]);
        addRenderableWidget(xyz[2]);

        if (selectedPoint.getControl() == CameraAnimIdeCache.ControlType.NONE) {
            addRenderableWidget(new ExtendedButton(x + 202 , y + 2, 90, 10, INTERPOLATION, b -> Minecraft.getInstance().pushGuiLayer(new InterpolationSettingScreen(1))));
            CameraKeyframe point = track.getPoint(time);
            assert point != null;// pos不为null，则point不为null
            float fov = point.getFov();
            Vector3f rot = point.getRot();
            addRenderableOnly(new StringWidget(x + 1, y + 2 + 10, 30, 10, ROT, font));
            numbers[0] = new NumberEditBox(font, x + 37, y + 2 + 10, 50, 10, rot.x, Component.literal("xRot"));
            addRenderableWidget(numbers[0]);
            numbers[1] = new NumberEditBox(font, x + 92, y + 2 + 10, 50, 10, rot.y, Component.literal("yRot"));
            addRenderableWidget(numbers[1]);
            numbers[2] = new NumberEditBox(font, x + 147, y + 2 + 10, 50, 10, rot.z, Component.literal("zRot"));
            addRenderableWidget(numbers[2]);
            addRenderableWidget(new ExtendedButton(x + 202 , y + 2 + 10, 90, 10, INTERPOLATION, b -> Minecraft.getInstance().pushGuiLayer(new InterpolationSettingScreen(2))));
            addRenderableOnly(new StringWidget(x + 1, y + 2 + 10 + 10, 30, 10, ZOOM, font));
            numbers[3] = new NumberEditBox(font, x + 37, y + 2 + 10 + 10, 50, 10, fov, Component.literal("zoom"));
            addRenderableWidget(numbers[3]);
            addRenderableWidget(new ExtendedButton(x + 92 , y + 2 + 10 + 10, 90, 10, INTERPOLATION, b -> Minecraft.getInstance().pushGuiLayer(new InterpolationSettingScreen(3))));
            type = CycleButton
                    .builder(PathInterpolator::getDisplayName)
                    .withValues(PathInterpolator.values())
                    .withInitialValue(point.getPathInterpolator())
                    .create(x + 37, y + 2 + 10 + 10 + 10, 65, 11, TYPE, (b, t) -> {
                    });
            addRenderableWidget(type);
            addRenderableOnly(new StringWidget(x + 1, y + 2 + 10 + 10 + 10 + 11, 30, 10, TIME, font));
            numbers[4] = new NumberEditBox(font, x + 37, y + 2 + 10 + 10 + 10 + 11, 50, 10, time, Component.literal("time"));
            addRenderableWidget(numbers[4]);
        }

        StringWidget info = new StringWidget(x + 1, y + 2 + 10 + 10 + 40, 100, 10, Component.literal(""), font);
        addRenderableOnly(info);
        addRenderableOnly(new StringWidget(x, y + 2 + 10 + 10 + 60, 300, 10, TIP, font));

        Button button = Button
                .builder(SAVE, (b) -> {
                    float xn, yn, zn;

                    try {
                        xn = Float.parseFloat(xyz[0].getValue());
                        yn = Float.parseFloat(xyz[1].getValue());
                        zn = Float.parseFloat(xyz[2].getValue());
                    } catch (NumberFormatException e) {
                        info.setMessage(POS_ERROR);
                        return;
                    }

                    switch (selectedPoint.getControl()) {
                        case LEFT, RIGHT -> {
                            pos.set(xn, yn, zn);
                            onClose();
                        }
                        case NONE -> {
                            CameraKeyframe point = track.getPoint(time);
                            assert point != null;// pos不为null，则point不为null
                            pos.set(xn, yn, zn);

                            try {
                                float xRot = Float.parseFloat(numbers[0].getValue());
                                float yRot = Float.parseFloat(numbers[1].getValue());
                                float zRot = Float.parseFloat(numbers[2].getValue());
                                point.getRot().set(xRot, yRot, zRot);
                            } catch (NumberFormatException e) {
                                info.setMessage(ROT_ERROR);
                                return;
                            }

                            try {
                                point.setFov(Float.parseFloat(numbers[3].getValue()));
                            } catch (NumberFormatException e) {
                                info.setMessage(ZOOM_ERROR);
                                return;
                            }

                            if (point.getPathInterpolator() != type.getValue()) {
                                point.setPathInterpolator(type.getValue());
                                track.updateBezier(time);
                            }

                            try {
                                int newTime = Integer.parseInt(numbers[4].getValue());
                                track.setTime(time, newTime);
                                onClose();
                            } catch (NumberFormatException e) {
                                info.setMessage(TIME_ERROR);
                            }
                        }
                    }
                })
                .pos(x + 202, y + 30)
                .width(50)
                .build();
        addRenderableWidget(button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int x = 20;
        int y = 20;
        guiGraphics.hLine(x, x + 300, y, 0xFF95e1d3);
        guiGraphics.hLine(x, x + 300, y + 54, 0xFF95e1d3);
        guiGraphics.vLine(x + 300, y, y + 54, 0xFF95e1d3);
        guiGraphics.vLine(x, y, y + 54, 0xFF95e1d3);
    }
}
