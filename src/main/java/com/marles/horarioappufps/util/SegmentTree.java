package com.marles.horarioappufps.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Represents a Lazy Propagation Max Segment Tree with default value of 0. <br>
 * Allows Range Update and Range Query
 */
@Slf4j
public class SegmentTree {

    private static int neutro = -1;
    private STNode r;

    public SegmentTree(int n) {
        this.r = new STNode(0, n - 1);
    }

    public void update(int i, int j, int x) {
        this.r.update(i, j, x);
    }

    public int query(int i, int j) {
        return this.r.query(i, j);
    }

    static class STNode {
        int data, lazy;
        STNode left, right;
        int L, R;

        public STNode(int L, int R) {
            this.L = L;
            this.R = R;
            this.data = this.lazy = neutro;
            if (L == R) {
                return;
            }
            int m = (L + R) >> 1;
            this.left = new STNode(L, m);
            this.right = new STNode(m + 1, R);
        }

        public void propagate(int val) {
            if(val == neutro){
                return;
            }
            this.data = val;
            this.lazy = neutro;
            if (L == R) {
                return;
            }
            this.left.propagate(val);
            this.right.propagate(val);
        }

        public void update(int i, int j, int val) {
            propagate(this.lazy);
            if (i > R || j < L) {
                return;
            }
            if (i <= L && j >= R) {
                propagate(val);
                return;
            }
            this.left.update(i, j, val);
            this.right.update(i, j, val);
            this.data = Math.max(this.left.data, this.right.data);
        }

        public int query(int i, int j) {
            propagate(this.lazy);
            if (i > R || j < L) {
                return neutro;
            }
            if (i <= L && j >= R) {
                return this.data;
            }
            int lf = this.left.query(i, j);
            int rg = this.right.query(i, j);
            return Math.max(lf, rg);
        }

    }
}
