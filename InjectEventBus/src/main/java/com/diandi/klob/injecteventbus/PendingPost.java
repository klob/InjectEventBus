package com.diandi.klob.injecteventbus;

import java.util.ArrayList;
import java.util.List;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 13:38 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class PendingPost {

    private final static List<PendingPost> sPendingPostPool = new ArrayList<>();

    public Object event;
    public Subscription subscription;
    public PendingPost next;

    public PendingPost(Object event, Subscription subscription) {
        this.event = event;
        this.subscription = subscription;
    }

    static PendingPost obtainPendingPost(Subscription subscription, Object event) {
        synchronized (sPendingPostPool) {
            int size = sPendingPostPool.size();
            if (size > 0) {
                PendingPost pendingPost = sPendingPostPool.remove(size - 1);
                pendingPost.event = event;
                pendingPost.subscription = subscription;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new PendingPost(event, subscription);

    }

    static void releasePendingPost(PendingPost pendingPost) {
        pendingPost.event = null;
        pendingPost.subscription = null;
        pendingPost.next = null;
        synchronized (sPendingPostPool) {
            if (sPendingPostPool.size() < 10000) {
                sPendingPostPool.add(pendingPost);
            }
        }


    }
}
