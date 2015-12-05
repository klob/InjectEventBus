package com.diandi.klob.injecteventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.diandi.klob.injecteventbus.annotation.Poster;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 14:13 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class HandlerPoster extends Handler implements Poster {
    private final PendingPostQueue mQueue;
    private final int maxMillisInsideHandlerMessage;
    private final EventBus mEventBus;
    private boolean isHandlerActive;

    public HandlerPoster(int maxMillisInsideHandlerMessage, Looper looper, EventBus eventBus) {
        super(looper);
        this.maxMillisInsideHandlerMessage = maxMillisInsideHandlerMessage;
        this.mEventBus = eventBus;
        mQueue = new PendingPostQueue();
    }

    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            mQueue.enqueue(pendingPost);
            if (!isHandlerActive) {
                isHandlerActive = true;
                if (!sendMessage(obtainMessage())) {
                    throw new EventBusException("Could not send handler message ");
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        boolean rescheduled = false;
        try {
            long start = SystemClock.uptimeMillis();
            while (true) {
                PendingPost pendingPost = mQueue.poll();
                if (pendingPost == null) {
                    synchronized (this) {
                        pendingPost = mQueue.poll();
                        if (pendingPost == null) {
                            isHandlerActive = false;
                            return;
                        }
                    }
                }
                mEventBus.invokeSubscriber(pendingPost);
                long timeInMethod = SystemClock.uptimeMillis() - start;
                if (timeInMethod > maxMillisInsideHandlerMessage) {
                    if (!sendMessage(obtainMessage())) {
                        throw new EventBusException("Could not send handler message ");
                    }
                    rescheduled = true;
                    return;
                }

            }
        } finally {
            isHandlerActive = rescheduled;
        }

    }
}
