package com.group31;

import com.group31.receiver.HonestByzantineRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
        if (args.length < 2) {
            System.out.println("required args: <network-file> <this-process-pid>");
            System.exit(0);
        }

        ArrayList<ProcessDescription> allProcesses = getAllProcesses(args[0]);
        ByzantineProcessDescription thisProcess = getCurrentProcess(args[0], Integer.parseInt(args[1]));
        System.out.println("This process: " + thisProcess);

        Thread byzantineThread = new Thread(
                thisProcess.isMaliciousNode() ?
                        new MaliciousByzantineRunnable(allProcesses, thisProcess) :
                        new HonestByzantineRunnable(allProcesses, thisProcess)
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
