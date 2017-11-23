package com.group31;

import com.group31.receiver.PetersonRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static ArrayList<String> getHosts(String filePath) {
        ArrayList<String> hosts = new ArrayList<>();

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
                        hosts.add(lineTxt);
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

        return hosts;
    }

    private static String getNextHostAddress(String filePath, Integer pid) throws IllegalArgumentException {
        ArrayList<String> hosts = getHosts(filePath);

        // Find self pid in the network
        for (int i = 0; i < hosts.size(); i++) {
            String hostDescr = hosts.get(i);

            if (Integer.parseInt(hostDescr.split(" ")[0]) == pid) {
                return hosts.get((i + 1) % hosts.size()).split(" ")[1];
            }
        }

        throw new IllegalArgumentException("Process Id " + pid + " was not found");
    }

    private static String getThisHostAddress(String filePath, Integer pid) throws IllegalArgumentException {
        ArrayList<String> hosts = getHosts(filePath);

        // Find self pid in the network
        for (int i = 0; i < hosts.size(); i++) {
            String hostDescr = hosts.get(i);

            if (Integer.parseInt(hostDescr.split(" ")[0]) == pid) {
                return hostDescr.split(" ")[1];
            }
        }

        throw new IllegalArgumentException("Process Id " + pid + " was not found");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("required args: <network-file> <this-pid>");
            System.exit(0);
        }

        final int PID = Integer.parseInt(args[1]);

        String nextHost = getNextHostAddress(args[0], PID);
        String thisHost = getThisHostAddress(args[0], PID);
        System.out.println("Next host: " + nextHost);

        PetersonRunnable petersonRunnable = new PetersonRunnable(PID, thisHost, nextHost);
        Thread petersonThread = new Thread(petersonRunnable);

        try {
            petersonThread.join();
            petersonThread.start();

            long delay = (long)(Math.max(Math.random(), 0.1) * 5000);
            System.out.println("First send delay: " + delay + " ms");
            Thread.sleep(delay);

            petersonRunnable.sendToNextHost(PID);
        } catch (Exception e) {
            System.out.println("Exception in Main");
            e.printStackTrace();
        }
    }
}
