package com.duxl.baselib.rx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * SimpleSubscriber
 * create by duxl 2021/6/17
 */
public class SimpleSubscriber<T> implements Subscriber<T> {

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
