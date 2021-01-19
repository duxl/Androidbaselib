package com.duxl.baselib.rx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.android.ActivityEvent;
import com.trello.rxlifecycle4.android.FragmentEvent;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * LifecycleTransformer
 * create by duxl 2020/8/16
 */
public class LifecycleTransformer<T> implements ObservableTransformer<T, T> {

    private LifecycleProvider mLifecycleProvider;

    public LifecycleTransformer(LifecycleProvider lifecycleProvider) {
        this.mLifecycleProvider = lifecycleProvider;
    }

    @Override
    public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        if (mLifecycleProvider instanceof AppCompatActivity) {
            return upstream
                    .subscribeOn(Schedulers.io())
                    .compose(mLifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

        } else if (mLifecycleProvider instanceof Fragment) {
            return upstream
                    .subscribeOn(Schedulers.io())
                    .compose(mLifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return null;
    }
}
