package com.group31;

import java.io.Serializable;

public class Message implements Serializable {
    private int sourcePid;
    private VectorClock timestamp;
    private String content;

    public Message(int sourcePid, VectorClock timestamp, String content) {
        this.sourcePid = sourcePid;
        this.timestamp = new VectorClock(timestamp); // Make a copy
        this.content = content;
    }

    public int getSourcePid() {
        return sourcePid;
    }

    public VectorClock getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Message from: ");
        b.append(getSourcePid());
        b.append(" at ");
        b.append(getTimestamp().toString());
        b.append(" content ");
        b.append(getContent());
        return b.toString();
    }
}
