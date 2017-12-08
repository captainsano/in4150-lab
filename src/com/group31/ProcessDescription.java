package com.group31;

import java.io.Serializable;

public class ProcessDescription implements Serializable {
    protected String hostname;
    protected int pid;

    public ProcessDescription(int pid, String hostname) {
        this.hostname = hostname;
        this.pid = pid;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPid() {
        return pid;
    }

    @Override
    public String toString() {
        return "ProcessDescription{" +
                "pid='" + pid + '\'' +
                ", hostname='" + hostname + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return this.pid;
    }
}
