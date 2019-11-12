package mpd;

import java.lang.Thread;

public class ThreadedMinimumPairwiseDistance implements MinimumPairwiseDistance {

    private final int NUM_THREADS = 4;


    public ThreadedMinimumPairwiseDistance() {

    }

    @Override
    public long minimumPairwiseDistance(int[] values) throws InterruptedException {

        // Variables
        Thread[] threads = new Thread[NUM_THREADS];
        int size = values.length / 2;
        MinimumResult result = new MinimumResult();

        // Left Triangle Runnable call
        NormalSectionThreading range1 = new NormalSectionThreading(result, values, 0, size);
        // Middle Triangle Runnable call
        InvertedSectionThreading range2 = new InvertedSectionThreading(result, values, size, size * 2);
        // Right Triangle Runnable call
        RightSectionThreading range3 = new RightSectionThreading(result, values, size, size * 2);
        // Top Triangle Runnable call
        NormalSectionThreading range4 = new NormalSectionThreading(result, values, size, size * 2);

        // Creating all Threads
        threads[0] = new Thread(range1);
        threads[1] = new Thread(range2);
        threads[2] = new Thread(range3);
        threads[3] = new Thread(range4);

        // Loops for starting threads and joining the threads
        for (int k = 0; k < NUM_THREADS; k++) {
            threads[k].start();
        }

        for (int k = 0; k < NUM_THREADS; k++) {
            threads[k].join();
        }


        return result.getResult();
    }

    public class NormalSectionThreading implements Runnable {

        private MinimumResult result;
        private int begin;
        private int end;
        private int[] values;

        public NormalSectionThreading(MinimumResult result, int[] values, int begin, int end) {
            this.result = result;
            this.values = values;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = begin; i < end ; ++i) {
                for (int j = begin; j < i; ++j) {
                    long diff = Math.abs(values[i] - values[j]); // checks distance between two integers
                    if (diff < result.getResult()) { // compares the new distance to the one that we have saved
                        result.setResult(diff); // if true, set the new distance as the result
                    }
                }
            }
        }
    }

    public class InvertedSectionThreading implements Runnable {

        private MinimumResult result;
        private int begin;
        private int end;
        private int[] values;

        public InvertedSectionThreading(MinimumResult result, int[] values, int begin, int end) {
            this.result = result;
            this.values = values;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            for (int j = 0; j < begin ; ++j) {
                for (int i = begin; i <= j+begin; ++i) {
                    long diff = Math.abs(values[j] - values[i]);
                    if (diff < result.getResult()) {
                        result.setResult(diff);
                    }
                }
            }
        }
    }

    public class RightSectionThreading implements Runnable {

        private MinimumResult result;
        private int begin;
        private int end;
        private int[] values;

        public RightSectionThreading(MinimumResult result, int[] values, int begin, int end) {
            this.result = result;
            this.values = values;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = begin; i < end ; ++i) {
                for (int j = 0; j+begin < i; ++j) {
                    long diff = Math.abs(values[i] - values[j]);
                    if (diff < result.getResult()) {
                        result.setResult(diff);
                    }
                }
            }
        }
    }

    public class MinimumResult {

        // sets the first result as the largest value, base case
        private long result = Integer.MAX_VALUE;


        public long getResult() {
            return result;
        }

        public synchronized void setResult(long newResult) {
            if (result > newResult) { // if the old result is larger then the new, set old as the new
                result = newResult;
            }
        }
    }
}

