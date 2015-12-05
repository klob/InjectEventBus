package com.diandi.klob.injecteventbus.annotation;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-03  .
 * *********    Time : 14:48 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
/**
 * Each event handler method has a thread mode, which determines in which thread the method is to be called by EventBus.
 * EventBus takes care of threading independently from the posting thread.
 *
 */
public enum ThreadMode {
    /**
     * Subscriber will be called in the same thread, which is posting the event. This is the default. Event delivery
     * implies the least overhead because it avoids thread switching completely. Thus this is the recommended mode for
     * simple tasks that are known to complete is a very short time without requiring the main thread. Event handlers
     * using this mode must return quickly to avoid blocking the posting thread, which may be the main thread.
     */
    PostThread,

    /**
     * Subscriber will be called in Android's main thread (sometimes referred to as UI thread). If the posting thread is
     * the main thread, event handler methods will be called directly. Event handlers using this mode must return
     * quickly to avoid blocking the main thread.
     */
    MainThread,

    /**
     * Subscriber will be called in a background thread. If posting thread is not the main thread, event handler methods
     * will be called directly in the posting thread. If the posting thread is the main thread, EventBus uses a single
     * background thread, that will deliver all its events sequentially. Event handlers using this mode should try to
     * return quickly to avoid blocking the background thread.
     */
    BackgroundThread,

    /**
     * Event handler methods are called in a separate thread. This is always independent from the posting thread and the
     * main thread. Posting events never wait for event handler methods using this mode. Event handler methods should
     * use this mode if their execution might take some time, e.g. for network access. Avoid triggering a large number
     * of long running asynchronous handler methods at the same time to limit the number of concurrent threads. EventBus
     * uses a thread pool to efficiently reuse threads from completed asynchronous event handler notifications.
     */
    Async
}