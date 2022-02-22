package com.xiaoyv.blueprint.rxbus;

import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableInternalHelper;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/05/08
 *     desc  :
 * </pre>
 */
public final class FlowableUtils {

    public static <T> Disposable subscribe(Flowable<T> flowable,
                                           Consumer<? super T> onNext,
                                           Consumer<? super Throwable> onError) {
        return subscribe(flowable,
                onNext, onError,
                Functions.EMPTY_ACTION,
                FlowableInternalHelper.RequestMax.INSTANCE
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> Disposable subscribe(Flowable<T> flowable,
                                            Consumer<? super T> onNext,
                                            Consumer<? super Throwable> onError,
                                            Action onComplete,
                                            Consumer<? super Subscription> onSubscribe) {
        Utils.requireNonNull(flowable, "flowable is null");
        Utils.requireNonNull(onNext, "onNext is null");
        Utils.requireNonNull(onError, "onError is null");
        Utils.requireNonNull(onComplete, "onComplete is null");
        Utils.requireNonNull(onSubscribe, "onSubscribe is null");

        MyLambdaSubscriber<T> ls = new MyLambdaSubscriber<>(onNext, onError, onComplete, onSubscribe);
        flowable.subscribe(ls);
        return ls;
    }
}
