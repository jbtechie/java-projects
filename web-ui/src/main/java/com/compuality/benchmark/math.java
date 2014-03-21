package com.compuality.benchmark;

import com.yammer.metrics.core.Clock;

import java.util.Random;

public class math {
    public static void main(String[] args) {
        while (true) {
            test();
        }
    }

    private static void test() {
        Random rand = new Random();
        Clock clock = Clock.defaultClock();
        long start = clock.tick();
        long limit = 1_000_000_000; //10^9
        long value = 0;
        for(long i = 0; i < limit; ++i){}
        final double t1 = (clock.tick() - start)/1e6;
        start = clock.tick();
        for(long j = 0; j < limit; ++j) {
            value = rand.nextInt();
        }
        final double t2 = (clock.tick() - start)/1e6;
        start = clock.tick();
        for(long k = 0; k < limit; ++k) {
            value = value + (new Long(k));
        }
        final double t3 = (clock.tick() - start)/1e6;
        System.out.print(t1 + " " + t2 + " " + t3 + " " + value + "\n");
    }
}
