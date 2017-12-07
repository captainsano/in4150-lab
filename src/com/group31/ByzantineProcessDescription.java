package com.group31;

public class ByzantineProcessDescription extends ProcessDescription {
    private boolean isMaliciousNode;

    public static ByzantineProcessDescription fromNetworkFileLine(String line) {
        String[] components = line.split(" ");

        if (components.length < 3) {
            throw new IllegalArgumentException("Expected 4 components: <name> <hostname> <pid> <malicious>");
        }

        return new ByzantineProcessDescription(components[0], components[1], Integer.parseInt(components[2]), Integer.parseInt(components[3]) == 1);
    }

    public ByzantineProcessDescription(String name, String hostname, int pid, boolean isMaliciousNode) {
        super(name, hostname, pid);
        this.isMaliciousNode = isMaliciousNode;
    }

    public boolean isMaliciousNode() {
        return isMaliciousNode;
    }


    @Override
    public String toString() {
        return "ByzantineProcessDescription{" +
                "  name='" + name + '\'' +
                ", hostname='" + hostname + '\'' +
                ", pid=" + pid +
                ", isMaliciousNode=" + isMaliciousNode +
                '}';
    }
}
