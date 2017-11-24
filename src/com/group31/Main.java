package com.group31;

import com.group31.receiver.PetersonRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    private static ArrayList<ProcessDescription> getProcesses(String filePath) {
        ArrayList<ProcessDescription> processes = new ArrayList<>();

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
                        processes.add(ProcessDescription.fromNetworkFileLine(lineTxt));
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

    private static ProcessDescription getNextProcess(String filePath, String name) throws IllegalArgumentException {
        ArrayList<ProcessDescription> processes = getProcesses(filePath);

        // Find self pid in the network
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getName().equals(name)) {
                return processes.get((i + 1) % processes.size());
            }
        }

        throw new IllegalArgumentException("Process name " + name + " was not found");
    }

    private static ProcessDescription getCurrentProcess(String filePath, String name) throws IllegalArgumentException {
        ArrayList<ProcessDescription> processes = getProcesses(filePath);

        // Find self pid in the network
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getName().equals(name)) {
                return processes.get(i);
            }
        }

        throw new IllegalArgumentException("Process name " + name + " was not found");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("required args: <network-file> <this-process-name>");
            System.exit(0);
        }

        ProcessDescription thisProcess = getCurrentProcess(args[0], args[1]);
        ProcessDescription nextProcess = getNextProcess(args[0], args[1]);
        System.out.println("This process: " + thisProcess);
        System.out.println("Next process: " + nextProcess);

        PetersonRunnable petersonRunnable = new PetersonRunnable(thisProcess, nextProcess);
        Thread petersonThread = new Thread(petersonRunnable);

        try {
            petersonThread.join();
            petersonThread.start();

            Thread.sleep(15000);
            long delay = (long) (Math.max(Math.random(), 0.1) * 5000);
            System.out.println("First send delay: " + delay + " ms");
            Thread.sleep(delay);

            if (!petersonRunnable.hasStartedReceiving()) {
                petersonRunnable.sendToNextHost(thisProcess.getPid());
            }
        } catch (Exception e) {
            System.out.println("Exception in Main");
            e.printStackTrace();
        }
    }
}
