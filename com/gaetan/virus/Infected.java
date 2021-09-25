package com.gaetan.virus;

public final class Infected {
    private final int x;
    private final int y;

    public Infected(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}
