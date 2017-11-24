package com.group31;

public class ProcessDescription {
    private String name;
    private String hostname;
    private int pid;

    public static ProcessDescription fromNetworkFileLine(String line) {
        String[] components = line.split(" ");

        if (components.length < 3) {
            throw new IllegalArgumentException("Expected 3 components: <name> <hostname> <pid>");
        }

        return new ProcessDescription(components[0], components[1], Integer.parseInt(components[2]));
    }

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
}
