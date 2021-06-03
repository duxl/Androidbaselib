package com.duxl.baselib.utils;

import android.util.Log;

import androidx.annotation.IntRange;

import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.BuildConfig;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;

/**
 * <pre>
 * 多任务标记工具类，可用于多个异步操作是否都执行完毕的判断
 * 举例：多个异步接口调用前显示dialog加载匡，所有接口都调用完毕后取消dialog
 * 这个工具类就可以用来记录这些接口是否都调用完毕，哪些已经调用完毕了
 * <pre/><br /><br />
 * create by duxl 2020/4/16
 */
public class MultiTaskMarkUtil {

    private OnAllCompleteListener mOnAllCompleteListener;
    private int mTaskCount;
    private int mCompleteFlag;

    private final String TAG = "MultiTaskMarkUtil";

    /**
     * @param taskCount 总的任务个数，必须大于等于1
     * @param listener  所有任务完毕回调监听
     */
    public MultiTaskMarkUtil(@IntRange(from = 1) int taskCount, OnAllCompleteListener listener) {
        this.mTaskCount = taskCount;
        this.mOnAllCompleteListener = listener;
    }

    /**
     * 设置某个任务已经完成
     *
     * @param taskIndex 第几的个任务，必须大于等于1 且小于等于taskCount(构造函数传的总的任务个)
     */
    public void setComplete(@IntRange(from = 1) int taskIndex) {
        if (taskIndex < 1 || taskIndex > mTaskCount) {
            throw new IllegalArgumentException("taskIndex必须大于等于1 且小于等于" + mTaskCount + "，当前传入的是" + taskIndex);
        }
        taskIndex--;
        mCompleteFlag |= (1 << taskIndex);
        isLoadComplete();
    }

    /**
     * 重置所有任务都未完成
     */
    public void resetComplete() {
        mCompleteFlag = 0;
    }

    public interface OnAllCompleteListener {
        void onAllComplete();
    }

    private synchronized void isLoadComplete() {
        if (BaseApplication.getInstance().getGlobalHttpConfig().isDEBUG()) {
            printCompleteTask();
        }
        if (mCompleteFlag == ((1 << mTaskCount) - 1)) { // 所有接口解决加载完毕
            if (mOnAllCompleteListener != null) {
                mOnAllCompleteListener.onAllComplete();
                if (BaseApplication.getInstance().getGlobalHttpConfig().isDEBUG()) {
                    Log.i(TAG, "已完成Tasks: ----- All -----");
                }
            }
        }
    }

    private void printCompleteTask() {
        char[] chars = Integer.toBinaryString(mCompleteFlag).toCharArray();
        String msg = "已完成Tasks：";
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == '1') {
                msg += (chars.length - i) + "、";
            }
        }
        Log.i(TAG, msg);
    }

    public static <T> ObservableTransformer<T, T> compose(MultiTaskMarkUtil markUtil, @IntRange(from = 1) int taskIndex) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .doOnTerminate(() -> setComplete())
                        .doOnDispose(() -> setComplete());
            }

            void setComplete() {
                if (taskIndex != 0) {
                    markUtil.setComplete(taskIndex);
                }
            }
        };
    }

}
