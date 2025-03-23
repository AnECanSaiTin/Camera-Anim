package cn.anecansaitin.cameraanim.client.gui.screen;

import cn.anecansaitin.cameraanim.client.ide.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.enums.ControlType;
import cn.anecansaitin.cameraanim.client.gui.widget.NumberEditBox;
import cn.anecansaitin.cameraanim.client.ide.SelectedPoint;
import cn.anecansaitin.cameraanim.client.util.ClientUtil;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.animation.interpolation.types.PathInterpolator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.joml.Vector3f;

public class PointSettingScreen extends Screen {
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

    private static final int CONTENT_WIDTH = 300;
    private static final int CONTENT_HEIGHT = 160;
    private static final int FIELD_WIDTH = 50;
    private static final int FIELD_HEIGHT = 10;
    private static final int BUTTON_WIDTH = 90;
    private static final int SMALL_BUTTON_WIDTH = 50;
    private static final int SPACING = 5;

    private final NumberEditBox[] positionFields = new NumberEditBox[3];
    private final NumberEditBox[] rotationFields = new NumberEditBox[3];
    private NumberEditBox zoomField;
    private NumberEditBox durationField;
    private CycleButton<PathInterpolator> typeButton;
    private StringWidget infoWidget;

    private int baseX;
    private int baseY;

    public PointSettingScreen() {
        super(Component.literal("Point Setting"));
    }

    @Override
    protected void init() {
        this.baseX = (this.width - CONTENT_WIDTH) / 2;
        this.baseY = (this.height - CONTENT_HEIGHT) / 2;

        SelectedPoint selectedPoint = CameraAnimIdeCache.getSelectedPoint();
        GlobalCameraPath track = CameraAnimIdeCache.getPath();
        int time = selectedPoint.getPointTime();
        Vector3f pos = selectedPoint.getPosition();
        if (pos == null) return;

        initPositionFields(pos);
        if (selectedPoint.getControl() == ControlType.NONE) {
            CameraKeyframe point = track.getPoint(time);
            assert point != null;
            initRotationFields(point.getRot());
            initZoomField(point.getFov());
            initTypeButton(point.getPathInterpolator());
            initDurationField(time);
        }
        initInfoAndTip();
        initSaveButton(selectedPoint, track, pos, time);
    }

    private void initPositionFields(Vector3f pos) {
        addRenderableOnly(new StringWidget(baseX + 1, baseY + 2, 30, FIELD_HEIGHT, POS, font));
        positionFields[0] = new NumberEditBox(font, baseX + 37, baseY + 2, FIELD_WIDTH, FIELD_HEIGHT, pos.x, Component.literal("x"));
        positionFields[1] = new NumberEditBox(font, baseX + 92, baseY + 2, FIELD_WIDTH, FIELD_HEIGHT, pos.y, Component.literal("y"));
        positionFields[2] = new NumberEditBox(font, baseX + 147, baseY + 2, FIELD_WIDTH, FIELD_HEIGHT, pos.z, Component.literal("z"));
        addRenderableWidget(positionFields[0]);
        addRenderableWidget(positionFields[1]);
        addRenderableWidget(positionFields[2]);
        addRenderableWidget(new ExtendedButton(baseX + 202, baseY + 2, BUTTON_WIDTH, FIELD_HEIGHT, INTERPOLATION, b -> {
            ClientUtil.pushGuiLayer(new InterpolationSettingScreen(1));
        }));
    }

    private void initRotationFields(Vector3f rot) {
        addRenderableOnly(new StringWidget(baseX + 1, baseY + 2 + FIELD_HEIGHT + SPACING, 30, FIELD_HEIGHT, ROT, font));
        rotationFields[0] = new NumberEditBox(font, baseX + 37, baseY + 2 + FIELD_HEIGHT + SPACING, FIELD_WIDTH, FIELD_HEIGHT, rot.x, Component.literal("xRot"));
        rotationFields[1] = new NumberEditBox(font, baseX + 92, baseY + 2 + FIELD_HEIGHT + SPACING, FIELD_WIDTH, FIELD_HEIGHT, rot.y, Component.literal("yRot"));
        rotationFields[2] = new NumberEditBox(font, baseX + 147, baseY + 2 + FIELD_HEIGHT + SPACING, FIELD_WIDTH, FIELD_HEIGHT, rot.z, Component.literal("zRot"));
        addRenderableWidget(rotationFields[0]);
        addRenderableWidget(rotationFields[1]);
        addRenderableWidget(rotationFields[2]);
        addRenderableWidget(new ExtendedButton(baseX + 202, baseY + 2 + FIELD_HEIGHT + SPACING, BUTTON_WIDTH, FIELD_HEIGHT, INTERPOLATION, b -> ClientUtil.pushGuiLayer(new InterpolationSettingScreen(2))));
    }

    private void initZoomField(float fov) {
        addRenderableOnly(new StringWidget(baseX + 1, baseY + 2 + 2 * (FIELD_HEIGHT + SPACING), 30, FIELD_HEIGHT, ZOOM, font));
        zoomField = new NumberEditBox(font, baseX + 37, baseY + 2 + 2 * (FIELD_HEIGHT + SPACING), FIELD_WIDTH, FIELD_HEIGHT, fov, Component.literal("zoom"));
        addRenderableWidget(zoomField);
        addRenderableWidget(new ExtendedButton(baseX + 202, baseY + 2 + 2 * (FIELD_HEIGHT + SPACING), BUTTON_WIDTH, FIELD_HEIGHT, INTERPOLATION, b -> ClientUtil.pushGuiLayer(new InterpolationSettingScreen(3))));
    }

    private void initTypeButton(PathInterpolator initialType) {
        addRenderableOnly(new StringWidget(baseX + 1, baseY + 2 + 3 * (FIELD_HEIGHT + SPACING), 30, FIELD_HEIGHT, TYPE, font));
        typeButton = CycleButton.builder(PathInterpolator::getDisplayName)
                .withValues(PathInterpolator.values())
                .withInitialValue(initialType)
                .create(baseX + 37, baseY + 2 + 3 * (FIELD_HEIGHT + SPACING), 65, FIELD_HEIGHT + 1, TYPE, (b, t) -> {
                });
        addRenderableWidget(typeButton);
    }

    private void initDurationField(int time) {
        addRenderableOnly(new StringWidget(baseX + 1, baseY + 2 + 4 * (FIELD_HEIGHT + SPACING), 30, FIELD_HEIGHT, TIME, font));
        durationField = new NumberEditBox(font, baseX + 37, baseY + 2 + 4 * (FIELD_HEIGHT + SPACING), FIELD_WIDTH, FIELD_HEIGHT, time, Component.literal("time"));
        addRenderableWidget(durationField);
    }

    private void initInfoAndTip() {
        infoWidget = new StringWidget(baseX + 1, baseY + 2 + 5 * (FIELD_HEIGHT + SPACING), 100, FIELD_HEIGHT, Component.literal(""), font);
        addRenderableOnly(infoWidget);
        addRenderableOnly(new StringWidget(baseX, baseY + 2 + 6 * (FIELD_HEIGHT + SPACING), CONTENT_WIDTH, FIELD_HEIGHT, TIP, font));
    }

    private void initSaveButton(SelectedPoint selectedPoint, GlobalCameraPath track, Vector3f pos, int time) {
        addRenderableWidget(new ExtendedButton(baseX + 202, baseY + 3 * (FIELD_HEIGHT + SPACING), SMALL_BUTTON_WIDTH, FIELD_HEIGHT + 4, SAVE, b -> {
            float xn = positionFields[0].getFloatValue(pos.x);
            float yn = positionFields[1].getFloatValue(pos.y);
            float zn = positionFields[2].getFloatValue(pos.z);

            if (!isValidNumber(positionFields[0].getValue()) || !isValidNumber(positionFields[1].getValue()) || !isValidNumber(positionFields[2].getValue())) {
                infoWidget.setMessage(POS_ERROR);
                return;
            }

            switch (selectedPoint.getControl()) {
                case LEFT, RIGHT -> {
                    pos.set(xn, yn, zn);
                    onClose();
                }
                case NONE -> {
                    CameraKeyframe point = track.getPoint(time);
                    assert point != null;
                    pos.set(xn, yn, zn);

                    if (!isValidNumber(rotationFields[0].getValue()) || !isValidNumber(rotationFields[1].getValue()) || !isValidNumber(rotationFields[2].getValue())) {
                        infoWidget.setMessage(ROT_ERROR);
                        return;
                    }

                    point.getRot().set(rotationFields[0].getFloatValue(0), rotationFields[1].getFloatValue(0), rotationFields[2].getFloatValue(0));

                    if (!isValidNumber(zoomField.getValue())){
                        infoWidget.setMessage(ZOOM_ERROR);
                        return;
                    }

                    point.setFov(zoomField.getFloatValue(70));

                    if (point.getPathInterpolator() != typeButton.getValue()) {
                        point.setPathInterpolator(typeButton.getValue());
                        track.updateBezier(time);
                    }

                    if (!isValidNumber(durationField.getValue())){
                        infoWidget.setMessage(TIME_ERROR);
                        return;
                    }

                    int newTime = (int) durationField.getFloatValue(70);
                    track.setTime(time, newTime);
                    onClose();
                }
            }
        }));
    }

    private boolean isValidNumber(String text) {
        return !text.trim().isEmpty() && text.matches("^-?\\d*[.,]?\\d+$");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.hLine(baseX, baseX + CONTENT_WIDTH, baseY, 0xFF95e1d3);
        guiGraphics.hLine(baseX, baseX + CONTENT_WIDTH, baseY + CONTENT_HEIGHT, 0xFF95e1d3);
        guiGraphics.vLine(baseX, baseY, baseY + CONTENT_HEIGHT, 0xFF95e1d3);
        guiGraphics.vLine(baseX + CONTENT_WIDTH, baseY, baseY + CONTENT_HEIGHT, 0xFF95e1d3);
    }
}
