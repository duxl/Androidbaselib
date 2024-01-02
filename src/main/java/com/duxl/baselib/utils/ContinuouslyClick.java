package com.duxl.baselib.utils;

/**
 * <pre>
 * 连续点击次数累计，比如连续点击10次进入debug页面，
 * 使用示例：
 *
 * mBinding.btn.setOnClickListener {
 *      val clickCount: Int = mContinuouslyClick.addCount()
 *      if (clickCount == 10) {
 *          open(DebugActivity.class)
 *      }
 * }
 * <pre/>
 */
public class ContinuouslyClick {

    // 当前点击的次数
    private int currentClickCount = 0;

    // 重置间隔时长
    private long resetInterval = 1000;

    /**
     * 设置重置间隔时长，默认1秒后重置点击次数为0
     *
     * @param resetInterval
     */
    public void setResetInterval(long resetInterval) {
        this.resetInterval = resetInterval;
        this.currentClickCount = 0;
    }

    /**
     * 获取当前点击次数
     *
     * @return
     */
    public int getCurrentClickCount() {
        return currentClickCount;
    }

    /**
     * 获取重置间隔时间
     *
     * @return
     */
    public long getResetInterval() {
        return resetInterval;
    }

    /**
     * 增加点击次数
     *
     * @return 返回当前累加的次数
     */
    public int addCount() {
        Dispatch.I.removeCallbacks(resetRunnable);
        Dispatch.I.postDelayed(resetRunnable, resetInterval);
        return ++currentClickCount;
    }

    private Runnable resetRunnable = () -> currentClickCount = 0;
}
