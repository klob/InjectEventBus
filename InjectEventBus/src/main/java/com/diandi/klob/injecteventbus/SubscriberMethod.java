package com.diandi.klob.injecteventbus;

import com.diandi.klob.injecteventbus.annotation.ThreadMode;

import java.lang.reflect.Method;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-03  .
 * *********    Time : 21:23 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class SubscriberMethod {
    Method method;
    ThreadMode threadMode;
    Class<?> eventType;
    String methodName;

    public SubscriberMethod(Method method, ThreadMode threadMode, Class<?> eventType) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SubscriberMethod) {
            checkMethodName();
            SubscriberMethod otherSubcriberMethod = (SubscriberMethod) other;
            return methodName.equals(otherSubcriberMethod.methodName);
        } else {
            return false;
        }
    }

    private synchronized void checkMethodName() {
        if (methodName == null) {
            StringBuilder builder = new StringBuilder(64);
            builder.append(method.getDeclaringClass().getName());
            builder.append('#').append(method.getName());
            builder.append('(').append(eventType.getName());
            methodName = builder.toString();
        }
    }


    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public String toString() {
        return "SubscriberMethod{" +
                "method=" + method +
                ", threadMode=" + threadMode +
                ", eventType=" + eventType +
                '}';
    }
}

