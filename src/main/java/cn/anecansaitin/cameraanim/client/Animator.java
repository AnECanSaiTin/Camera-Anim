package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.animation.TimeInterpolator;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.Map;

import static cn.anecansaitin.cameraanim.InterpolationMath.catmullRom;
import static cn.anecansaitin.cameraanim.InterpolationMath.line;
import static cn.anecansaitin.cameraanim.client.ClientUtil.partialTicks;

public class Animator {
    public static final Animator INSTANCE = new Animator();
    private GlobalCameraPath path;
    private boolean playing;
    private int time;

    private final Vector3f center = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    private final Matrix3f rotationMatrix = new Matrix3f();

    public void tick() {
        if (!playing || path == null) {
            return;
        }

        if (time > path.getLength()) {
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
        path = null;
        ClientUtil.resetCameraType();
    }

    public void resetAndPlay() {
        time = 0;
        playing = true;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPathAndPlay(GlobalCameraPath path) {
        this.path = path;
        resetAndPlay();
    }

    public void setPathAndPlay(GlobalCameraPath path, Vector3f center, Vector3f rotation) {
        this.path = path;
        this.center.set(center);
        this.rotation.set(rotation);
        rotationMatrix
                .identity()
                .rotateY((360 - rotation.y) * Mth.DEG_TO_RAD);
        resetAndPlay();
    }

    public boolean prepareCameraInfo(Vector3f posDest, Vector3f rotDest, float[] fov) {
        if (path == null) {
            return false;
        }

        float partialTicks = isPlaying() ? partialTicks() : 0;
        CameraKeyframe current = path.getPoint(time);

        if (current == null) {
            // 当前不处于关键帧上
            Map.Entry<Integer, CameraKeyframe> preEntry = path.getPreEntry(time);
            Map.Entry<Integer, CameraKeyframe> nextEntry = path.getNextEntry(time);
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

            float t1;
            // 坐标插值
            if (next.getPosTimeInterpolator() == TimeInterpolator.BEZIER) {
                t1 = next.getPosBezier().interpolate(t);
            } else {
                t1 = t;
            }

            switch (next.getPathInterpolator()) {
                case LINEAR -> line(t1, pre.getPos(), next.getPos(), posDest);
                case SMOOTH -> {
                    Vector3f p0, p3;
                    Map.Entry<Integer, CameraKeyframe> prePre = path.getPreEntry(preEntry.getKey());

                    if (prePre == null) {
                        p0 = pre.getPos();
                    } else {
                        p0 = prePre.getValue().getPos();
                    }

                    Map.Entry<Integer, CameraKeyframe> nextNext = path.getNextEntry(nextEntry.getKey());

                    if (nextNext == null) {
                        p3 = next.getPos();
                    } else {
                        p3 = nextNext.getValue().getPos();
                    }

                    catmullRom(t1, p0, pre.getPos(), next.getPos(), p3, posDest);
                }
                case BEZIER -> next.getPathBezier().interpolate(t1, pre.getPos(), next.getPos(), posDest);
                case STEP -> posDest.set(pre.getPos());
            }

            // 旋转插值
            if (next.getPosTimeInterpolator() == TimeInterpolator.BEZIER) {
                t1 = next.getRotBezier().interpolate(t);
            } else {
                t1 = t;
            }

            Vector3f preRot = pre.getRot();
            Vector3f nextRot = next.getRot();
            line(t1, preRot, nextRot, rotDest);

            // fov插值
            if (next.getPosTimeInterpolator() == TimeInterpolator.BEZIER) {
                t1 = next.getRotBezier().interpolate(t);
            } else {
                t1 = t;
            }

            fov[0] = Mth.lerp(t1, pre.getFov(), next.getFov());
        } else {
            posDest.set(current.getPos());
            rotDest.set(current.getRot());
            fov[0] = current.getFov();
        }

        if (path.isNativeMode()) {
            rotationMatrix.transform(posDest).add(center);
            rotDest.add(rotation);
        }

        return true;
    }
}
