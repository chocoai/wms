package com.xyy.wms.outbound.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;


public class Test {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		testThenApply();//(等待并转化future)
		testThenAccept();//future完成处理,可获取结果(监听future完成)
		testThenRun();//future完成处理(监听future完成)
		testThenCompose();//两个future彼此依赖串联在一起
		testThenCombine();//thenCombine用于组合两个并发的任务,产生新的future有返回值
		testThenAcceptBoth();//thenCombine用于组合两个并发的任务,future无返回值
	}
	/**
	 * thenApply(等待并转化future)
	 */
    public static void testThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        });

        CompletableFuture<Integer> f2 = f1.thenApply(new Function<String, Integer>() {
            @Override
            public Integer apply(String t) {
            	System.out.println(t);
                return Integer.valueOf(t.length());
            }
        });

        CompletableFuture<Double> f3 = f2.thenApply(r -> r * 2.0);
        System.out.println(f3.get());
    }
    
    /**
     * future完成处理,可获取结果(监听future完成)
     */
    public static void testThenAccept(){
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        });
        f1.thenAccept(e -> {
            System.out.println("get result:"+e);
        });
    }

    /**
     * future完成处理(监听future完成)
     */
    public static void testThenRun(){
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        });
        f1.thenRun(new Runnable() {
            @Override
            public void run() {
                System.out.println("finished");
            }
        });
    }
    
    /**
     * 两个future彼此依赖串联在一起
     * 避免CompletableFuture<CompletableFuture<String>>这种
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void testThenCompose() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        }, executor);
        CompletableFuture<CompletableFuture<String>> f4 = f1.thenApply(Test::calculate);
        System.out.println("f4.get:"+f4.get().get());

        CompletableFuture<String> f5 = f1.thenCompose(Test::calculate);
        System.out.println("f5.get:"+f5.get());

        System.out.println(f1.get());
    }

    public static CompletableFuture<String> calculate(String input) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(input);
            return input + "---" + input.length();
        }, executor);
        return future;
    }
   
    /*
    * thenCombine用于组合两个并发的任务,产生新的future有返回值
    * @throws ExecutionException
    * @throws InterruptedException
    */
   public static void testThenCombine() throws ExecutionException, InterruptedException {
       CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
           try {
               System.out.println("f1 start to sleep at:"+System.currentTimeMillis());
               Thread.sleep(1000);
               System.out.println("f1 finish sleep at:"+System.currentTimeMillis());
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           return "zero";
       });
       CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
           try {
               System.out.println("f2 start to sleep at:"+System.currentTimeMillis());
               Thread.sleep(3000);
               System.out.println("f2 finish sleep at:"+System.currentTimeMillis());
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           return "hello";
       });

       CompletableFuture<String> reslutFuture =
               f1.thenCombine(f2, new BiFunction<String, String, String>() {

                   @Override
                   public String apply(String t, String u) {
                       System.out.println("f3 start to combine at:"+System.currentTimeMillis());
                       return t.concat(u);
                   }
               });
       System.out.println(reslutFuture.get());//zerohello
       System.out.println("finish combine at:"+System.currentTimeMillis());
   }
   /**
    * thenAcceptBoth用于组合两个并发的任务,产生新的future没有返回值
    * @throws ExecutionException
    * @throws InterruptedException
    */
   public static void testThenAcceptBoth() throws ExecutionException, InterruptedException {
       CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
           try {
               System.out.println("f1 start to sleep at:"+System.currentTimeMillis());
               TimeUnit.SECONDS.sleep(1);
               System.out.println("f1 stop sleep at:"+System.currentTimeMillis());
           } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
           return "zero";
       });
       CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
           try {
               System.out.println("f2 start to sleep at:"+System.currentTimeMillis());
               TimeUnit.SECONDS.sleep(3);
               System.out.println("f2 stop sleep at:"+System.currentTimeMillis());
           } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
           return "hello";
       });

       CompletableFuture<Void> reslutFuture = f1.thenAcceptBoth(f2, new BiConsumer<String, String>() {
           @Override
           public void accept(String t, String u) {
               System.out.println("f3 start to accept at:"+System.currentTimeMillis());
               System.out.println(t + " over");
               System.out.println(u + " over");
           }
       });

       System.out.println(reslutFuture.get());
       System.out.println("finish accept at:"+System.currentTimeMillis());

   }
}
