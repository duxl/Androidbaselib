package com.duxl.baselib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 元素唯一，不重复的集合
 *
 * @param <E> 需要自己实现元的equals方法判断元素是否相等
 */
public class UniqueList<E> extends ArrayList<E> {
    @Override
    public boolean add(E e) {
        if (e == null || contains(e)) {
            return false;
        }
        return super.add(e);
    }

    @Override
    public void add(int index, E e) {
        if (e == null || contains(e)) {
            return;
        }
        super.add(index, e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            return false;
        }

        return super.addAll(c.stream().filter(it -> it != null && !contains(it)).collect(Collectors.toList()));
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c == null) {
            return false;
        }

        return super.addAll(index, c.stream().filter(it -> it != null && !contains(it)).collect(Collectors.toList()));
    }
}
