package com.diandi.klob.injecteventbus;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 09:38 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class EventBusException extends RuntimeException {
    private static final long serialVersionUID=-2912559384646531480L;

    public EventBusException(String detailMessage) {
        super(detailMessage);
    }

    public EventBusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EventBusException(Throwable throwable) {
        super(throwable);
    }
}
