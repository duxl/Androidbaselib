package com.duxl.baselib.widget;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 要使TextView的超链接可点击，需要设置setMovementMethod方法
 * 常规设置TextView.setMovementMethod(LinkMovementMethod.getInstance());点击后链接一直会被选中
 * 改用TextView.setMovementMethod(RemoveSelectionLinkMovementMethod.getInstance());点击后会自动取消选中
 * ps：android:textColorHighlight 设置按压时的高亮颜色
 */
public class RemoveSelectionLinkMovementMethod extends LinkMovementMethod {

    static RemoveSelectionLinkMovementMethod sInstance;

    public static RemoveSelectionLinkMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new RemoveSelectionLinkMovementMethod();

        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        boolean result = super.onTouchEvent(widget, buffer, event);
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            Selection.removeSelection(buffer);
        }
        return result;
    }
}
