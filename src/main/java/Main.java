import com.sun.management.OperatingSystemMXBean;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bitfoi on 5/29/2017.
 */
public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    private static OperatingSystemMXBean operatingSystemMXBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static void populateList(List<Integer> list, int listSize) {
        Random r = new Random();
        for (int i = 0; i < listSize; i++) {
            list.add(r.nextInt(101));
        }
    }

    public static void main(String[] args) throws Exception {
        List<Integer> list = new ArrayList<>();

        double avgCpuUsage = 0;
        long avgRamUsage = 0;

        long ramUsage;
        double cpuUsage;

        int port = 9875;

        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();

            long startTime = System.currentTimeMillis();

            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            int listSize = in.readInt();
            populateList(list, listSize);

            ExecutorService executorService = Executors.newFixedThreadPool(25);

            List<Callable<Integer>> todos = new ArrayList<>();

            for (int i = 0; i < 25; i++) {
                Worker worker = new Worker(list, i);
                todos.add(worker);
            }

            List<Future<Integer>> resultList = executorService.invokeAll(todos);

            cpuUsage = operatingSystemMXBean.getProcessCpuLoad();
            avgCpuUsage += cpuUsage;
            ramUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            avgRamUsage += ramUsage;

            int sum = 0;
            for (int i = 0; i < resultList.size(); i++) {
                sum += resultList.get(0).get();
            }

            System.out.println("Answer is " + sum);

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            executorService.shutdown();

            in.close();
            clientSocket.close();

            logger.info("-----------------------------------------");
            logger.info("MAP REDUCE: This iteration used as avg of " + avgCpuUsage * 100 + "% of CPU");
            logger.info("MAP REDUCE: This iteration used an avg of RAM usage of " + avgRamUsage);
            logger.info("MAP REDUCE: Time needed is " + totalTime + " (msec)");
            logger.info("-----------------------------------------");
        }
    }
}
