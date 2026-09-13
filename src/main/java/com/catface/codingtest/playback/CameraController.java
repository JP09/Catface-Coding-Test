package com.catface.codingtest.playback;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;


public class CameraController {

    private final Minecraft minecraft;

    public CameraController() {
        this.minecraft = Minecraft.getInstance();
    }

    public void setCameraPosition(
            double x,
            double y,
            double z,
            float yaw,
            float pitch) {
        if (minecraft.gameRenderer == null) {
            return;
        }

        Entity camera = minecraft.getCameraEntity();

        if(camera == null) {
            return;
        }

        camera.setPos(x, y, z);
        camera.setYRot(yaw);
        camera.setXRot(pitch);
    }
}
