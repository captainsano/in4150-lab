package com.group31;

public class ByzantineProcessDescription extends ProcessDescription {
    private boolean isMaliciousNode;

    public static ByzantineProcessDescription fromNetworkFileLine(String line) {
        String[] components = line.split(" ");

        if (components.length < 3) {
            throw new IllegalArgumentException("Expected 3 components: <pid> <hostname> <malicious>");
        }

        return new ByzantineProcessDescription(Integer.parseInt(components[0]), components[1], Integer.parseInt(components[2]) == 1);
    }

    public ByzantineProcessDescription(int pid, String hostname, boolean isMaliciousNode) {
        super(pid, hostname);
        this.isMaliciousNode = isMaliciousNode;
    }

    public boolean isMaliciousNode() {
        return isMaliciousNode;
    }


    @Override
    public String toString() {
        return "ByzantineProcessDescription{" +
                "  pid='" + pid + '\'' +
                ", hostname='" + hostname + '\'' +
                ", isMaliciousNode=" + isMaliciousNode +
                '}';
    }
}
