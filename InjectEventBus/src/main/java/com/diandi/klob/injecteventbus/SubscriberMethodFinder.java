package com.diandi.klob.injecteventbus;


import com.diandi.klob.injecteventbus.annotation.Receiver;
import com.diandi.klob.injecteventbus.annotation.ThreadMode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-03  .
 * *********    Time : 20:03 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class SubscriberMethodFinder {
    private static final int BRIDGE = 0X40;
    private static final int SYNTHETIC = 0X1000;
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    private static final Map<Class<?>, List<SubscriberMethod>> mMethodCache = new HashMap<>();
    private static Map<Class<?>, Class<?>> mSkipMethodVerificationClasses;
    String TAG = "SubcriberMethodFinder";

    public SubscriberMethodFinder(List<Class<?>> skipMethodVerificationClasses) {
        mSkipMethodVerificationClasses = new ConcurrentHashMap<>();
        if (skipMethodVerificationClasses != null) {
            for (Class<?> clazz : skipMethodVerificationClasses) {
                mSkipMethodVerificationClasses.put(clazz, clazz);
            }
        }
    }

    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        List<SubscriberMethod> subscriberMethods;
        synchronized (mMethodCache) {
            subscriberMethods = mMethodCache.get(subscriberClass);
        }
        if (subscriberMethods != null) {
            return subscriberMethods;
        }
        subscriberMethods = new ArrayList<>();
        Class<?> clazz = subscriberClass;
        HashMap<String, Class> eventTypeFound = new HashMap<>();
        StringBuilder methodKeyBuilder = new StringBuilder();
        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            try {
                Method[] methods = clazz.getDeclaredMethods();
                filterSubscriberMethods(subscriberMethods, eventTypeFound, methodKeyBuilder, methods);
            } catch (Exception e) {
                e.printStackTrace();

                Method[] methods = clazz.getMethods();
                subscriberMethods.clear();
                eventTypeFound.clear();
                filterSubscriberMethods(subscriberMethods, eventTypeFound, methodKeyBuilder, methods);
                break;

            }
            clazz = clazz.getSuperclass();
        }
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass + " has no public methods Event annotation ");
        } else {
            synchronized (mMethodCache) {
                mMethodCache.put(subscriberClass, subscriberMethods);
            }
        }

        return subscriberMethods;
    }

    private void filterSubscriberMethods(List<SubscriberMethod> subscriberMethods, HashMap<String, Class> eventTypesFound, StringBuilder methodKeyBuilder, Method[] methods) {
        int noParemterIndicator = 0;
        for (Method method : methods) {
            Receiver event = method.getAnnotation(Receiver.class);
            if (event != null) {
                String methodName = method.getName();
                int modifiers = method.getModifiers();
                Class<?> methodClass = method.getDeclaringClass();

                if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                    ThreadMode mode = event.threadMode();
                    Class<?> eventType = null;
                    String methodKey;

                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        if (noParemterIndicator == 0) {
                            eventType = EmptyParameter.class;
                            noParemterIndicator++;
                        } else {
                            throw new EventBusException("Subscriber " + methodClass + " can only have one no parameter method ");
                        }

                    } else if (parameterTypes.length == 1) {
                        eventType = parameterTypes[0];
                    } else {
                        throw new EventBusException("Subscriber " + methodClass + "." + method + " can only have no more than one parameter . if you need user more parameters ,use class wrap or bundle instead");
                    }
                    methodKeyBuilder.setLength(0);
                    methodKeyBuilder.append(methodName);
                    methodKeyBuilder.append('>').append(eventType.getName());
                    methodKey = methodKeyBuilder.toString();


                    Class methodClassOld = eventTypesFound.put(methodKey, methodClass);
                    if (methodClassOld == null || methodClassOld.isAssignableFrom(methodClass)) {
                        subscriberMethods.add(new SubscriberMethod(method, mode, eventType));
                    } else {
                        eventTypesFound.put(methodKey, methodClassOld);
                    }
                } else if (!mSkipMethodVerificationClasses.containsKey(methodClass)) {
                    EventBusDebuger.d(EventBus.TAG, "skipping method( not public ,static or abstract ):" + methodClass + "." + methodName);
                }

            }
        }


    }

}
