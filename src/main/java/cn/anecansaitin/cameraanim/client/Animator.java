package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.common.animation.CameraPoint;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraTrack;
import org.joml.Vector3f;

import static cn.anecansaitin.cameraanim.client.ClientUtil.partialTicks;
import static cn.anecansaitin.cameraanim.InterpolationMath.*;

public class Animator {
    public static final Animator INSTANCE = new Animator();
    private boolean playing;
    private int time;

    public void tick() {
        if (!playing) {
            return;
        }

        time++;
    }

    public void stop() {
        playing = false;
        time = 0;
    }

    public boolean isPlaying() {
        return playing;
    }

    public Vector3f getPosition() {
        float partialTicks = partialTicks();
        GlobalCameraTrack track = TrackCache.getTrack();

        return new Vector3f();
    }

    public Vector3f getRotation() {
        return new Vector3f();
    }


    public float getFov() {
        return 0;
    }
}
