package com.diandi.klob.injecteventbus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-03  .
 * *********    Time : 14:23 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
//@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Receiver {

    int value() default 0;

    ThreadMode threadMode() default ThreadMode.PostThread;

}
