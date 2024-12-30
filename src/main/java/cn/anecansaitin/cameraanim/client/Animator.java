package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraTrack;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.Map;

import static cn.anecansaitin.cameraanim.client.ClientUtil.partialTicks;
import static cn.anecansaitin.cameraanim.InterpolationMath.*;

public class Animator {
    public static final Animator INSTANCE = new Animator();
    private boolean preview;
    private boolean playing;
    private int time;

    public void tick() {
        if (!preview || !playing) {
            return;
        }

        if (time > TrackCache.getTrack().getLength()) {
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
        if (time >= TrackCache.getTrack().getLength()) {
            return;
        }

        time = Math.min(TrackCache.getTrack().getLength(), time + 5);
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public boolean isPlaying() {
        return preview && playing;
    }

    public boolean getCameraInfo(Vector3f posDest, Vector3f rotDest, float[] fov) {
        float partialTicks = isPlaying() ? partialTicks() : 0;
        GlobalCameraTrack track = TrackCache.getTrack();
        CameraPoint current = track.getPoint(time);

        if (current == null) {
            // 当前不处于关键帧上
            Map.Entry<Integer, CameraPoint> preEntry = track.getPreEntry(time);
            Map.Entry<Integer, CameraPoint> nextEntry = track.getNextEntry(time);
            float t;

            if (preEntry == null) {
                if (nextEntry == null) return false;
                posDest.set(nextEntry.getValue().getPosition());
                rotDest.set(nextEntry.getValue().getRotation());
                return true;
            } else {
                if (nextEntry == null) {
                    posDest.set(preEntry.getValue().getPosition());
                    rotDest.set(preEntry.getValue().getRotation());
                    return true;
                } else {
                    t = (partialTicks + time - preEntry.getKey()) / (nextEntry.getKey() - preEntry.getKey());
                }
            }

            CameraPoint pre = preEntry.getValue();
            CameraPoint next = nextEntry.getValue();

            // todo 这里的插值暂时都是线性，之后需要能设置
            // 坐标插值
            switch (next.getType()) {
                case LINEAR -> {
                    line(t, pre.getPosition(), next.getPosition(), posDest);
                }
                case SMOOTH -> {
                    Vector3f p0, p3;
                    Map.Entry<Integer, CameraPoint> prePre = track.getPreEntry(preEntry.getKey());

                    if (prePre == null) {
                        p0 = pre.getPosition();
                    } else {
                        p0 = prePre.getValue().getPosition();
                    }

                    Map.Entry<Integer, CameraPoint> nextNext = track.getNextEntry(nextEntry.getKey());

                    if (nextNext == null) {
                        p3 = next.getPosition();
                    } else {
                        p3 = nextNext.getValue().getPosition();
                    }

                    catmullRom(t, p0, pre.getPosition(), next.getPosition(), p3, posDest);
                }
                case BEZIER -> {
                    bezier(t, pre.getPosition(), pre.getRightBezierControl(), next.getLeftBezierControl(), next.getPosition(), posDest);
                }
                case STEP -> {
                    posDest.set(pre.getPosition());
                }
            }

            // 旋转插值
            Vector3f preRot = pre.getRotation();
            Vector3f nextRot = next.getRotation();
            line(t, preRot, nextRot, rotDest);

            // fov插值
            fov[0] = Mth.lerp(t, pre.getFov(), next.getFov());
        } else {
            posDest.set(current.getPosition());
            rotDest.set(current.getRotation());
            fov[0] = current.getFov();
        }

        return true;
    }
}
