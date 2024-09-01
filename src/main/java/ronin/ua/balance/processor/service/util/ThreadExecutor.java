package ronin.ua.balance.processor.service.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;


@Component
public class ThreadExecutor {

    public static final int CORE_COUNT_OF_THREADS = 2;// random number depended on PC
    public static final int MAX_COUNT_OF_THREADS = 40;// random number depended on PC
    public static final int ALIVE_TIME=1;
    ExecutorService service;

    public ThreadExecutor() {
        this.service = new ThreadPoolExecutor(
                CORE_COUNT_OF_THREADS,
                MAX_COUNT_OF_THREADS,
                ALIVE_TIME,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>()
        );
    }


    public <T> List<T> asyncRunAndWaitResult(Collection<Callable<T>> tasks) {
        List<Future<T>> futures = new ArrayList<>();
        for (Callable<T> task : tasks) {
            futures.add(service.submit(task));
        }

        List<T> res = new ArrayList<>();
        for (Future<T> future : futures) {
            try {
                res.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    public void asyncRun(Collection<Runnable> tasks) {
        tasks.forEach((r)-> service.submit(r));
    }
}
