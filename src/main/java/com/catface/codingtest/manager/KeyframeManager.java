package com.catface.codingtest.manager;

import com.catface.codingtest.model.KeyframeSequence;

import java.util.HashMap;
import java.util.Map;

public class KeyframeManager {

    private final Map<String, KeyframeSequence> sequences =
            new HashMap<>();

    public KeyframeSequence getOrCreateSequence(String name) {
        return sequences.computeIfAbsent(
                name,
                KeyframeSequence::new
        );
    }

    public KeyframeSequence getSequence(String name){
        return sequences.get(name);
    }

    public boolean checkSequence(String name){
        return sequences.containsKey(name);
    }

    public void deleteSequence(String name){
        sequences.remove(name);
    }

    public Map<String, KeyframeSequence> getSequences() {
        return sequences;
    }
}
