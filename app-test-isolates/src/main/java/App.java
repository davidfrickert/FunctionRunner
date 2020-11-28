import rest.request.compression.Sleep;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class App {
    static final boolean ISOLATE_ENABLED = false;
    //static final Queue<Long> latency = new ConcurrentLinkedQueue<>();
    static final List<Thread> threads = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            //final Runtime runtime = Runtime.getRuntime();

            //System.out.println("(Runtime)Main total memory: " + runtime.totalMemory() / (1024 * 1024) + "MB");
            //System.out.println("(Runtime)Main used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

            //System.out.println("(MXBeans)Main committed memory: " +  ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / (1024 * 1024) + "MB");
            //System.out.println("(MXBeans)Main used memory: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / (1024 * 1024) + "MB");

            final Thread thread;
            if (ISOLATE_ENABLED) {
                thread = new Thread(() -> {
                    //final long startTime = System.nanoTime();
                    Sleep.sleepIsolate(10000L);
                    //final long timeTaken = System.nanoTime() - startTime;
                    //latency.add(timeTaken);
                });
            } else {
                thread = new Thread(() -> {
                    //final long startTime = System.nanoTime();
                    Sleep.sleep(10000L);
                    //final long timeTaken = System.nanoTime() - startTime;
                    //latency.add(timeTaken);
                });
            }
            threads.add(thread);
            thread.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

       // final double asDouble = latency.stream().mapToLong(a -> a).average().getAsDouble();
        //System.out.println("Average request time: " + asDouble / 1_000_000 + "ms");


    }

}
