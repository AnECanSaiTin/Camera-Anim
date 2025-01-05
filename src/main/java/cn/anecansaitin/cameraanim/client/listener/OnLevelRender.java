package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.InterpolationMath;
import cn.anecansaitin.cameraanim.client.Animator;
import cn.anecansaitin.cameraanim.client.PreviewAnimator;
import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.cameraanim.client.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.animation.PathInterpolator;
import cn.anecansaitin.cameraanim.common.animation.Vec3BezierController;
import cn.anecansaitin.freecameraapi.CameraModifierManager;
import cn.anecansaitin.freecameraapi.ICameraModifier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

import static cn.anecansaitin.cameraanim.client.CameraAnimIdeCache.*;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class OnLevelRender {
    private static final int
            X_COLOR = 0xffff1242,
            Y_COLOR = 0xff26ec45,
            Z_COLOR = 0xff0894ed,
            X_COLOR_TRANSPARENT = 0x7fff1242,
            Y_COLOR_TRANSPARENT = 0x7f26ec45,
            Z_COLOR_TRANSPARENT = 0x7f0894ed,
            SELECTED_COLOR = 0xff3e90ff,
            SELECTED_COLOR_TRANSPARENT = 0x7f3e90ff;
    private static final Vector3f
            V_CACHE_1 = new Vector3f(),
            V_CACHE_2 = new Vector3f(),
            V_CACHE_3 = new Vector3f(),
            V_CACHE_4 = new Vector3f(),
            V_CACHE_5 = new Vector3f(),
            V_CACHE_6 = new Vector3f(),
            V_CACHE_7 = new Vector3f(),
            CAMERA_CACHE = new Vector3f();
    private static final Quaternionf Q_CACHE = new Quaternionf();

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        setupPlayerCamera();
        renderCameraAnimIde(event);
    }

    private static void renderCameraAnimIde(RenderLevelStageEvent event) {
        if (Animator.INSTANCE.isPlaying()) {
            return;
        }

        if ((CameraAnimIdeCache.VIEW || CameraAnimIdeCache.EDIT) && !getPath().getPoints().isEmpty()) {
            if (!ClientUtil.gamePaused()) {
                setupCameraAnimIdeCamera();
            }

            if (!ClientUtil.hideGui()) {
                renderCameraAnimIdePath(event);
            }
        }
    }

    private static void renderCameraAnimIdePath(RenderLevelStageEvent event) {
        CameraAnimIdeCache.tick();
        PoseStack poseStack = event.getPoseStack();
        PoseStack.Pose last = poseStack.last();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.enableDepthTest();

        // 获取相机位置
        Vec3 p = event.getCamera().getPosition();
        CAMERA_CACHE.set(p.x, p.y, p.z);
        SelectedPoint selected = getSelectedPoint();

        // 线条
        renderLines(selected, bufferSource, last);
        // 连续三角面
        renderFilledBox(selected, bufferSource, last);
        // 面片
        renderQuads(selected, bufferSource, last);
        RenderSystem.disableDepthTest();
    }

    private static final ICameraModifier IDE_MODIFIER = CameraModifierManager
            .createModifier(CameraAnim.MODID + "_ide", false)
            .enableFov()
            .enablePos()
            .enableRotation()
            .enableGlobalMode();

    private static final Vector3f IDE_POS = new Vector3f();
    private static final Vector3f IDE_ROT = new Vector3f();
    private static final float[] IDE_FOV = new float[1];

    private static void setupCameraAnimIdeCamera() {
        PreviewAnimator animator = PreviewAnimator.INSTANCE;

        if (!PREVIEW) {
            IDE_MODIFIER.disable();
            return;
        }

        if (!animator.prepareCameraInfo(IDE_POS, IDE_ROT, IDE_FOV)) {
            return;
        }

        IDE_MODIFIER.enable()
                .setPos(IDE_POS.x, IDE_POS.y, IDE_POS.z)
                .setRotationYXZ(IDE_ROT)
                .setFov(IDE_FOV[0]);
    }

    private static final ICameraModifier PLAYER_MODIFIER = CameraModifierManager
            .createModifier(CameraAnim.MODID + "_player", true)
            .enableFov()
            .enablePos()
            .enableRotation()
            .enableGlobalMode();

    private static final Vector3f PLAYER_POS = new Vector3f();
    private static final Vector3f PLAYER_ROT = new Vector3f();
    private static final float[] PLAYER_FOV = new float[1];

    private static void setupPlayerCamera() {
        Animator animator = Animator.INSTANCE;

        if (!animator.isPlaying()) {
            PLAYER_MODIFIER.disable();
            return;
        }

        if (!animator.prepareCameraInfo(PLAYER_POS, PLAYER_ROT, PLAYER_FOV)) {
            return;
        }

        PLAYER_MODIFIER.enable()
                .setPos(PLAYER_POS.x, PLAYER_POS.y, PLAYER_POS.z)
                .setRotationYXZ(PLAYER_ROT)
                .setFov(PLAYER_FOV[0]);
    }

    private static void renderLines(SelectedPoint selected, MultiBufferSource.BufferSource bufferSource, PoseStack.Pose pose) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);
        // 轨迹
        renderTrackLine(selected, buffer, pose);
        // 贝塞尔曲线控制点连接线
        renderBezierLine(selected, buffer, pose);

        switch (getMode()) {
            case MOVE -> // 移动线
                    renderMoveLine(selected, buffer, pose);
        }

        renderCamera(pose, buffer);

        bufferSource.endBatch(RenderType.LINES);
    }

    private static void renderCamera(PoseStack.Pose pose, VertexConsumer buffer) {
        PreviewAnimator animator = PreviewAnimator.INSTANCE;
        Q_CACHE.set(0, 0, 0, 1).rotationYXZ(IDE_ROT.y, IDE_ROT.x, IDE_ROT.z);

        if (!PREVIEW) {
            if (animator.prepareCameraInfo(IDE_POS, IDE_ROT, IDE_FOV)) {
                IDE_ROT.y *= -1;
                IDE_ROT.mul(Mth.DEG_TO_RAD);

                addLine(buffer, pose, V_CACHE_1.set(-0.15f, -0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, -0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.15f, -0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(0.15f, -0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);

                addLine(buffer, pose, V_CACHE_1.set(-0.15f, 0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, 0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.15f, 0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(0.15f, 0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);

                addLine(buffer, pose, V_CACHE_1.set(0.15f, -0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, 0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.15f, -0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.15f, 0.1f, -0.1f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);

                addLine(buffer, pose, V_CACHE_1.set(0.5f, 0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.5f, 0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.5f, -0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.5f, 0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(0.5f, -0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.5f, 0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(0.5f, -0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.5f, -0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);

                addLine(buffer, pose, V_CACHE_1.set(0.5f, 0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(0.5f, -0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.5f, 0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.15f, 0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
                addLine(buffer, pose, V_CACHE_1.set(-0.5f, -0.28125f, 0.3f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), V_CACHE_2.set(-0.15f, -0.1f, 0f).rotate(Q_CACHE).add(IDE_POS).sub(CAMERA_CACHE), 0xff000000);
            }
        }
    }

    // 使用vCache1、vCache2、vCache3、vCache4
    private static void renderTrackLine(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last) {
        GlobalCameraPath track = getPath();
        ArrayList<CameraKeyframe> points = track.getPoints();

        if (points.size() < 2) {
            return;
        }

        for (int i = 1, c = points.size(); i < c; i++) {
            CameraKeyframe p1 = points.get(i - 1);
            CameraKeyframe p2 = points.get(i);
            final Vector3f v1 = V_CACHE_1.set(p1.getPos()).sub(CAMERA_CACHE);
            final Vector3f v2 = V_CACHE_2.set(p2.getPos()).sub(CAMERA_CACHE);

            switch (p2.getPathInterpolator()) {
                case LINEAR -> addLine(buffer, last, v1, v2, 0xffffffff);
                case SMOOTH -> {
                    Vector3f v0;
                    Vector3f v3;

                    if (i > 1) {
                        CameraKeyframe p = points.get(i - 2);
                        v0 = V_CACHE_3.set(p.getPos()).sub(CAMERA_CACHE);
                    } else {
                        v0 = v1;
                    }

                    if (i < c - 1) {
                        CameraKeyframe p = points.get(i + 1);
                        v3 = V_CACHE_4.set(p.getPos()).sub(CAMERA_CACHE);
                    } else {
                        v3 = v2;
                    }

                    addSmoothLine(buffer, last, v0, v1, v2, v3, 0xffffffff);
                }
                case BEZIER -> {
                    Vec3BezierController controller = p2.getPathBezier();
                    addBezierLine(buffer, last, v1, V_CACHE_3.set(controller.getLeft()).sub(CAMERA_CACHE), V_CACHE_4.set(controller.getRight()).sub(CAMERA_CACHE), v2, 0xffffffff);
                }
                case STEP -> addLine(buffer, last, v1, v2, 0xff7f7f7f);
            }
        }
    }

    // 使用vCache1、vCache2
    private static void renderMoveLine(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last) {
        int selectedTime = selected.getPointTime();

        if (selectedTime < 0) {
            return;
        }

        // 选中后显示移动用箭头的线
        Vector3f p = selected.getPosition();

        if (p == null) return;

        Vector3f pos = V_CACHE_1.set(p);

        pos.sub(CAMERA_CACHE);
        int xColor = X_COLOR,
                yColor = Y_COLOR,
                zColor = Z_COLOR;

        // 选中变色
        switch (getMoveMode().getMoveType()) {
            case X -> xColor = SELECTED_COLOR;
            case Y -> yColor = SELECTED_COLOR;
            case Z -> zColor = SELECTED_COLOR;
        }

        // x轴
        Vector3f axis = V_CACHE_2.set(pos).add(1, 0, 0);
        addLine(buffer, last, pos, axis, xColor);
        // y轴
        axis.add(-1, 1, 0);
        addLine(buffer, last, pos, axis, yColor);
        // z轴
        axis.add(0, -1, 1);
        addLine(buffer, last, pos, axis, zColor);
    }

    // 使用vCache1、vCache2
    private static void renderBezierLine(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last) {
        int selectedTime = selected.getPointTime();
        GlobalCameraPath track = getPath();

        if (selectedTime <= 0) {
            return;
        }

        //渲染被选中点的额外线条（贝塞尔控制点连接线）
        CameraKeyframe selectedPoint = track.getPoint(selectedTime);

        if (selectedPoint == null || selectedPoint.getPathInterpolator() != PathInterpolator.BEZIER) {
            return;
        }

        Vec3BezierController controller = selectedPoint.getPathBezier();
        Vector3f selectedPos = V_CACHE_1.set(selectedPoint.getPos()).sub(CAMERA_CACHE);
        Vector3f right = V_CACHE_2.set(controller.getRight()).sub(CAMERA_CACHE);
        addLine(buffer, last, selectedPos, right, 0x7f98FB98);
        CameraKeyframe pre = track.getPrePoint(selectedTime);

        if (pre == null) return;

        Vector3f prePos = V_CACHE_1.set(pre.getPos()).sub(CAMERA_CACHE);
        Vector3f left = V_CACHE_2.set(controller.getLeft()).sub(CAMERA_CACHE);
        addLine(buffer, last, prePos, left, 0x7f98FB98);
    }

    private static void renderFilledBox(SelectedPoint selected, MultiBufferSource.BufferSource bufferSource, PoseStack.Pose last) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.DEBUG_FILLED_BOX);
        // 相机点
        renderPoint(buffer, last);
        // 贝塞尔控制点
        renderBezierPoint(selected, buffer, last);
        // 选中点
        renderSelectedPoint(selected, buffer, last);

        switch (getMode()) {
            case MOVE -> // 移动箭头
                    renderArrowhead(selected, buffer, last);
        }

        bufferSource.endBatch(RenderType.DEBUG_FILLED_BOX);
    }

    // 使用vCache1
    private static void renderPoint(VertexConsumer buffer, PoseStack.Pose last) {
        GlobalCameraPath track = getPath();

        for (CameraKeyframe point : track.getPoints()) {
            addPoint(buffer, last, V_CACHE_1.set(point.getPos()).sub(CAMERA_CACHE), 0.1f, 0xff000000);
        }
    }

    // 使用vCache1
    private static void renderBezierPoint(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last) {
        int selectedTime = selected.getPointTime();

        if (selectedTime < 1) return;

        GlobalCameraPath track = getPath();
        CameraKeyframe selectedPoint = track.getPoint(selectedTime);

        if (selectedPoint == null || selectedPoint.getPathInterpolator() != PathInterpolator.BEZIER) return;

        CameraKeyframe prePoint = track.getPrePoint(selectedTime);

        if (prePoint == null) return;

        // 显示贝塞尔控制点
        Vec3BezierController controller = selectedPoint.getPathBezier();
        addPoint(buffer, last, V_CACHE_1.set(controller.getLeft()).sub(CAMERA_CACHE), 0.05f, 0x7f98FB98);
        addPoint(buffer, last, V_CACHE_1.set(controller.getRight()).sub(CAMERA_CACHE), 0.05f, 0x7f98FB98);
    }

    // 使用vCache1
    private static void renderSelectedPoint(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last) {
        Vector3f pos = selected.getPosition();

        if (pos == null) return;

        switch (selected.getControl()) {
            case LEFT, RIGHT ->
                    addPoint(buffer, last, V_CACHE_1.set(pos).sub(CAMERA_CACHE), 0.07f, SELECTED_COLOR_TRANSPARENT);
            case NONE ->
                    addPoint(buffer, last, V_CACHE_1.set(pos).sub(CAMERA_CACHE), 0.12f, SELECTED_COLOR_TRANSPARENT);
        }
    }

    // 使用vCache1
    private static void renderArrowhead(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last) {
        Vector3f pos = selected.getPosition();

        if (pos == null) return;

        pos = V_CACHE_1.set(pos).sub(CAMERA_CACHE);
        int xColor = X_COLOR,
                yColor = Y_COLOR,
                zColor = Z_COLOR;

        // 选中变色
        switch (getMoveMode().getMoveType()) {
            case X -> xColor = SELECTED_COLOR;
            case Y -> yColor = SELECTED_COLOR;
            case Z -> zColor = SELECTED_COLOR;
        }

        addArrowhead(buffer, last, pos, xColor, yColor, zColor);
    }

    private static void renderQuads(SelectedPoint selected, MultiBufferSource.BufferSource bufferSource, PoseStack.Pose last) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.DEBUG_QUADS);

        switch (getMode()) {
            case NONE -> {
            }
            case MOVE -> // 移动半透明片
                    renderMoveSlice(selected, buffer, last);
        }

        bufferSource.endBatch(RenderType.DEBUG_QUADS);
    }

    // 使用vCache1
    private static void renderMoveSlice(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last) {
        Vector3f pos = selected.getPosition();

        if (pos == null) return;

        pos = V_CACHE_1.set(pos).sub(CAMERA_CACHE);
        int xyColor = Z_COLOR_TRANSPARENT,
                yzColor = X_COLOR_TRANSPARENT,
                xzColor = Y_COLOR_TRANSPARENT;

        // 选中变色
        switch (getMoveMode().getMoveType()) {
            case XY -> xyColor = SELECTED_COLOR_TRANSPARENT;
            case YZ -> yzColor = SELECTED_COLOR_TRANSPARENT;
            case XZ -> xzColor = SELECTED_COLOR_TRANSPARENT;
        }

        addSlice(buffer, last, pos, xyColor, yzColor, xzColor);
    }

    // 使用vCache5
    private static void addLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos1, Vector3f pos2, int color) {
        Vector3f normalize = V_CACHE_5.set(pos2).sub(pos1).normalize();
        buffer.addVertex(pose, pos1).setColor(color).setNormal(pose, normalize.x, normalize.y, normalize.z);
        buffer.addVertex(pose, pos2).setColor(color).setNormal(pose, normalize.x, normalize.y, normalize.z);
    }

    // 使用vCache6、vCache7
    private static void addSmoothLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pre, Vector3f p1, Vector3f p2, Vector3f after, int color) {
        Vector3f pos1 = V_CACHE_6.set(p1);
        Vector3f pos2 = V_CACHE_7.zero();

        for (float i = 1; i <= 20; i++) {
            float f = 0.05f * i;
            InterpolationMath.catmullRom(f, pre, p1, p2, after, pos2);
            addLine(buffer, pose, pos1, pos2, color);
            pos1.set(pos2);
        }

        addLine(buffer, pose, pos2, p2, color);
    }

    // 使用vCache6、vCache7
    private static void addBezierLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f p1, Vector3f c1, Vector3f c2, Vector3f p2, int color) {
        Vector3f pos1 = V_CACHE_6.set(p1);
        Vector3f pos2 = V_CACHE_7.zero();

        for (int i = 0; i < 20; i++) {
            float t = 0.05f * i;
            InterpolationMath.bezier(t, p1, c1, c2, p2, pos2);
            addLine(buffer, pose, pos1, pos2, color);
            pos1.set(pos2);
        }

        addLine(buffer, pose, pos2, p2, color);
    }

    // 使用vCache5
    private static void addPoint(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos, float size, int color) {
        Vector3f vec = V_CACHE_5;

        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);//1
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);//1
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);//2
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);//3
        buffer.addVertex(pose, vec.set(pos).add(size, size, -size)).setColor(color);//4
        buffer.addVertex(pose, vec.set(pos).add(size, size, size)).setColor(color);//5
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);//6
        buffer.addVertex(pose, vec.set(pos).add(size, -size, size)).setColor(color);//7
        buffer.addVertex(pose, vec.set(pos).add(-size, -size, size)).setColor(color);//8
        buffer.addVertex(pose, vec.set(pos).add(size, size, size)).setColor(color);//9
        buffer.addVertex(pose, vec.set(pos).add(-size, size, size)).setColor(color);//10
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);//11
        buffer.addVertex(pose, vec.set(pos).add(-size, -size, size)).setColor(color);//12
        buffer.addVertex(pose, vec.set(pos).add(-size, -size, -size)).setColor(color);//13
        buffer.addVertex(pose, vec.set(pos).add(size, -size, -size)).setColor(color);//14
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);//15
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);//16
        buffer.addVertex(pose, vec.set(pos).add(-size, size, -size)).setColor(color);//16
    }

    // 使用vCache5
    private static void addSlice(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos, int xyColor, int yzColor, int xzColor) {
        float spacing = 0.2f;
        float half = 0.3f + spacing;
        Vector3f vec = V_CACHE_5;
        // yz
        buffer.addVertex(pose, vec.set(pos).add(0, spacing, spacing)).setColor(yzColor);
        buffer.addVertex(pose, vec.set(pos).add(0, spacing, half)).setColor(yzColor);
        buffer.addVertex(pose, vec.set(pos).add(0, half, half)).setColor(yzColor);
        buffer.addVertex(pose, vec.set(pos).add(0, half, spacing)).setColor(yzColor);

        //xy
        buffer.addVertex(pose, vec.set(pos).add(half, half, 0)).setColor(xyColor);
        buffer.addVertex(pose, vec.set(pos).add(half, spacing, 0)).setColor(xyColor);
        buffer.addVertex(pose, vec.set(pos).add(spacing, spacing, 0)).setColor(xyColor);
        buffer.addVertex(pose, vec.set(pos).add(spacing, half, 0)).setColor(xyColor);

        //xz
        buffer.addVertex(pose, vec.set(pos).add(spacing, 0, spacing)).setColor(xzColor);
        buffer.addVertex(pose, vec.set(pos).add(spacing, 0, half)).setColor(xzColor);
        buffer.addVertex(pose, vec.set(pos).add(half, 0, half)).setColor(xzColor);
        buffer.addVertex(pose, vec.set(pos).add(half, 0, spacing)).setColor(xzColor);
    }

    // 使用vCache5
    private static void addArrowhead(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos, int xColor, int yColor, int zColor) {
        Vector3f vec = V_CACHE_5.set(pos);
        float size = 0.1f;
        float height = 0.35f;
        float spacing = 1;
        // y
        vec.add(0, spacing, 0);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(yColor);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(yColor);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(yColor);
        buffer.addVertex(pose, vec.x + size, vec.y, vec.z - size).setColor(yColor);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z + size).setColor(yColor);
        buffer.addVertex(pose, vec.x + size, vec.y, vec.z + size).setColor(yColor);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(yColor);
        buffer.addVertex(pose, vec.x + size, vec.y, vec.z - size).setColor(yColor);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(yColor);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z + size).setColor(yColor);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(yColor);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(yColor);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(yColor);

        // x
        vec.add(spacing, -spacing, 0);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y + size, vec.z - size).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z + size).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y + size, vec.z + size).setColor(xColor);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y + size, vec.z - size).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(xColor);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z + size).setColor(xColor);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(xColor);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(xColor);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(xColor);

        // z
        vec.add(-spacing, 0, spacing);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(zColor);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(zColor);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(zColor);
        buffer.addVertex(pose, vec.x - size, vec.y + size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x + size, vec.y - size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(zColor);
        buffer.addVertex(pose, vec.x + size, vec.y + size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x - size, vec.y + size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x + size, vec.y - size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(zColor);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(zColor);
    }
}