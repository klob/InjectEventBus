package com.diandi.klob.injecteventbus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-03  .
 * *********    Time : 15:54 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class EventBusBuilder {
    private final static ExecutorService sExecutorService = Executors.newCachedThreadPool();


    boolean sendNoSubscriberEvent = true;
    boolean sendSubscriberEvent = true;

    boolean throwSubscriberException = false;

    boolean needInnerClassEvent=true;

    boolean eventInheritance = true;

    ExecutorService executorService =sExecutorService;
    List<Class<?>> mSkipMethodVerificationForClasses;


    public EventBusBuilder setSendNoSubscriberEvent(boolean sendNoSubscriberEvent) {
        this.sendNoSubscriberEvent = sendNoSubscriberEvent;
        return this;
    }

    public EventBusBuilder setSendSubscriberEvent(boolean sendSubscriberEvent) {
        this.sendSubscriberEvent = sendSubscriberEvent;
        return this;
    }

    public EventBusBuilder setThrowSubscriberException(boolean throwSubscriberException) {
        this.throwSubscriberException = throwSubscriberException;
        return this;
    }

    public EventBusBuilder setEventInheritance(boolean eventInheritance) {
        this.eventInheritance = eventInheritance;
        return this;
    }

    public EventBusBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public EventBusBuilder setSkipMethodVerificationForClasses(List<Class<?>> skipMethodVerificationForClasses) {
        if(mSkipMethodVerificationForClasses==null)
        {
            mSkipMethodVerificationForClasses=new ArrayList<>();
        }
        mSkipMethodVerificationForClasses = skipMethodVerificationForClasses;
        return this;
    }

    public EventBusBuilder setNeedInnerClassEvent(boolean needInnerClassEvent) {
        this.needInnerClassEvent = needInnerClassEvent;
        return this;
    }

    public EventBus build()
    {
        return new EventBus(this);
    }
}
