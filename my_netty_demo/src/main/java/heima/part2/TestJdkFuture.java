package heima.part2;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建线程池
        ExecutorService service = Executors.newFixedThreadPool(2);
        //提交任务
        Future<Integer> futrue = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("计算中----");
                Thread.sleep(2000);
                return 1;
            }
        });

        //主线程通过futrue来获取结果,这是个同步的方法
        log.debug("等待结果");
        log.debug("结果是：{}",futrue.get());
    }
}
