package org.example;
import java.io.*;
import java.util.*;
import static java.lang.Thread.startVirtualThread;

public class FileService {
    public static void loadCSV(String fileName, SimpleRepository<ParentContact> repo) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (!data[1].contains("@")) {

                    System.out.println("Invalid Email Skipped : " + data[1]);
                    continue;
                }
                ParentContact parent=new ParentContact(data[0],data[1],data[2],
                     data[3]   );

                repo.add(parent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void exportJSON(ParentContact p){
        try(FileWriter fw = new FileWriter("parent_card.json")){

            fw.write("{\n");
            fw.write("\"parentName\":\"" + p.getParentName() + "\",\n");
            fw.write("\"parentEmail\":\"" + p.getParentEmail() + "\",\n");
            fw.write("\"contactPhone\":\"" + p.getContactPhone() + "\",\n");
            fw.write("\"gradeLevel\":\"" + p.getGradeLevel() + "\"\n");
            fw.write("}");

            System.out.println("JSoN Exported Successfully");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void backupFile(String source,String destination){
        Thread.startVirtualThread(()->{
            try(FileInputStream fis=new FileInputStream(source);
            FileOutputStream fos=new FileOutputStream(destination)){
                int data;
                while ((data = fis.read()) != -1) fos.write(data);
            }catch(Exception e){
              //  e.printStackTrace();
                System.out.println(e.getMessage());
            }
        });
    }
}



