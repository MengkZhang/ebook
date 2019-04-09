package com.tzpt.cloundlibrary.manager.base.adapter.helper;

import java.util.List;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface DataHelper<T> {

    boolean addAll(List<T> list);

    boolean addAll(int position, List<T> list);

    void add(T data);

    void add(int position, T data);

    void clear();

    boolean contains(T data);

    T getData(int index);

    void modify(T oldData, T newData);

    void modify(int index, T newData);

    boolean remove(T data);

    void remove(int index);
}
