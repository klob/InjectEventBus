package com.diandi.klob.injecteventbus.event;

import com.diandi.klob.injecteventbus.EventBus;
import com.diandi.klob.injecteventbus.annotation.Event;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 12:57 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class SubscriberExceptionEvent implements Event{

    public final EventBus eventBus;
    public final Throwable throwable;
    public final Object causingEvent;
    public final Object causingSubscriber;

    public SubscriberExceptionEvent(EventBus eventBus, Throwable throwable, Object causingEvent, Object causingSubscriber) {
        this.eventBus = eventBus;
        this.throwable = throwable;
        this.causingEvent = causingEvent;
        this.causingSubscriber = causingSubscriber;
    }
}
