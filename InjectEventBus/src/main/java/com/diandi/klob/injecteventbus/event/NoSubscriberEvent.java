package com.diandi.klob.injecteventbus.event;

import com.diandi.klob.injecteventbus.EventBus;
import com.diandi.klob.injecteventbus.annotation.Event;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 12:55 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class NoSubscriberEvent implements Event {
    public final EventBus eventBus;
    public final Object originalEvent;

    public NoSubscriberEvent(EventBus eventBus, Object originalEvent) {
        this.eventBus = eventBus;
        this.originalEvent = originalEvent;
    }
}
