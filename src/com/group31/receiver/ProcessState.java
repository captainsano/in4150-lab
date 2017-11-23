package com.group31.receiver;

class ProcessState {
    public static final int DONT_SEND = -999999;

    private final Integer PID;
    private Integer tid;
    private Integer ntid;
    private boolean elected = false;
    private boolean relaying = false;
    private boolean roundComplete = true;

    ProcessState(int PID) {
        this.PID = PID;
        this.tid = PID;
    }

    private int receiveNtid(int ntid) {
        this.roundComplete = false;
        this.ntid = ntid;
        return Math.max(tid, ntid);
    }

    private int receiveNntid(int nntid) {
        if (relaying) {
            tid = nntid;
            return tid;
        }

        if (ntid >= tid && ntid >= nntid) {
            tid = ntid;
        } else {
            relaying = true;
            return DONT_SEND;
        }

        roundComplete = true;
        return tid;
    }

    public int receive(int nid) {
        if (elected || (nid == PID)) {
            elected = true;
            return PID;
        }

        if (relaying) {
            return nid;
        }

        return roundComplete ? receiveNtid(nid) : receiveNntid(nid);
    }

    public boolean isElected() {
        return elected;
    }
}