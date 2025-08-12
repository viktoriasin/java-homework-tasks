public class Job implements Runnable {
    final Object monitor = new Object();
    private final String firstThreadName;
    private boolean firstThreadIsStarted = false;

    public Job(String firstThreadName) {
        this.firstThreadName = firstThreadName;
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                processForward(1, 10);
                processBackward(10, 1);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processForward(int start, int end) throws InterruptedException {
        String name = Thread.currentThread().getName();
        synchronized (monitor) {
            if (!name.equals(firstThreadName)) {
                while (!firstThreadIsStarted) {
                    monitor.wait();
                }
            } else {
                firstThreadIsStarted = true;
            }
            for (int i = start; i <= end; i++) {
                System.out.printf("%s%s%d%n", name, ": ", i);
                sleep();
                monitor.notify();
                monitor.wait();
            }
        }
    }

    private void processBackward(int start, int end) throws InterruptedException {
        synchronized (monitor) {
            for (int i = start; i >= end; i--) {
                System.out.printf("%s%s%d%n", Thread.currentThread().getName(), ": ", i);
                sleep();
                monitor.notify();
                monitor.wait();
            }
        }
    }
}
