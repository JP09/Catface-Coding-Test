package com.catface.codingtest.manager;

import com.catface.codingtest.model.KeyframeSequence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KeyframeManager {

    private final Map<String, KeyframeSequence> sequences =
            new HashMap<>();

    private String currentSequenceName;

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

        if (name.equals(currentSequenceName)) {
            currentSequenceName = null;
        }
    }

    public void setCurrentSequenceName(String name){
        if (!sequences.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Sequence does not exist: " + name
            );
        }

        currentSequenceName = name;
    }

    public KeyframeSequence getCurrentSequence(){
        if (currentSequenceName == null) {
            return null;
        }

        return sequences.get(currentSequenceName);
    }

    public String getCurrentSequenceName() {
        return currentSequenceName;
    }

    public Collection<KeyframeSequence> getAllSequences() { return sequences.values(); }

    public Map<String, KeyframeSequence> getSequences() {
        return sequences;
    }
}
