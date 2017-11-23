package com.group31;

import com.group31.receiver.ReceiverRunnable;
import com.group31.sender.SenderRunnable;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BSSProcess {
    private static ArrayList<String> getNetworkHosts(String filePath) {
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

    /**
     * The BSS process launches a sender and a receiver thread and waits for them to complete.
     *
     * @param args 0 - file to read the network addresses
     *             1 - this process number
     *             2 - max random delay in milliseconds
     *             3 - max messages to send
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("required args: <network-file> <this-pid> <max-random-delay-ms> <max-messages>");
            System.exit(0);
        }

        final ArrayList<String> HOSTS = getNetworkHosts(args[0]);
        final int PID = Integer.parseInt(args[1]);
        final int MAX_INTERVAL = Integer.parseInt(args[2]);
        final int MAX_MESSAGE_COUNT = Integer.parseInt(args[3]);
        final int TOTAL_PROCESS_COUNT = HOSTS.size(); // TODO: infer this from network file

        VectorClock localVectorClock = new VectorClock(TOTAL_PROCESS_COUNT);

        Thread senderThread = new Thread(new SenderRunnable(
                HOSTS,
                PID,
                MAX_MESSAGE_COUNT,
                MAX_INTERVAL,
                TOTAL_PROCESS_COUNT,
                localVectorClock
        ));

        Thread receiverThread = new Thread(new ReceiverRunnable(PID, localVectorClock));

        try {
            receiverThread.join();
            receiverThread.start();

            senderThread.join();
            senderThread.start();
        } catch (Exception e) {
            System.out.println("Exception in BSSProcess");
            e.printStackTrace();
        }
    }
}
