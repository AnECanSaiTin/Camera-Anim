package cn.anecansaitin.cameraanim.client.animation;

import cn.anecansaitin.cameraanim.common.animation.interpolation.types.TimeInterpolator;
import cn.anecansaitin.cameraanim.util.InterpolationMath;
import cn.anecansaitin.cameraanim.client.ide.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.util.ClientUtil;
import cn.anecansaitin.cameraanim.common.animation.*;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.Map;

public class PreviewAnimator {
    public static final PreviewAnimator INSTANCE = new PreviewAnimator();
    private boolean playing;
    private int time;

    private PreviewAnimator() {
    }

    public void tick() {
        if (!playing) {
            return;
        }

        if (time > CameraAnimIdeCache.getPath().getLength()) {
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
        int length = CameraAnimIdeCache.getPath().getLength();

        if (time >= length) {
            return;
        }

        time = Math.min(length, time + 5);
    }

    public boolean isPlaying() {
        return playing;
    }

    /**
     * Prepares the camera's position, rotation, and field of view (FOV) based on the current animation time.
     *
     * @param posDest The destination vector to store the interpolated position.
     * @param rotDest The destination vector to store the interpolated rotation.
     * @param fov     A single-element array to store the interpolated FOV.
     * @return {@code true} if the camera info was successfully prepared, {@code false} if no valid keyframes exist.
     */
    public boolean prepareCameraInfo(Vector3f posDest, Vector3f rotDest, float[] fov) {
        float partialTicks = playing ? ClientUtil.partialTicks() : 0;
        GlobalCameraPath path = CameraAnimIdeCache.getPath();
        CameraKeyframe current = path.getPoint(time);

        if (current != null) {
            posDest.set(current.getPos());
            rotDest.set(current.getRot());
            fov[0] = current.getFov();
            return true;
        }

        Map.Entry<Integer, CameraKeyframe> preEntry = path.getPreEntry(time);
        Map.Entry<Integer, CameraKeyframe> nextEntry = path.getNextEntry(time);

        if (preEntry == null && nextEntry == null) {
            return false;
        }
        if (preEntry == null) {
            posDest.set(nextEntry.getValue().getPos());
            rotDest.set(nextEntry.getValue().getRot());
            fov[0] = nextEntry.getValue().getFov();
            return true;
        }
        if (nextEntry == null) {
            posDest.set(preEntry.getValue().getPos());
            rotDest.set(preEntry.getValue().getRot());
            fov[0] = preEntry.getValue().getFov();
            return true;
        }

        float t = calculateInterpolationFactor(preEntry.getKey(), nextEntry.getKey(), partialTicks);
        interpolateCamera(preEntry.getValue(), nextEntry.getValue(), t, path, posDest, rotDest, fov);
        return true;
    }

    /**
     * Calculates the interpolation factor (t) between two keyframes.
     *
     * @param preTime      The time of the previous keyframe.
     * @param nextTime     The time of the next keyframe.
     * @param partialTicks The partial tick value for smooth interpolation.
     * @return The interpolation factor in the range [0, 1].
     */
    private float calculateInterpolationFactor(int preTime, int nextTime, float partialTicks) {
        return (partialTicks + time - preTime) / (nextTime - preTime);
    }

    /**
     * Interpolates position, rotation, and FOV between two keyframes.
     *
     * @param pre     The previous keyframe.
     * @param next    The next keyframe.
     * @param t       The interpolation factor.
     * @param path    The camera path for accessing adjacent keyframes.
     * @param posDest The destination position vector.
     * @param rotDest The destination rotation vector.
     * @param fov     The destination FOV array.
     */
    private void interpolateCamera(CameraKeyframe pre, CameraKeyframe next, float t,
                                   GlobalCameraPath path, Vector3f posDest, Vector3f rotDest, float[] fov) {
        // Position interpolation
        float posT = (next.getPosTimeInterpolator() == TimeInterpolator.BEZIER)
                ? next.getPosBezier().interpolate(t)
                : t;
        interpolatePosition(pre, next, posT, path, posDest);

        // Rotation interpolation
        float rotT = (next.getRotTimeInterpolator() == TimeInterpolator.BEZIER)
                ? next.getRotBezier().interpolate(t)
                : t;
        InterpolationMath.line(rotT, pre.getRot(), next.getRot(), rotDest);

        // FOV interpolation
        float fovT = (next.getFovTimeInterpolator() == TimeInterpolator.BEZIER)
                ? next.getFovBezier().interpolate(t)
                : t;
        fov[0] = Mth.lerp(fovT, pre.getFov(), next.getFov());
    }

    /**
     * Interpolates the position between two keyframes based on the path type.
     *
     * @param pre     The previous keyframe.
     * @param next    The next keyframe.
     * @param t       The interpolation factor for position.
     * @param path    The camera path for accessing adjacent keyframes.
     * @param posDest The destination position vector.
     */
    private void interpolatePosition(CameraKeyframe pre, CameraKeyframe next, float t,
                                     GlobalCameraPath path, Vector3f posDest) {
        switch (next.getPathInterpolator()) {
            case LINEAR:
                InterpolationMath.line(t, pre.getPos(), next.getPos(), posDest);
                break;
            case SMOOTH:
                Vector3f p0 = getPrecedingPosition(pre, path, path.getPreEntry(time));
                Vector3f p3 = getFollowingPosition(next, path, path.getNextEntry(time));
                InterpolationMath.catmullRom(t, p0, pre.getPos(), next.getPos(), p3, posDest);
                break;
            case BEZIER:
                next.getPathBezier().interpolate(t, pre.getPos(), next.getPos(), posDest);
                break;
            case STEP:
                posDest.set(pre.getPos());
                break;
        }
    }

    /**
     * Retrieves the position of the keyframe preceding the given one, or defaults to the given keyframe's position.
     *
     * @param pre     The current keyframe.
     * @param path    The camera path.
     * @param preEntry The entry of the previous keyframe relative to the current time.
     * @return The position of the preceding keyframe, or the current keyframe's position if none exists.
     */
    private Vector3f getPrecedingPosition(CameraKeyframe pre, GlobalCameraPath path, Map.Entry<Integer, CameraKeyframe> preEntry) {
        Map.Entry<Integer, CameraKeyframe> prePre = preEntry != null ? path.getPreEntry(preEntry.getKey()) : null;
        return (prePre == null) ? pre.getPos() : prePre.getValue().getPos();
    }

    /**
     * Retrieves the position of the keyframe following the given one, or defaults to the given keyframe's position.
     *
     * @param next     The current keyframe.
     * @param path     The camera path.
     * @param nextEntry The entry of the next keyframe relative to the current time.
     * @return The position of the following keyframe, or the current keyframe's position if none exists.
     */
    private Vector3f getFollowingPosition(CameraKeyframe next, GlobalCameraPath path, Map.Entry<Integer, CameraKeyframe> nextEntry) {
        Map.Entry<Integer, CameraKeyframe> nextNext = nextEntry != null ? path.getNextEntry(nextEntry.getKey()) : null;
        return (nextNext == null) ? next.getPos() : nextNext.getValue().getPos();
    }
}
