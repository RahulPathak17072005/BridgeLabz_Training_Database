package org.example;
public class MonitorTask implements Runnable{

    @Override
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println("School Registry Online");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }
}




