package com.catface.codingtest.model;

public class CameraKeyframe {

    private final String name;

    private final double x;
    private final double y;
    private final double z;

    private final float yaw;
    private final float pitch;

    public CameraKeyframe(
            String name,
            double x,
            double y,
            double z,
            float yaw,
            float pitch) {

        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getName(){
        return name;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getZ(){
        return z;
    }

    public float getYaw(){
        return yaw;
    }

    public float getPitch(){
        return pitch;
    }
}
