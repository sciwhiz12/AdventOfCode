package test;

import common.Timings;

public class TimingsTest {
    public static void main(String[] args) throws InterruptedException {
        Timings.start();
        System.out.println("Timings Test, waiting 1000 milisecs");
        Thread.sleep(1000);
        Timings.stop();
    }
}
