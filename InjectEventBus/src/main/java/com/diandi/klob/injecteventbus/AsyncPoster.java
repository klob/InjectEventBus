package com.diandi.klob.injecteventbus;

import com.diandi.klob.injecteventbus.annotation.Poster;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 20:33 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class AsyncPoster implements Runnable, Poster {
    private final PendingPostQueue mQueue;
    private final EventBus mEventBus;

    public AsyncPoster(EventBus eventBus) {
        mEventBus = eventBus;
        mQueue = new PendingPostQueue();
    }

    @Override
    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        mQueue.enqueue(pendingPost);
        mEventBus.getExecutorService().execute(this);

    }

    @Override
    public void run() {
        PendingPost pendingPost = mQueue.poll();
        if (pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        mEventBus.invokeSubscriber(pendingPost);
    }
}
