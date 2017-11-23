import java.io.*;

public class ReadFiles {
    public static void readText(String filePath){
        try{
            //open a file
            String encoding = "UTF-8";
            File file = new File(filePath);

            if(file.isFile() && file.exists()){
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fis,encoding);
                BufferedReader bf = new BufferedReader(reader);

                String lineTxt;
                while( (lineTxt = bf.readLine()) != null){
                    System.out.println(lineTxt);
                }
                reader.close();
            }
            else{
                System.out.println("can't find the file");
            }
        }catch(Exception e){
            System.out.println("Exception in ReadFiles");
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        String filePath = "D:\\2017_Netherlands\\Courses\\Semester_1\\Q2\\Distributed Algorithms\\Lab\\Exercise 1\\ip.txt";
        readText(filePath);
    }
}
