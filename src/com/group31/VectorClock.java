package com.group31;

import java.lang.reflect.Array;

public class VectorClock {
    private Integer[] vector;
    private int size;

    VectorClock(int size) throws IllegalArgumentException {
        if (size == 0) {
            throw new IllegalArgumentException("Size has to be at least 1");
        }

        this.size = size;

        vector = new Integer[size];
        for (int i = 0; i < size; i++) {
            vector[i] = 0;
        }
    }

    public Integer getIndex(int i) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i > this.size - 1) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return this.vector[i];
    }

    // TODO: Syncrhonized
    public void increment(int i) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i > this.size - 1) {
            throw new ArrayIndexOutOfBoundsException();
        }

        this.vector[i] += 1;
    }

    public VectorClock max(VectorClock other) throws IllegalArgumentException {
        if (this.size != other.size) {
            throw new IllegalArgumentException("Vectors in comparison should be of same size");
        }

        VectorClock maxVectorClock = new VectorClock(this.size);
        for (int i = 0; i < this.size; i++) {
            maxVectorClock.vector[i] = Math.max(this.vector[i], other.vector[i]);
        }

        return maxVectorClock;
    }

    public boolean isOnlyOneComponentLessByOne(VectorClock other) throws IllegalArgumentException {
        if (this.size != other.size) {
            throw new IllegalArgumentException("Vectors in comparison should be of same size");
        }

        int diffs = 0;
        for (int i = 0; i < this.size; i++) {
            if (this.vector[i] < other.vector[i] && other.vector[i] - this.vector[i] == 1) {
                diffs += 1;
                if (diffs > 1) { return false; }
            }
        }
        return diffs == 1;
    }

    @Override
    public String toString() {
        StringBuilder vcString = new StringBuilder();
        vcString.append("[ ");

        for (int i = 0; i < this.size; i++) {
            vcString.append(vector[i]);
            if (i != this.size - 1) {
                vcString.append(", ");
            }
        }

        vcString.append(" ]");
        return vcString.toString();
    }
}
