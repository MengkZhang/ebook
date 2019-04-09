package com.tzpt.cloundlibrary.manager.base;

/**
 * Created by Administrator on 2017/6/20.
 */

public interface BaseContract {
    interface BasePresenter<T>{
        void attachView(T view);
        void detachView();
    }

    interface BaseView<T>{
    }
}
