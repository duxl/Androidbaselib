package com.duxl.baselib.utils;

import android.text.Layout;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.duxl.baselib.widget.DataRunnable;

/**
 * 文本折叠工具，例如文本超过3行就只显示3行，点击后展开更多
 */
public class TextExpandUtil {

    /**
     * 截取文本，调用此方法需要TextView已经将文本绘制到UI，建议在tv.post函数中调用，例如：<br/>
     * 获取多行文本的第1行，getTruncated(tv, 0, 0)<br/>
     * 获取多行文本的第2、3行，getTruncated(tv, 1, 2)<br/>
     *
     * @param tv
     * @param startLine 开始截取行号
     * @param endLine   结束截取行号
     * @return
     */
    public static @Nullable CharSequence getTruncated(TextView tv, int startLine, int endLine) {
        Layout layout = tv.getLayout();
        if (startLine > layout.getLineCount()) {
            startLine = layout.getLineCount();
        }
        if (endLine > layout.getLineCount()) {
            endLine = layout.getLineCount();
        }

        if (endLine < startLine) {
            return null;
        }

        int start = layout.getLineStart(startLine);
        int end = layout.getLineEnd(endLine);
        return tv.getText().subSequence(start, end);
    }

    /**
     * 折叠文本
     *
     * @param tv             需要折叠的TextView，注意TextView不要设置函数限制，即不要设置setMaxLines(Int)和setSingleLine(Boolean)
     * @param moreAttachText 折叠后的文本末尾附加文案
     * @param maxLine        最大行，将超过此行后的文本折叠起来
     * @param callback       折叠后的回调函数，成功折叠回调true，返回false表示文本未超过maxLine将不会折叠
     */
    public static void collapse(TextView tv, CharSequence moreAttachText, int maxLine, @Nullable DataRunnable<Boolean> callback) {
        tv.post(() -> {
            if (tv.getLineCount() <= maxLine) {
                if (callback != null) {
                    callback.run(false);
                }
                return;
            }

            // 最大行前N行文本
            CharSequence beforeLinesText = getTruncated(tv, 0, maxLine - 2);
            // 最大行的文本
            CharSequence endLinesText = getTruncated(tv, maxLine - 1, maxLine - 1);
            if (endLinesText != null) {
                // 最后一行文本折叠后的内容, 使用TextUtils.ellipsize函数将连接moreText后的文本，末尾显示不下的使用省略号
                CharSequence ellipsizedText;
                if (EmptyUtils.isEmpty(moreAttachText)) {
                    ellipsizedText = TextUtils.ellipsize(TextUtils.concat(endLinesText,"…"), tv.getPaint(), tv.getMeasuredWidth() - tv.getPaddingStart() - tv.getPaddingEnd(), TextUtils.TruncateAt.END);
                } else {
                    CharSequence tmpEllipsizedText = TextUtils.ellipsize(TextUtils.concat(moreAttachText, endLinesText), tv.getPaint(), tv.getMeasuredWidth() - tv.getPaddingStart() - tv.getPaddingEnd(), TextUtils.TruncateAt.END);
                    // 将moreAttachText放置到文本末尾
                    ellipsizedText = TextUtils.concat(tmpEllipsizedText.subSequence(moreAttachText.length(), tmpEllipsizedText.length()), tmpEllipsizedText.subSequence(0, moreAttachText.length()));

                }
                tv.setText(TextUtils.concat(beforeLinesText, ellipsizedText));
                if (callback != null) {
                    callback.run(true);
                }

            } else {
                if (callback != null) {
                    callback.run(false);
                }
            }
        });
    }
}
