package com.diandi.klob.injecteventbus.annotation;

import com.diandi.klob.injecteventbus.Subscription;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 20:20 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public interface Poster {
    void enqueue(Subscription subscription,Object event);
}
