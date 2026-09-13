package com.catface.codingtest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyframeSequence {

    private final String name;

    private final List<CameraKeyframe> keyframes =
            new ArrayList<>();

    public KeyframeSequence(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void addKeyframe(CameraKeyframe keyframe){
        keyframes.add(keyframe);
    }

    public List<CameraKeyframe> getKeyframes(){
        return Collections.unmodifiableList(keyframes);
    }

    public int size(){
        return keyframes.size();
    }

    public boolean isEmpty(){
        return keyframes.isEmpty();
    }
}
