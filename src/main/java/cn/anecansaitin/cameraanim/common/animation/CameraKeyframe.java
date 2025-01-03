package cn.anecansaitin.cameraanim.common.animation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import org.joml.Vector3f;

/// 相机点位
public class CameraKeyframe {
    public static final CameraKeyframe NULL = new CameraKeyframe(new Vector3f(Float.MIN_VALUE), new Vector3f(), 0, PathInterpolator.STEP);
    private final Vector3f pos;
    private final Vector3f rot;
    private float fov;
    private PathInterpolator pathType;
    private final Vec3BezierController pathBezier;
    private TimeInterpolator posType;
    private final TimeBezierController posBezier;
    private TimeInterpolator rotType;
    private final TimeBezierController rotBezier;
    private TimeInterpolator fovType;
    private final TimeBezierController fovBezier;

    public CameraKeyframe(Vector3f pos, Vector3f rot, float fov, PathInterpolator pathType) {
        this.pos = pos;
        this.rot = rot;
        this.fov = fov;
        this.pathType = pathType;
        posType = TimeInterpolator.LINEAR;
        rotType = TimeInterpolator.LINEAR;
        fovType = TimeInterpolator.LINEAR;
        pathBezier = new Vec3BezierController(new Vector3f(), new Vector3f());
        posBezier = new TimeBezierController();
        rotBezier = new TimeBezierController();
        fovBezier = new TimeBezierController();
    }

    public CameraKeyframe(Vector3f pos, Vector3f rot, float fov) {
        this(pos, rot, fov, PathInterpolator.LINEAR);
    }

    private CameraKeyframe(Vector3f pos, Vector3f rot, float fov, PathInterpolator pathType, Vec3BezierController pathBezier, TimeInterpolator posType, TimeBezierController posBezier, TimeInterpolator rotType, TimeBezierController rotBezier, TimeInterpolator fovType, TimeBezierController fovBezier) {
        this.pos = pos;
        this.rot = rot;
        this.fov = fov;
        this.pathType = pathType;
        this.posType = posType;
        this.rotType = rotType;
        this.fovType = fovType;
        this.pathBezier = pathBezier;
        this.posBezier = posBezier;
        this.fovBezier = fovBezier;
        this.rotBezier = rotBezier;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(float x, float y, float z) {
        pos.set(x, y, z);
    }

    public Vector3f getRot() {
        return rot;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public PathInterpolator getPathInterpolator() {
        return pathType;
    }

    public void setPathInterpolator(PathInterpolator type) {
        this.pathType = type;
    }

    public Vec3BezierController getPathBezier() {
        return pathBezier;
    }

    public TimeBezierController getPosBezier() {
        return posBezier;
    }

    public TimeBezierController getRotBezier() {
        return rotBezier;
    }

    public TimeBezierController getFovBezier() {
        return fovBezier;
    }

    public TimeInterpolator getPosTimeInterpolator() {
        return posType;
    }

    public TimeInterpolator getRotTimeInterpolator() {
        return rotType;
    }

    public TimeInterpolator getFovTimeInterpolator() {
        return fovType;
    }

    public void setPosTimeInterpolator(TimeInterpolator type) {
        this.posType = type;
    }

    public void setRotTimeInterpolator(TimeInterpolator type) {
        this.rotType = type;
    }

    public void setFovTimeInterpolator(TimeInterpolator type) {
        this.fovType = type;
    }

    public static CompoundTag toNBT(CameraKeyframe keyframe) {
        CompoundTag root = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(FloatTag.valueOf(keyframe.pos.x));
        pos.add(FloatTag.valueOf(keyframe.pos.y));
        pos.add(FloatTag.valueOf(keyframe.pos.z));
        root.put("pos", pos);
        ListTag rot = new ListTag();
        rot.add(FloatTag.valueOf(keyframe.rot.x));
        rot.add(FloatTag.valueOf(keyframe.rot.y));
        rot.add(FloatTag.valueOf(keyframe.rot.z));
        root.put("rot", rot);
        root.putFloat("fov", keyframe.fov);
        root.putInt("pathType", keyframe.pathType.index);
        root.putInt("posType", keyframe.posType.index);
        root.putInt("rotType", keyframe.rotType.index);
        root.putInt("fovType", keyframe.fovType.index);
        root.put("pathBezier", Vec3BezierController.toNBT(keyframe.pathBezier));
        root.put("posBezier", TimeBezierController.toNBT(keyframe.posBezier));
        root.put("rotBezier", TimeBezierController.toNBT(keyframe.rotBezier));
        root.put("fovBezier", TimeBezierController.toNBT(keyframe.fovBezier));
        return root;
    }

    public static CameraKeyframe fromNBT(CompoundTag root) {
        return new CameraKeyframe(
                new Vector3f(root.getList("pos", 5).getFloat(0), root.getList("pos", 5).getFloat(1), root.getList("pos", 5).getFloat(2)),
                new Vector3f(root.getList("rot", 5).getFloat(0), root.getList("rot", 5).getFloat(1), root.getList("rot", 5).getFloat(2)),
                root.getFloat("fov"),
                PathInterpolator.fromIndex(root.getInt("pathType")),
                Vec3BezierController.fromNBT(root.getList("pathBezier", FloatTag.TAG_FLOAT)),
                TimeInterpolator.fromIndex(root.getInt("posType")),
                TimeBezierController.fromNBT(root.getList("posBezier", FloatTag.TAG_FLOAT)),
                TimeInterpolator.fromIndex(root.getInt("rotType")),
                TimeBezierController.fromNBT(root.getList("rotBezier", FloatTag.TAG_FLOAT)),
                TimeInterpolator.fromIndex(root.getInt("fovType")),
                TimeBezierController.fromNBT(root.getList("fovBezier", FloatTag.TAG_FLOAT))
        );
    }
}
