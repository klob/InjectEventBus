package com.diandi.klob.injecteventbus;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 10:15 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class Subscription {
    final Object subscriber;
    final SubscriberMethod subscriberMethod;
    final int priority;
    volatile boolean isActive;

    public Subscription(Object subscriber, SubscriberMethod subscriberMethod, int priority) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
        this.priority = priority;
        isActive = true;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Subscription) {
            Subscription otherSubscription = (Subscription) other;
            return subscriber == otherSubscription.subscriber && subscriberMethod.equals(otherSubscription.subscriberMethod);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return subscriber.hashCode();
    }
}

