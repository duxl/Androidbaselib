package com.duxl.baselib.widget.dialog;

/**
 * 加载框接口
 * create by duxl 2021/3/25
 */
public interface IProgressDialog {

    /**
     * 显示加载框
     *
     * @param msg 提示信息
     */
    void show(CharSequence msg);

    /**
     * 隐藏加载框
     */
    void dismiss();

    /**
     * cancelTouchOutside 点击加载框外是否消失
     *
     * @return
     */
    boolean cancelTouchOutside();

    /**
     * 弹框是否可取消（指的是用户点击手机的返回键）
     *
     * @return
     */
    boolean cancelable();
}
