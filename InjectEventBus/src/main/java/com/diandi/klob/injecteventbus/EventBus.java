package com.diandi.klob.injecteventbus;

import android.os.Looper;


import com.diandi.klob.injecteventbus.annotation.Event;
import com.diandi.klob.injecteventbus.event.NoSubscriberEvent;
import com.diandi.klob.injecteventbus.event.SubscriberExceptionEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-03  .
 * *********    Time : 14:01 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */

/**
 * EventBus is a central publish/subscribe event system for Android. Events are posted ({@link #post(Event)}) to the
 * bus, which delivers it to subscribers that have a matching handler method for the event type. To receive events,
 * subscribers must register themselves to the bus using {@link #register(Object)}. Once registered,
 * subscribers receive events until {@link #unregister(Object)} is called. By convention, event handling methods must
 * be named "onEvent", be public, return nothing (void), and have exactly one parameter (the event).
 */
public class EventBus {
    public static final String TAG = "EventBus";
    private static final EventBusBuilder sDefaultBuilder = new EventBusBuilder();

    private static final Map<Class<?>, List<Class<?>>> mEventTypesCache = new HashMap<>();
    private static EventBus sDefaultInstance;
    private final SubscriberMethodFinder mSubscriberMethodFinder;
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> mSubscriptionByType;
    private final Map<Object, List<Class<?>>> mTypesBySubscriber;

    private final ExecutorService mExecutorService;
    private final ThreadLocal<PostingThreadState> mCurrentPostingThreadState = new ThreadLocal<PostingThreadState>() {
        @Override
        protected PostingThreadState initialValue() {
            return new PostingThreadState();
        }
    };

    private final boolean mSendNoSubscriberEvent;
    private final boolean mEventInheritance;
    private final boolean mThrowSubscriberException;
    private final boolean mSendSubscriberExceptionEvent;

    private final HandlerPoster mMainThreadPoster;
    private final BackgroundPoster mBackgroundPoster;
    private final AsyncPoster mAsyncPoster;

    public EventBus() {
        this(sDefaultBuilder);
    }

    public EventBus(EventBusBuilder builder) {
        mSubscriberMethodFinder = new SubscriberMethodFinder(builder.mSkipMethodVerificationForClasses);
        mSubscriptionByType = new HashMap<>();
        mTypesBySubscriber = new HashMap<>();


        mExecutorService = builder.executorService;
        mEventInheritance = builder.eventInheritance;
        mSendNoSubscriberEvent = builder.sendNoSubscriberEvent;
        mSendSubscriberExceptionEvent = builder.sendSubscriberEvent;
        mThrowSubscriberException = builder.throwSubscriberException;


        mMainThreadPoster = new HandlerPoster(10, Looper.getMainLooper(), this);
        mBackgroundPoster = new BackgroundPoster(this);
        mAsyncPoster = new AsyncPoster(this);

    }

    public static EventBus getDefault() {
        if (sDefaultInstance == null) {
            synchronized (EventBus.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new EventBus();
                }
            }
        }
        return sDefaultInstance;
    }

    public static EventBusBuilder builder() {
        return new EventBusBuilder();
    }

    static void addInterfaces(List<Class<?>> eventTypes, Class<?>[] interfaces) {
        for (Class<?> interfaceClass : interfaces) {
            if (!eventTypes.contains(interfaceClass)) {
                eventTypes.add(interfaceClass);
                addInterfaces(eventTypes, interfaceClass.getInterfaces());
            }

        }
    }

    public void register(Object subscriber) {
        register(subscriber, 0);

    }

    public void register(Object subscriber, int priority) {
        List<SubscriberMethod> subscriberMethods = mSubscriberMethodFinder.findSubscriberMethods(subscriber.getClass());
        EventBusDebuger.v(TAG, subscriberMethods.toString());
        for (SubscriberMethod method : subscriberMethods) {
            subscribe(subscriber, method, priority);
        }

    }

    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod, int priority) {
        Class<?> eventType = subscriberMethod.eventType;
        CopyOnWriteArrayList<Subscription> subscriptions = mSubscriptionByType.get(eventType);
        Subscription newSubscription = new Subscription(subscriber, subscriberMethod, priority);
        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<>();
            mSubscriptionByType.put(eventType, subscriptions);
        } else {
            if (mSubscriptionByType.containsKey(eventType)) {
                throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to the event " + eventType);
            }
        }

        int size = subscriptions.size();
        for (int i = 0; i <= size; i++) {
            if (i == size || newSubscription.priority > subscriptions.get(i).priority) {
                subscriptions.add(i, newSubscription);
                break;
            }
        }

        List<Class<?>> subscriberEvents = mTypesBySubscriber.get(subscriber);
        if (subscriberEvents == null) {
            subscriberEvents = new ArrayList<>();
            mTypesBySubscriber.put(subscriber, subscriberEvents);
        }
        subscriberEvents.add(eventType);

    }

    public void post(Event event) {
        if (event == null) {
            throw new NullPointerException("The event is null");
        }
        PostingThreadState postingState = mCurrentPostingThreadState.get();
        List<Object> eventQueue = postingState.eventQueue;
        eventQueue.add(event);

        if (!postingState.isPosting) {
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if (postingState.isCanceled) {
                throw new EventBusException("Internal error . Abort state was not reset");
            }
            try {
                while (!eventQueue.isEmpty()) {
                    postSingleEvent(eventQueue.remove(0), postingState);
                }
            } finally {
                postingState.isPosting = false;
                postingState.isMainThread = false;
            }
        }
    }

    public void post(Class event) {
        PostingThreadState postingState = mCurrentPostingThreadState.get();
        List<Object> eventQueue = postingState.eventQueue;
        eventQueue.add(event);

        if (!postingState.isPosting) {
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if (postingState.isCanceled) {
                throw new EventBusException("Internal error . Abort state was not reset");
            }
            try {
                while (!eventQueue.isEmpty()) {
                    postSingleEvent(eventQueue.remove(0), postingState);
                }
            } finally {
                postingState.isPosting = false;
                postingState.isMainThread = false;
            }
        }
    }

    public synchronized void unregister(Object subscriber) {
        List<Class<?>> subscriberTypes = mTypesBySubscriber.get(subscriber);
        {
            if (subscriberTypes != null) {
                for (Class<?> eventType : subscriberTypes) {
                    unsubscribeByEventType(subscriber, eventType);
                }
                mTypesBySubscriber.remove(subscriber);
            } else {
                EventBusDebuger.w(TAG, "Subscriber to unregister was not registered before : " + subscriber.getClass());
            }
        }
    }

    private void unsubscribeByEventType(Object subscriber, Class<?> eventType) {
        List<Subscription> subscriptions = mSubscriptionByType.get(eventType);
        if (subscriptions != null) {
            int size = subscriptions.size();
            for (int i = 0; i < size; i++) {
                Subscription subscription = subscriptions.get(i);
                if (subscription.subscriber == subscriber) {
                    subscription.isActive = false;
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
            }
        }
    }


    private void postSingleEvent(Object event, PostingThreadState postState) throws Error {
        Class<?> eventClass;
        if (event instanceof Class) {
            eventClass = EmptyParameter.class;
        } else {
            eventClass = event.getClass();
        }

        boolean subscriptionFound = false;
        if (mEventInheritance) {
            List<Class<?>> eventTypes = lookupAllEventTypes(eventClass);
            int countTypes = eventTypes.size();
            for (int h = 0; h < countTypes; h++) {
                Class<?> clazz = eventTypes.get(h);
                subscriptionFound |= postSingleEventForEventType(event, postState, clazz);
            }
        } else {
            subscriptionFound = postSingleEventForEventType(event, postState, eventClass);
        }
        if (!subscriptionFound) {
            if (EventBusDebuger.isLoggable()) {
                EventBusDebuger.d(TAG, "No subscribers registered for event " + eventClass);
            }
            if (mSendNoSubscriberEvent && eventClass != NoSubscriberEvent.class && eventClass != SubscriberExceptionEvent.class) {
                post(new NoSubscriberEvent(this, event));
            }
        }


    }

    private boolean postSingleEventForEventType(Object event, PostingThreadState postingState, Class<?> eventClass) {

        CopyOnWriteArrayList<Subscription> subscriptions;
        synchronized (this) {
            subscriptions = mSubscriptionByType.get(eventClass);
        }
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                postingState.event = event;
                postingState.subscription = subscription;
                boolean aborted = false;
                try {
                    postToSubscription(subscription, event, postingState.isMainThread);
                    aborted = postingState.isCanceled;
                } finally {
                    postingState.event = null;
                    postingState.subscription = null;
                    postingState.isCanceled = false;
                }
                if (aborted) {
                    break;
                }
            }
            return true;

        }
        return false;
    }

    private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
        switch (subscription.subscriberMethod.threadMode) {
            case PostThread:
                invokeSubscriber(subscription, event);
                break;
            case MainThread:
                if (isMainThread) {
                    invokeSubscriber(subscription, event);
                } else {
                    mMainThreadPoster.enqueue(subscription, event);
                }
                break;
            case BackgroundThread:
                if (isMainThread) {
                    mBackgroundPoster.enqueue(subscription, event);
                } else {
                    invokeSubscriber(subscription, event);
                }
                break;
            case Async:
                mAsyncPoster.enqueue(subscription, event);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode :" + subscription.subscriberMethod.threadMode);
        }
    }

    void invokeSubscriber(PendingPost pendingPost) {
        Object event = pendingPost.event;
        Subscription subscription = pendingPost.subscription;
        PendingPost.releasePendingPost(pendingPost);
        if (subscription.isActive) {
            invokeSubscriber(subscription, event);
        }
    }

    private void invokeSubscriber(Subscription subscription, Object event) {
        try {
            if (event instanceof Class) {
                invokeNoParameterSubscriber(subscription, event);
            } else {
                subscription.subscriberMethod.method.invoke(subscription.subscriber, event);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            handleSubscriberException(subscription, event, e.getCause());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unexpected exception ", e);
        }
    }

    private void invokeNoParameterSubscriber(Subscription subscription, Object event) {
        for (Object o : mTypesBySubscriber.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            if (key.getClass().equals(event)) {
                try {
                    subscription.subscriberMethod.method.invoke(subscription.subscriber);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    handleSubscriberException(subscription, event, e.getCause());
                }
            }


        }

    }

    private void handleSubscriberException(Subscription subscription, Object event, Throwable cause) {
        if (event instanceof SubscriberExceptionEvent) {
            if (EventBusDebuger.isLoggable()) {
                EventBusDebuger.e(TAG, "SubscriberException subscriber " + subscription.subscriber.getClass() + " threw an exception ", cause);
                SubscriberExceptionEvent exceptionEvent = (SubscriberExceptionEvent) event;
                EventBusDebuger.e(TAG, "Initial event " + exceptionEvent.causingEvent + " caused exception " + exceptionEvent.causingSubscriber, exceptionEvent.throwable);
            }
        } else {
            if (mThrowSubscriberException) {
                throw new EventBusException("Invoking subscriber failed ", cause);
            }
            if (EventBusDebuger.isLoggable()) {
                EventBusDebuger.e(TAG, "Could not dispatch event :" + event.getClass() + " to subscriber class " + subscription.subscriber.getClass(), cause);
            }
            if (mSendSubscriberExceptionEvent) {
                SubscriberExceptionEvent exceptionEvent = new SubscriberExceptionEvent(this, cause, event, subscription.subscriber);
                post(exceptionEvent);
            }


        }
    }


    private List<Class<?>> lookupAllEventTypes(Class<?> eventClass) {
        synchronized (mEventTypesCache) {
            List<Class<?>> eventTypes = mEventTypesCache.get(eventClass);
            if (eventTypes == null) {
                eventTypes = new ArrayList<>();
                Class<?> clazz = eventClass;
                while (clazz != null) {
                    eventTypes.add(clazz);
                    addInterfaces(eventTypes, clazz.getInterfaces());
                    clazz = clazz.getSuperclass();

                }
                mEventTypesCache.put(eventClass, eventTypes);
            }
            return eventTypes;


        }
    }

    ExecutorService getExecutorService() {
        return mExecutorService;
    }

    final static class PostingThreadState {
        final List<Object> eventQueue = new ArrayList<>();
        boolean isPosting;
        boolean isMainThread;
        Subscription subscription;
        Object event;
        boolean isCanceled;

    }
}
