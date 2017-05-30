import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by bitfoi on 5/29/2017.
 */
public class Worker implements Callable<Integer> {

    private List<Integer> list;
    private int index;

    public Worker(List<Integer> list, int threadNo) {
        this.list = list;
        index = threadNo;
    }

    public Integer call() throws Exception {
        int localSum = 0;

        for (int i = index; i < list.size(); i += 25) {
            localSum += list.get(i);
        }
        return localSum;
    }
}
