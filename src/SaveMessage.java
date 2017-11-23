package com.group31.receiver;
import com.group31.Message;

import java.io.*;

public class SaveMessage {
    public static void saveMessage(Message message){
        String filePath = "D:\\2017_Netherlands\\Courses\\Semester_1\\Q2\\Distributed Algorithms\\Lab\\Exercise 1\\message.txt";
        File file = new File(filePath);
        if(!file.exists()){
            try{file.createNewFile();
            }catch (Exception e){
                System.out.println("Exception in createNewFile");
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(writer);

            bw.write(message + "\r\n");
            bw.flush();
            bw.close();
            writer.close();
            fos.close();
        }catch (Exception e){
            System.out.println("Exception in saveMessage");
            e.printStackTrace();
        }
    }

}
