package com.duxl.baselib.widget;

/**
 * create by duxl 2023/8/19
 */
public interface OnItemClickListener<E, R> {

    default void onItemClick(E item) {
        onItemClickR(item);
    }

    R onItemClickR(E item);
}
