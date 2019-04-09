package com.tzpt.cloudlibrary.modle.remote.newdownload.core;

/**
 * Created by Administrator on 2018/8/3.
 */

public abstract class NamedRunnable implements Runnable{

    protected final String name;

    public NamedRunnable(String name) {
        this.name = name;
    }

    @Override
    public final void run() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name);
        try {
            execute();
        } catch (InterruptedException e) {
            interrupted(e);
        } finally {
            Thread.currentThread().setName(oldName);
            finished();
        }
    }

    protected abstract void execute() throws InterruptedException;

    protected abstract void interrupted(InterruptedException e);

    protected abstract void finished();
}
