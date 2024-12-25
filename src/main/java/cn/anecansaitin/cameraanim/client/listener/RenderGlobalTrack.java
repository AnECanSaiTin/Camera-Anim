package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.InterpolationMath;
import cn.anecansaitin.cameraanim.client.TrackCache;
import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraTrack;
import cn.anecansaitin.cameraanim.common.animation.PointInterpolationType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

import static cn.anecansaitin.cameraanim.client.TrackCache.*;

@EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class RenderGlobalTrack {
    private static final Vector3f vCache1 = new Vector3f();
    private static final Vector3f vCache2 = new Vector3f();
    private static final Vector3f vCache3 = new Vector3f();
    private static final Vector3f vCache4 = new Vector3f();
    private static final Vector3f vCache5 = new Vector3f();
    private static final Vector3f vCache6 = new Vector3f();
    private static final Vector3f vCache7 = new Vector3f();
    private static final Vector3f CAMERA_CACHE = new Vector3f();

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES || !(TrackCache.VIEW || TrackCache.EDIT) || getTrack().getCount() < 1) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        PoseStack.Pose last = poseStack.last();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.enableDepthTest();

        // 获取相机位置
        Vec3 p = event.getCamera().getPosition();
        CAMERA_CACHE.set(p.x, p.y, p.z);
        TrackCache.SelectedPoint selected = getSelectedPoint();

        // 线条
        renderLine(selected, bufferSource, last, CAMERA_CACHE);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.DEBUG_FILLED_BOX);
        // 点
        renderPoint(selected, buffer, last, CAMERA_CACHE);
        //箭头
        renderArrowhead(selected, buffer, last, CAMERA_CACHE);
        bufferSource.endBatch(RenderType.DEBUG_FILLED_BOX);

        // 选中点的移动片
        renderSlice(selected, bufferSource, last, CAMERA_CACHE);
        RenderSystem.disableDepthTest();
    }

    private static void renderArrowhead(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last, Vector3f cameraPos) {
        int selectedIndex = selected.getPointIndex();

        if (selectedIndex >= 0) {
            GlobalCameraTrack track = getTrack();
            Vector3f pos;

            switch (selected.getControl()) {
                case LEFT -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex);
                    pos = vCache1.set(selectedPoint.getLeftBezierControl());
                }
                case RIGHT -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex - 1);
                    pos = vCache1.set(selectedPoint.getRightBezierControl());
                }
                case NONE -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex);
                    pos = vCache1.set(selectedPoint.getPosition());
                }
                case null, default -> pos = vCache1.zero();
            }

            pos.sub(cameraPos);
            addArrowhead(buffer, last, pos);
        }
    }

    // 使用vCache1
    private static void renderSlice(SelectedPoint selected, MultiBufferSource.BufferSource bufferSource, PoseStack.Pose last, Vector3f cameraPos) {
        int selectedIndex = selected.getPointIndex();

        if (selectedIndex >= 0) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.DEBUG_QUADS);
            GlobalCameraTrack track = getTrack();
            Vector3f pos;

            switch (selected.getControl()) {
                case LEFT -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex);
                    pos = vCache1.set(selectedPoint.getLeftBezierControl());
                }
                case RIGHT -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex - 1);
                    pos = vCache1.set(selectedPoint.getRightBezierControl());
                }
                case NONE -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex);
                    pos = vCache1.set(selectedPoint.getPosition());
                }
                case null, default -> pos = vCache1.zero();
            }

            addSlice(buffer, last, pos.sub(cameraPos));
            bufferSource.endBatch(RenderType.DEBUG_QUADS);
        }
    }

    // 使用vCache1
    private static void renderPoint(SelectedPoint selected, VertexConsumer buffer, PoseStack.Pose last, Vector3f cameraPos) {
        GlobalCameraTrack track = getTrack();

        for (int i = 0; i < track.getCount(); i++) {
            CameraPoint point = track.getPoint(i);
            addPoint(buffer, last, vCache1.set(point.getPosition()).sub(cameraPos), 0.1f, 0xff000000);
        }

        int selectedIndex = selected.getPointIndex();

        // 被选中点的额外点
        if (selectedIndex >= 0) {
            CameraPoint selectedPoint = track.getPoint(selectedIndex);

            // 显示贝塞尔控制点
            if (selectedIndex > 0 && selectedPoint.getType() == PointInterpolationType.BEZIER) {
                addPoint(buffer, last, vCache1.set(track.getPoint(selectedIndex - 1).getRightBezierControl()).sub(cameraPos), 0.05f, 0x7f98FB98);
                addPoint(buffer, last, vCache1.set(selectedPoint.getLeftBezierControl()).sub(cameraPos), 0.05f, 0x7f98FB98);
            }

            switch (selected.getControl()) {
                case LEFT ->
                        addPoint(buffer, last, vCache1.set(selectedPoint.getLeftBezierControl()).sub(cameraPos), 0.07f, 0x7fffffff);
                case RIGHT ->
                        addPoint(buffer, last, vCache1.set(track.getPoint(selectedIndex - 1).getRightBezierControl()).sub(cameraPos), 0.07f, 0x7fffffff);
                case NONE ->
                        addPoint(buffer, last, vCache1.set(selectedPoint.getPosition()).sub(cameraPos), 0.12f, 0xffffffff);
            }
        }
    }

    // 使用vCache1、vCache2、vCache3、vCache4
    private static void renderLine(SelectedPoint selected, MultiBufferSource.BufferSource bufferSource, PoseStack.Pose last, Vector3f cameraPos) {
        GlobalCameraTrack track = getTrack();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);
        int selectedIndex = selected.getPointIndex();

        // 选中后显示移动用箭头的线
        if (selectedIndex >= 0) {
            Vector3f pos;

            switch (selected.getControl()) {
                case LEFT -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex);
                    pos = vCache1.set(selectedPoint.getLeftBezierControl());
                }
                case RIGHT -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex - 1);
                    pos = vCache1.set(selectedPoint.getRightBezierControl());
                }
                case NONE -> {
                    CameraPoint selectedPoint = track.getPoint(selectedIndex);
                    pos = vCache1.set(selectedPoint.getPosition());
                }
                case null, default -> pos = vCache1.zero();
            }

            pos.sub(cameraPos);

            // x轴
            Vector3f axis = vCache2.set(pos).add(1, 0, 0);
            addLine(buffer, last, pos, axis, 0xffff1242);
            // y轴
            axis.add(-1, 1, 0);
            addLine(buffer, last, pos, axis, 0xff23d400);
            // z轴
            axis.add(0, -1, 1);
            addLine(buffer, last, pos, axis, 0xff0894ed);
        }

        if (track.getCount() > 2) {
            for (int i = 1, c = track.getCount(); i < c; i++) {
                CameraPoint p1 = track.getPoint(i - 1);
                CameraPoint p2 = track.getPoint(i);
                final Vector3f v1 = vCache1.set(p1.getPosition()).sub(cameraPos);
                final Vector3f v2 = vCache2.set(p2.getPosition()).sub(cameraPos);

                switch (p2.getType()) {
                    case LINEAR -> addLine(buffer, last, v1, v2, 0xffffffff);
                    case SMOOTH -> {
                        Vector3f v0;
                        Vector3f v3;

                        if (i > 1) {
                            CameraPoint p = track.getPoint(i - 2);
                            v0 = vCache3.set(p.getPosition()).sub(cameraPos);
                        } else {
                            v0 = v1;
                        }

                        if (i < c - 1) {
                            CameraPoint p = track.getPoint(i + 1);
                            v3 = vCache4.set(p.getPosition()).sub(cameraPos);
                        } else {
                            v3 = v2;
                        }

                        addSmoothLine(buffer, last, v0, v1, v2, v3, 0xffffffff);
                    }
                    case BEZIER ->
                            addBezierLine(buffer, last, v1, vCache3.set(p1.getRightBezierControl()).sub(cameraPos), vCache4.set(p2.getLeftBezierControl()).sub(cameraPos), v2, 0xffffffff);
                    case STEP -> addLine(buffer, last, v1, v2, 0xff7f7f7f);
                }
            }

            //渲染被选中点的额外线条
            if (selectedIndex > 0) {
                CameraPoint selectedPoint = track.getPoint(selectedIndex);

                if (selectedPoint.getType() == PointInterpolationType.BEZIER) {
                    Vector3f pos = vCache1.set(selectedPoint.getPosition()).sub(cameraPos);
                    Vector3f left = vCache2.set(selectedPoint.getLeftBezierControl()).sub(cameraPos);
                    addLine(buffer, last, pos, left, 0x7f98FB98);
                    CameraPoint pre = track.getPoint(selectedIndex - 1);
                    Vector3f prePos = vCache1.set(pre.getPosition()).sub(cameraPos);
                    Vector3f right = vCache2.set(pre.getRightBezierControl()).sub(cameraPos);
                    addLine(buffer, last, prePos, right, 0x7f98FB98);
                }
            }
        }

        bufferSource.endBatch(RenderType.LINES);
    }

    // 使用vCache5
    private static void addLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos1, Vector3f pos2, int color) {
        Vector3f normalize = vCache5.set(pos2).sub(pos1).normalize();
        buffer.addVertex(pose, pos1).setColor(color).setNormal(pose, normalize);
        buffer.addVertex(pose, pos2).setColor(color).setNormal(pose, normalize);
    }

    // 使用vCache6、vCache7
    private static void addSmoothLine(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pre, Vector3f p1, Vector3f p2, Vector3f after, int color) {
        Vector3f pos1 = vCache6.set(p1);
        Vector3f pos2 = vCache7.zero();

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
        Vector3f pos1 = vCache6.set(p1);
        Vector3f pos2 = vCache7.zero();

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
        Vector3f vec = vCache5;

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
    private static void addSlice(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos) {
        float spacing = 0.2f;
        float half = 0.3f + spacing;
        Vector3f vec = vCache5;
        buffer.addVertex(pose, vec.set(pos).add(0, spacing, spacing)).setColor(0x77fd3043);
        buffer.addVertex(pose, vec.set(pos).add(0, spacing, half)).setColor(0x77fd3043);
        buffer.addVertex(pose, vec.set(pos).add(0, half, half)).setColor(0x77fd3043);
        buffer.addVertex(pose, vec.set(pos).add(0, half, spacing)).setColor(0x77fd3043);

        buffer.addVertex(pose, vec.set(pos).add(half, half, 0)).setColor(0x772d5ee8);
        buffer.addVertex(pose, vec.set(pos).add(half, spacing, 0)).setColor(0x772d5ee8);
        buffer.addVertex(pose, vec.set(pos).add(spacing, spacing, 0)).setColor(0x772d5ee8);
        buffer.addVertex(pose, vec.set(pos).add(spacing, half, 0)).setColor(0x772d5ee8);

        buffer.addVertex(pose, vec.set(pos).add(spacing, 0, spacing)).setColor(0x7726ec45);
        buffer.addVertex(pose, vec.set(pos).add(spacing, 0, half)).setColor(0x7726ec45);
        buffer.addVertex(pose, vec.set(pos).add(half, 0, half)).setColor(0x7726ec45);
        buffer.addVertex(pose, vec.set(pos).add(half, 0, spacing)).setColor(0x7726ec45);
    }

    // 使用vCache5
    private static void addArrowhead(VertexConsumer buffer, PoseStack.Pose pose, Vector3f pos) {
        Vector3f vec = vCache5.set(pos);
        float size = 0.1f;
        float height = 0.35f;
        float spacing = 1;
        // y
        vec.add(0, spacing, 0);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x + size, vec.y, vec.z - size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z + size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x + size, vec.y, vec.z + size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x + size, vec.y, vec.z - size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z - size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x - size, vec.y, vec.z + size).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(0xff26ec45);
        buffer.addVertex(pose, vec.x, vec.y + height, vec.z).setColor(0xff26ec45);

        // x
        vec.add(spacing, -spacing, 0);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y + size, vec.z - size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z + size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y + size, vec.z + size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y + size, vec.z - size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z - size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x, vec.y - size, vec.z + size).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(0xffff1242);
        buffer.addVertex(pose, vec.x + height, vec.y, vec.z).setColor(0xffff1242);

        // z
        vec.add(-spacing, 0, spacing);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x - size, vec.y + size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x + size, vec.y - size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x, vec.y, vec.z + height).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x + size, vec.y + size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x - size, vec.y + size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x + size, vec.y - size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(0xff0894ed);
        buffer.addVertex(pose, vec.x - size, vec.y - size, vec.z).setColor(0xff0894ed);
    }
}