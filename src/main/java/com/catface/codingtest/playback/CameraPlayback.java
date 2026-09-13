package com.catface.codingtest.playback;

import com.catface.codingtest.model.CameraKeyframe;
import com.catface.codingtest.model.KeyframeSequence;
import net.minecraft.util.Mth;

import java.util.List;

public class CameraPlayback {

    private final CameraController cameraController;
    private KeyframeSequence activeSequence;

    private long startTime;
    private long durationMillis;

    private boolean playing;

    public CameraPlayback(CameraController cameraController) {
        this.cameraController = cameraController;
        this.playing = false;
    }

    public void start(KeyframeSequence sequence, double seconds) {
        if (sequence == null || sequence.getKeyframes().size() < 2 || seconds <= 0) {
            return;
        }

        this.activeSequence = sequence;
        this.durationMillis = (long) (seconds * 1000.0);
        this.startTime = System.currentTimeMillis();
        this.playing = true;
    }

    public void stop() {
        this.playing = false;
        this.activeSequence = null;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void update() {
        if (!playing || activeSequence == null) {
            return;
        }

        long currentTick = System.currentTimeMillis() - startTime;

        if (currentTick >= durationMillis) {
            CameraKeyframe finalKeyFrame = getLastKeyframe();

            applyKeyframe(finalKeyFrame);
            stop();
            return;
        }

        double progress = (double) currentTick / durationMillis;

        updateCamera(progress);

    }

    /**
     *  Calculate current camera position
     */

    private void updateCamera(double progress) {

        List<CameraKeyframe> keyframes = activeSequence.getKeyframes();

        int segmentCount = keyframes.size() - 1;
        double segmentProgress = progress * segmentCount;

        int segmentIndex = Math.min((int) Math.floor(segmentProgress), segmentCount - 1);

        double localProgress = segmentProgress - segmentIndex;
        CameraKeyframe start = keyframes.get(segmentIndex);
        CameraKeyframe end = keyframes.get(segmentIndex + 1);

        double x = interpolate(start.getX(), end.getX(), localProgress);
        double y = interpolate(start.getY(), end.getY(), localProgress);
        double z = interpolate(start.getZ(), end.getZ(), localProgress);

        float yaw = (float) interpolate(start.getYaw(), end.getYaw(), localProgress);
        float pitch = (float) interpolate(start.getPitch(), end.getPitch(), localProgress);

        cameraController.setCameraPosition(x, y, z, yaw, pitch);
    }

    /**
     *  Linear interpolation
     */

    private double interpolate(double start, double end, double progress) {
        return start + (end - start) * progress;
    }

    private CameraKeyframe getLastKeyframe() {
        List<CameraKeyframe> keyframes =
                activeSequence.getKeyframes();

        return keyframes.get(keyframes.size() - 1);
    }

    private void applyKeyframe(CameraKeyframe keyframe) {
        cameraController.setCameraPosition(keyframe.getX(), keyframe.getY(), keyframe.getZ(), keyframe.getYaw(), keyframe.getPitch());
    }
}
