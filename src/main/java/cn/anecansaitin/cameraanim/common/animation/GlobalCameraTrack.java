package cn.anecansaitin.cameraanim.common.animation;

import cn.anecansaitin.cameraanim.client.TrackCache;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.joml.Vector3f;

import java.util.ArrayList;

/// 全局相机轨迹
public class GlobalCameraTrack {
    public static final GlobalCameraTrack NULL = new NullTrack();
    private final ArrayList<CameraPoint> points;
    private final IntArrayList timeLine;
    private final String id;

    public GlobalCameraTrack(String id) {
        points = new ArrayList<>();
        timeLine = new IntArrayList();
        this.id = id;
    }

    private GlobalCameraTrack(ArrayList<CameraPoint> points, IntArrayList timeLine, String id) {
        this.points = points;
        this.timeLine = timeLine;
        this.id = id;
    }

    /// 把点加入到指定时间
    ///
    /// 相同时间点进行覆盖
    public void add(int time, CameraPoint point) {
        if (timeLine.isEmpty()) {
            timeLine.add(time);
            points.add(point);
        } else {
            int left = 0;
            int right = timeLine.size() - 1;

            while (left <= right) {
                int mid = left + (right - left) / 2;
                int midVal = timeLine.getInt(mid);

                if (midVal < time) {
                    left = mid + 1;
                } else if (midVal > time) {
                    right = mid - 1;
                } else {
                    left = mid; // 找到了相同值的位置
                    right = -1; // -1表示已经存在这个值
                    break;
                }
            }

            if (right == -1) {
                points.set(left, point);
                updateBezier(left);
            } else {
                points.add(left, point);
                timeLine.add(left, time);
                updateBezier(left);
            }
        }
    }

    public void add(CameraPoint point) {
        int index = points.size();
        points.add(point);

        if (index > 0) {
            timeLine.add(timeLine.getInt(index - 1) + 20);
        } else {
            timeLine.add(0);
        }

        updateBezier(index);
    }

    /// 更新控制点
    private void updateBezier(int index) {
        CameraPoint point = points.get(index);

        if (index > 0 && point.getType() == PointInterpolationType.BEZIER) {
            CameraPoint prePoint = points.get(index - 1);
            Vector3f c = new Vector3f(point.getPosition()).add(prePoint.getPosition()).mul(0.5f);
            prePoint.setRightBezierControl(c.x, c.y, c.z);
            point.setLeftBezierControl(c.x, c.y, c.z);
        }

        if (index < points.size() - 1) {
            CameraPoint nextPoint = points.get(index + 1);

            if (nextPoint.getType() != PointInterpolationType.BEZIER) {
                return;
            }

            Vector3f c = new Vector3f(point.getPosition()).add(nextPoint.getPosition()).mul(0.5f);
            nextPoint.setLeftBezierControl(c.x, c.y, c.z);
            point.setRightBezierControl(c.x, c.y, c.z);
        }
    }

    public void remove(int index) {
        if (index > 0 && index < points.size() - 1) {
            CameraPoint next = points.get(index + 1);;

            if (next.getType() == PointInterpolationType.BEZIER) {
                CameraPoint pre = points.get(index - 1);
                Vector3f mid = new Vector3f(pre.getPosition()).add(next.getPosition()).mul(0.5f);
                pre.setRightBezierControl(mid.x, mid.y, mid.z);
                next.setLeftBezierControl(mid.x, mid.y, mid.z);
            }
        }

        points.remove(index);
        timeLine.removeInt(index);
    }

    public CameraPoint getPoint(int index) {
        return points.get(index);
    }

    public int getTime(int index) {
        return timeLine.getInt(index);
    }

    public int getCount() {
        return points.size();
    }

    public String getId() {
        return id;
    }
    public GlobalCameraTrack resetID(String id) {
        return new GlobalCameraTrack(points, timeLine, id);
    }

    private static class NullTrack extends GlobalCameraTrack {
        public NullTrack() {
            super("null");
        }

        @Override
        public void add(int time, CameraPoint point) {
        }

        @Override
        public void add(CameraPoint point) {
        }

        @Override
        public void remove(int index) {
        }

        @Override
        public CameraPoint getPoint(int index) {
            return CameraPoint.NULL;
        }

        @Override
        public int getTime(int index) {
            return 0;
        }
    }
}
