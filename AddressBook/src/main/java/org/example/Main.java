package org.example;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws Exception {
        SimpleRepository<ParentContact> repo=new SimpleRepository<>();

        //load
        FileService.loadCSV("parents.csv",repo);

//backup
        FileService.backupFile("parents.csv","parents.bak");

//Display
        System.out.println("..............Parent Records..............");

        repo.getAll().forEach(System.out::println);

        //search by the prefix parent name

        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the name of the parent record prefix: - ");
        String keyword=sc.nextLine();

        repo.getAll().stream().filter(p->p.getParentName()
                .startsWith(keyword)).forEach(System.out::println);

//Delete
        System.out.print("\nEnter Email To Delete : ");
        String email = sc.nextLine();

        repo.remove(p -> ((ParentContact) p).getParentEmail()
                .equalsIgnoreCase(email));

        System.out.println("\nAfter Delete");

        repo.getAll().forEach(System.out::println);

        // Export
        if (!repo.getAll().isEmpty()) {
            FileService.exportJSON(
                    repo.getAll().get(0));
        }

        // Thread State Demo
        Thread monitor =
                new Thread(new MonitorTask());

        System.out.println(
                "\nBefore Start : " + monitor.getState());

        monitor.start();

        System.out.println("After Start : " + monitor.getState());

        monitor.join();

        System.out.println("After Join : " + monitor.getState());
    }

}
