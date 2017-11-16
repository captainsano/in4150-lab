package com.group31;

public class Main {

    public static void main(String[] args) {
        VectorClock vc1 = new VectorClock(5);

        vc1.increment(2);

        System.out.println("VC1: " + vc1);

        VectorClock vc2 = new VectorClock(5);

        vc2.increment(0);
        vc2.increment(2);

        System.out.println("VC2: " + vc2);

        System.out.println("is one less: " + vc1.isOnlyOneComponentLessByOne(vc2));
    }
}
