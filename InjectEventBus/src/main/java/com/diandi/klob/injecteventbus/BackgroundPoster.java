package com.diandi.klob.injecteventbus;


import com.diandi.klob.injecteventbus.annotation.Poster;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 20:17 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class BackgroundPoster implements Poster, Runnable {
    private final PendingPostQueue mQueue;
    private final EventBus mEventBus;
    private volatile boolean isExecutorRunning;

    public BackgroundPoster(EventBus eventBus) {
        mEventBus = eventBus;
        mQueue = new PendingPostQueue();
    }


    @Override
    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            mQueue.enqueue(pendingPost);
            if (!isExecutorRunning) {
                isExecutorRunning = true;
                mEventBus.getExecutorService().execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    PendingPost pendingPost = mQueue.poll(1000);
                    if (pendingPost == null) {
                        synchronized (this) {
                            pendingPost = mQueue.poll();
                            if (pendingPost == null) {
                                isExecutorRunning = false;
                                return;
                            }
                        }
                    }
                    mEventBus.invokeSubscriber(pendingPost);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                EventBusDebuger.w(EventBus.TAG, Thread.currentThread().getName() + " was interrupted", e);
            }
        } finally {
            isExecutorRunning = false;
        }
    }
}
