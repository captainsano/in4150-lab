package com.group31;

import com.group31.receiver.HonestByzantineRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    private static ArrayList<ByzantineProcessDescription> getProcessesWithByzantineInfo(String filePath) {
        ArrayList<ByzantineProcessDescription> processes = new ArrayList<>();

        try {
            final String encoding = "UTF-8";
            File file = new File(filePath);

            if (file.isFile() && file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fis, encoding);
                BufferedReader bf = new BufferedReader(reader);

                String lineTxt;
                while ((lineTxt = bf.readLine()) != null) {
                    if (lineTxt.trim().length() > 0) {
                        processes.add(ByzantineProcessDescription.fromNetworkFileLine(lineTxt));
                    }
                }
                reader.close();
            } else {
                System.out.println("Can't find the file");
            }
        } catch (Exception e) {
            System.out.println("Exception in ReadFiles");
            e.printStackTrace();
        }

        return processes;
    }

    private static ArrayList<ProcessDescription> getAllProcesses(String filePath) {
        return new ArrayList<>(
                getProcessesWithByzantineInfo(filePath)
                        .stream()
                        .map((bpd) -> new ProcessDescription(bpd.pid, bpd.hostname))
                        .collect(Collectors.toList())
        );
    }

    private static ByzantineProcessDescription getCurrentProcess(String filePath, int pid) throws IllegalArgumentException {
        ArrayList<ByzantineProcessDescription> processes = getProcessesWithByzantineInfo(filePath);

        // Find self pid in the network
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getPid() == pid) {
                return processes.get(i);
            }
        }

        throw new IllegalArgumentException("Process pid " + pid + " was not found");
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("required args: <hostname> <n> <f>");
            System.exit(0);
        }

        System.out.println("Args: " + args[0] + " " + args[1] + " " + args[2]);

        String hostname = args[0];
        Integer n = Integer.parseInt(args[1]);
        Integer f = Integer.parseInt(args[2]);

        ArrayList<ByzantineProcessDescription> allProcesses = new ArrayList<>();
        ArrayList<ProcessDescription> allProcessWithoutMaliciousInfo = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            boolean isMalicious = f != 0;

            ByzantineProcessDescription p = new ByzantineProcessDescription(i + 1, hostname, isMalicious);
            if (isMalicious) {
                f = f - 1;
            }

            allProcesses.add(p);
            allProcessWithoutMaliciousInfo.add(p);
        }

        for (int i = 0; i < n; i++) {
            ByzantineProcessDescription p = allProcesses.get(i);

            Thread byzantineThread = new Thread(
                    p.isMaliciousNode() ? new MaliciousByzantineRunnable(allProcessWithoutMaliciousInfo, p) : new HonestByzantineRunnable(allProcessWithoutMaliciousInfo, p)
            );

            try {
                byzantineThread.join();
                byzantineThread.start();
            } catch (Exception e) {
                System.out.println("Exception in Main");
                e.printStackTrace();
            }
        }
    }
}
