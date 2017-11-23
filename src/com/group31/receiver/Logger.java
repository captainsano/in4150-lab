package com.group31.receiver;

import java.io.*;

public class Logger<T> {
    private String prefix = "";
    private File file;
    private FileOutputStream fos;
    private BufferedWriter writer;

    Logger(String prefix, int PID) {
        this.prefix = prefix;

        String filePath = "./resources/process-" + prefix + "-" + PID + ".log";
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("Exception in createNewFile");
                e.printStackTrace();
            }
        }
        try {
            fos = new FileOutputStream(file);
            OutputStreamWriter oswriter = new OutputStreamWriter(fos);
            writer = new BufferedWriter(oswriter);
        } catch (Exception e) {
            System.out.println("Logger exception");
            e.printStackTrace();
        }
    }

    public void log(T message) {
        try {
            writer.write(prefix + " " + message + "\n");
            writer.flush();
        } catch (Exception e) {
            System.out.println("Exception in log");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (Exception e) {
            System.out.println("Exception in saveMessage");
            e.printStackTrace();
        }
    }
}
