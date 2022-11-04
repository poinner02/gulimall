package com.merchen.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * @author MrChen
 * @create 2022-08-09 22:35
 */
public class ThreadTest {

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5,
            20,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("start......12");
//        CompletableFuture.runAsync(()->{
//            System.out.println(Thread.currentThread().getId());
//            System.out.println("result"+10/2);
//        }, executor);

        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getId());
                    int i = 10 / 10;
                    System.out.println(i);
                    return i;
                },
                executor);

        CompletableFuture<Integer> future02= CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getId());
                    int i = 10 / 5;
                    System.out.println(i);
                    return i;
                },
                executor);

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(future01, future02);
//        voidCompletableFuture.get();



    }


}
