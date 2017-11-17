package com.group31;

import java.io.Serializable;

public class VectorClock implements Serializable {
    private Integer[] vector;
    private int size;

    public VectorClock(int size) throws IllegalArgumentException {
        if (size == 0) {
            throw new IllegalArgumentException("Size has to be at least 1");
        }

        this.size = size;

        vector = new Integer[size];
        clear();
    }

    public VectorClock(VectorClock other) {
        size = other.size;
        vector = new Integer[size];
        for (int i = 0; i < size; i++) {
            vector[i] = other.vector[i];
        }
    }

    synchronized public void clear() {
        for (int i = 0; i < this.size; i++) {
            this.vector[i] = 0;
        }
    }

    synchronized public Integer getIndex(int i) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i > this.size - 1) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return this.vector[i];
    }

    synchronized public void increment(int i) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i > this.size - 1) {
            throw new ArrayIndexOutOfBoundsException();
        }

        this.vector[i] += 1;
    }

    synchronized public boolean isGreaterThanOrEqualTo(VectorClock other) throws IllegalArgumentException {
        if (this.size != other.size) {
            throw new IllegalArgumentException("Vectors in comparison should be of same size");
        }

        for (int i = 0; i < other.getIndex(i); i++) {
            if (getIndex(i) < other.getIndex(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    synchronized public String toString() {
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
