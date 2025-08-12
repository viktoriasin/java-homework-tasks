public class Test {
    public static void main(String[] args) throws InterruptedException {
        String firstThreadName = "First thread";
        Job task = new Job(firstThreadName);
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.setName(firstThreadName);
        thread2.setName("Second thread");
        thread1.start();
        thread2.start();
        Thread.sleep(100_000);
        thread1.interrupt();
        thread2.interrupt();
    }
}
