public class Test {
    public static void main(String[] args){
        ThreadPool threadPool = new ThreadPool(2, 2, 3, TimeUnit.SECONDS);
        for (int i = 0; i<6; i++){
            threadPool.Execute(()->{
                System.out.println("working..." +Thread.currentThread().getName());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    },
                    (queue, task)->System.out.println("Queue is full...exit"));
        }
    }
}
