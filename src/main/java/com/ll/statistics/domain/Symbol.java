package com.ll.statistics.domain;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum Symbol {

    S1,
    S2,
    S3,
    S4,
    S5,
    S6,
    S7,
    S8,
    S9,
    S10;

    private final Lock symbolLock = new ReentrantLock();

    public void lock() {
        symbolLock.lock();
    }

    public void unlock() {
        symbolLock.unlock();
    }
}
