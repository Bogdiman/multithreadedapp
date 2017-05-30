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

    public static void populateList(List<Integer> list) {
        Random r = new Random();
        for (int i = 0; i < 500; i++) {
            list.add(r.nextInt(101));
        }
    }

    public static void main(String[] args) throws Exception {
        List<Integer> list = new ArrayList<>();
        populateList(list);

        ExecutorService executorService = Executors.newFixedThreadPool(25);

        List<Callable<Integer>> todos = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            Worker worker = new Worker(list, i);
            todos.add(worker);
        }

        List<Future<Integer>> resultList = executorService.invokeAll(todos);

        System.out.println("Answer is " + resultList.stream().mapToInt(x -> {
                try {
                    return x.get();
                } catch (Exception e) {

                }
                return 0;
            }).sum()
        );
        executorService.shutdown();
    }
}
