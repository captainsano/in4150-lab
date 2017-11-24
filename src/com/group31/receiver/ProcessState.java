package com.group31.receiver;

class ProcessState {
    public static final int DONT_SEND = -999999;

    private final Integer PID;
    private Integer tid;
    private Integer ntid;
    private boolean elected = false;
    private boolean relaying = false;
    private boolean receivedNtid = false;

    ProcessState(int PID) {
        this.PID = PID;
        this.tid = PID;
    }

    private int receiveNtid(int ntid) {
        this.receivedNtid = true;
        this.ntid = ntid;
        return Math.max(tid, ntid);
    }

    private int receiveNntid(int nntid) {
        receivedNtid = true;
        // Check if simulated middle guy is greater than left and right
        if (ntid >= tid && ntid >= nntid) {
            tid = ntid;
        } else {
            relaying = true;
            return tid;
        }

        return tid;
    }

    synchronized public int receive(int pidFromUpstream) {
        if (elected || (pidFromUpstream == PID)) {
            elected = true;
            return DONT_SEND;
        }

        if (relaying) {
            System.out.println("relaying");
            return pidFromUpstream;
        }

        return receivedNtid ? receiveNntid(pidFromUpstream) : receiveNtid(pidFromUpstream);
    }

    public boolean isElected() {
        return elected;
    }
}