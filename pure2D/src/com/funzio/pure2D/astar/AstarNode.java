package com.funzio.pure2D.astar;

import java.util.Comparator;

import android.graphics.Point;

import com.funzio.pure2D.utils.Reusable;

/**
 * @author long.ngo
 */
public class AstarNode extends Point implements Reusable {

    public AstarNode parent = null;
    public int h = 0; // heuristic
    public int g = 1; // cost

    private int key;

    public static final Comparator<AstarNode> COMPARATOR = new Comparator<AstarNode>() {
        public int compare(final AstarNode left, final AstarNode right) {
            return (left.g + left.h) - (right.g + right.h);
        }
    };

    public AstarNode(final Point p) {
        this(p.x, p.y);
    }

    public AstarNode(final int x, final int y) {
        this.x = x;
        this.y = y;

        // generate the key for quick lookup
        this.key = generateKey(x, y);
    }

    public AstarNode(final int x, final int y, final int g, final int h) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;

        // generate the key for quick lookup
        this.key = generateKey(x, y);
    }

    @Override
    public void reset(final Object... params) {
        if (params.length >= 2) {
            x = (Integer) params[0];
            y = (Integer) params[1];
        } else {
            x = y = 0;
        }

        h = 0;
        g = 1;
        parent = null;

        // generate the key for quick lookup
        key = generateKey(x, y);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AstarNode) {
            final AstarNode node = (AstarNode) o;
            return x == node.x && y == node.y;
        } else {
            return super.equals(o);
        }
    }

    public int getKey() {
        return key;
    }

    @Override
    public AstarNode clone() {
        return new AstarNode(x, y, g, h);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + "), g=" + g + ", h=" + h;
    }

    protected static int generateKey(final int x, final int y) {
        return y * 100000 + x;
    }

}
