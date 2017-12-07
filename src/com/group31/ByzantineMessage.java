package com.group31;

public class ByzantineMessage {
    private ProcessDescription sender;
    private String phase;
    private Integer round;
    private Integer w;

    public ByzantineMessage(ProcessDescription sender, String phase, Integer round, Integer w) {
        this.sender = sender;
        this.phase = phase;
        this.round = round;
        this.w = w;
    }

    public ProcessDescription getSender() {
        return sender;
    }

    public String getPhase() {
        return phase;
    }

    public Integer getRound() {
        return round;
    }

    public Integer getW() {
        return w;
    }

    @Override
    public String toString() {
        return "ByzantineMessage{" +
                "sender=" + sender +
                ", phase='" + phase + '\'' +
                ", round=" + round +
                ", w=" + w +
                '}';
    }
}
