package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.common.animation.*;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.Map;

import static cn.anecansaitin.cameraanim.client.ClientUtil.partialTicks;
import static cn.anecansaitin.cameraanim.InterpolationMath.*;

public class PreviewAnimator {
    public static final PreviewAnimator INSTANCE = new PreviewAnimator();
    private boolean playing;
    private int time;

    public void tick() {
        if (!playing) {
            return;
        }

        if (time >= CameraAnimIdeCache.getPath().getLength()) {
            reset();
        } else {
            time++;
        }
    }

    public void play() {
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public void reset() {
        time = 0;
        playing = false;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void back() {
        if (time <= 0) {
            return;
        }

        time = Math.max(0, time - 5);
    }

    public void forward() {
        if (time >= CameraAnimIdeCache.getPath().getLength()) {
            return;
        }

        time = Math.min(CameraAnimIdeCache.getPath().getLength(), time + 5);
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean prepareCameraInfo(Vector3f posDest, Vector3f rotDest, float[] fov) {
        float partialTicks = isPlaying() ? partialTicks() : 0;
        GlobalCameraPath track = CameraAnimIdeCache.getPath();
        Map.Entry<Integer, CameraKeyframe> current = track.getEntry(time);

        Map.Entry<Integer, CameraKeyframe> preEntry = current == null ? track.getPreEntry(time) : current;
        Map.Entry<Integer, CameraKeyframe> nextEntry = track.getNextEntry(time);
        float t;

        if (preEntry == null) {
            if (nextEntry == null) return false;
            posDest.set(nextEntry.getValue().getPos());
            rotDest.set(nextEntry.getValue().getRot());
            return true;
        } else {
            if (nextEntry == null) {
                posDest.set(preEntry.getValue().getPos());
                rotDest.set(preEntry.getValue().getRot());
                return true;
            } else {
                t = (partialTicks + time - preEntry.getKey()) / (nextEntry.getKey() - preEntry.getKey());
            }
        }

        CameraKeyframe pre = preEntry.getValue();
        CameraKeyframe next = nextEntry.getValue();

        float tp, tr, tf;
        if (next.getPosTimeInterpolator() == TimeInterpolator.BEZIER) {
            tp = next.getPosBezier().interpolate(t);
            tr = next.getRotBezier().interpolate(t);
            tf = next.getFovBezier().interpolate(t);
        } else {
            tp = t;
            tr = t;
            tf = t;
        }

        switch (next.getPathInterpolator()) {
            case LINEAR -> {
                // 坐标
                line(tp, pre.getPos(), next.getPos(), posDest);
                // 旋转
                line(tr, pre.getRot(), next.getRot(), rotDest);
                // fov
                fov[0] = Mth.lerp(tf, pre.getFov(), next.getFov());
            }
            case SMOOTH -> {
                // 坐标
                Vector3f p0, p3;
                Map.Entry<Integer, CameraKeyframe> prePre = track.getPreEntry(preEntry.getKey());

                if (prePre == null) {
                    p0 = pre.getPos();
                } else {
                    p0 = prePre.getValue().getPos();
                }

                Map.Entry<Integer, CameraKeyframe> nextNext = track.getNextEntry(nextEntry.getKey());

                if (nextNext == null) {
                    p3 = next.getPos();
                } else {
                    p3 = nextNext.getValue().getPos();
                }

                catmullRom(tp, p0, pre.getPos(), next.getPos(), p3, posDest);

                // 旋转
                line(tr, pre.getRot(), next.getRot(), rotDest);
                // fov
                fov[0] = Mth.lerp(tf, pre.getFov(), next.getFov());
            }
            case BEZIER -> {
                // 坐标
                next.getPathBezier().interpolate(tp, pre.getPos(), next.getPos(), posDest);
                // 旋转
                line(tr, pre.getRot(), next.getRot(), rotDest);
                // fov
                fov[0] = Mth.lerp(tf, pre.getFov(), next.getFov());
            }
            case STEP -> {
                // 坐标
                posDest.set(pre.getPos());
                rotDest.set(pre.getRot());
                fov[0] = pre.getFov();
            }
        }

        return true;
    }
}
