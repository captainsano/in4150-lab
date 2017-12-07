package com.group31;

public class ProcessDescription {
    protected String name;
    protected String hostname;
    protected int pid;

    public ProcessDescription(String name, String hostname, int pid) {
        this.name = name;
        this.hostname = hostname;
        this.pid = pid;
    }

    public String getName() {
        return name;
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
                "name='" + name + '\'' +
                ", hostname='" + hostname + '\'' +
                ", pid=" + pid +
                '}';
    }

    @Override
    public int hashCode() {
        return this.pid;
    }
}
