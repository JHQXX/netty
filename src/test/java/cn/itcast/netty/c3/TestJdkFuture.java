package cn.itcast.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Future 用于线程之间的通信 JdkFuture JDK原生的future
 */
@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.线程池
        ExecutorService service = Executors.newFixedThreadPool(2);
        //2.提交任务
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });

        //3.主线程 通过 future 来获取结果
        log.debug("等待结果");
        //阻塞方法 同步等待线程结果出现
        log.debug("结果 {}",future.get());
    }
}
