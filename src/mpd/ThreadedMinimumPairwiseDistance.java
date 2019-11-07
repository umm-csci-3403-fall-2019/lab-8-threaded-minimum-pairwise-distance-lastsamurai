package mpd;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.awt.windows.ThemeReader;
import sun.print.SunMinMaxPage;

import java.lang.Thread;

public class ThreadedMinimumPairwiseDistance implements MinimumPairwiseDistance {

    private final int NUM_THREADS = 4;


    public ThreadedMinimumPairwiseDistance() {

    }

    @Override
    public long minimumPairwiseDistance(int[] values) throws InterruptedException {

        Thread[] threads = new Thread[NUM_THREADS];

        int size = values.length / 2;

        MinimumResult result = new MinimumResult();

        SectionThreading range1 = new SectionThreading(result, values, 0, size, 0, size);
        SectionThreading range2 = new SectionThreading(result, values, size, size * 2, 0, size);
        SectionThreading range3 = new SectionThreading(result, values, size, size * 2, 0, size);
        SectionThreading range4 = new SectionThreading(result, values, size, size * 2, size, size * 2);

        threads[0] = new Thread(range1);
        threads[1] = new Thread(range2);
        threads[2] = new Thread(range3);
        threads[3] = new Thread(range4);

        for (int k = 0; k < NUM_THREADS; k++) {
            threads[k].start();
        }

        for (int k = 0; k < NUM_THREADS; k++) {
            threads[k].join();
        }


        return result.getResult();
    }

    public class SectionThreading implements Runnable {

        private MinimumResult result;
        private int begin;
        private int end;
        private int i;
        private int j;
        private int[] values;

        public SectionThreading(MinimumResult result, int[] values, int begin, int i, int j, int end) {
            this.result = result;
            this.values = values;
            this.begin = begin;
            this.i = i;
            this.j = j;
            this.end = end;
        }
        @Override
        public void run() {
            long coombyya = Integer.MAX_VALUE;
            for (int index = begin; index < i; ++index) {
                for (int index2 = j; index2 < end; ++index2) {
                    long diff = Math.abs(values[index] - values[index2]);
                    if (diff < coombyya) {
                        result.setResult(coombyya);
                    }
                }
            }
        }
    }



    public class MinimumResult {

        private long result = 10000000;
        private long prior;

        public long getResult() {
            return result;
        }

        public synchronized void setResult(long result) {
            if (this.result == 10000000) {
                this.result = result;

            }
            if (prior > result) {
                this.prior = result;
                this.result = result;

            }
        }
    }
}

