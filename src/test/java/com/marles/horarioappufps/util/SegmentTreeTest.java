package com.marles.horarioappufps.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SegmentTreeTest {
    private Random random = new Random();
    private final int N = 100;

    @Test
    public void test_preset_1(){
        SegmentTree segmentTree = new SegmentTree(N);
        segmentTree.update(0, 24, 1);
        segmentTree.update(25, 49, 2);
        segmentTree.update(50, 74, 3);
        segmentTree.update(75, 99, 4);

        assertEquals(1, segmentTree.query(10, 15));
        assertEquals(2, segmentTree.query(30, 35));
        assertEquals(3, segmentTree.query(60, 65));
        assertEquals(4, segmentTree.query(80, 85));
    }

    @Test
    public void test_preset_2(){
        SegmentTree segmentTree = new SegmentTree(N);
        segmentTree.update(0, 49, 1);
        segmentTree.update(50, 99, 2);

        assertEquals(2, segmentTree.query(0, 99));
    }

    @Test
    public void test_preset_3(){
        SegmentTree segmentTree = new SegmentTree(N);
        segmentTree.update(0, 24, 1);
        segmentTree.update(10, 25, 2);

        assertEquals(2, segmentTree.query(12, 12));
    }

    @Test
    public void test_preset_4() {
        SegmentTree segmentTree = new SegmentTree(N);

        assertEquals(-1, segmentTree.query(0, 99));
    }

    @Test
    public void test_random(){
        final int TIMES = 20;
        final int MAX_N = 1000;
        for(int time = 0; time < TIMES; ++time){
            int n = random.nextInt(1,MAX_N);
            log.info("N = {}", n);
            SegmentTree segmentTree = new SegmentTree(n);
            int[] array = new int[n];
            for(int i = 0; i < n; ++i)
                array[i] = -1;
            int q = random.nextInt(n);
            log.info("updates = {}", q);
            for(int query = 0; query < q; ++query){
                int i = random.nextInt(n-1);
                int j = random.nextInt(i+1, n);
                int x = random.nextInt(1000);
                segmentTree.update(i, j, x);
                log.info("upd({},{},{})", i, j, x);
                for(int k = i; k <= j; ++k){
                    array[k] = x;
                }
            }

            q = random.nextInt(n);
            log.info("queries = {}", q);
            for(int query = 0; query < q; ++query){
                int i = random.nextInt(n-1);
                int j = random.nextInt(i+1, n);
                log.info("query({},{})", i, j);
                int ans = segmentTree.query(i, j);
                int ans2 = array[i];

                for(int k = i+1; k <= j; ++k){
                    ans2 = Math.max(ans2, array[k]);
                }
                log.info("segtree = {}  vs  array = {}", ans, ans2);
                assertEquals(ans, ans2);
            }

        }
    }
}
