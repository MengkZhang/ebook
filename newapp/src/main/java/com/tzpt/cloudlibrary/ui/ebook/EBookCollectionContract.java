package com.tzpt.cloudlibrary.ui.ebook;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.EBookBean;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by tonyjia on 2018/7/11.
 */
public interface EBookCollectionContract {

    interface View extends BaseContract.BaseView {
        void setEBookListError(boolean refresh);

        void setEBookList(List<EBookBean> eBookInfoBeans, boolean refresh);

        void setEBookListEmpty(boolean refresh);

        void showErrorMsg(int resId);

        void showDelProgress();

        void dismissDelProgress();

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getCollectionEBookList(final int pageNo, boolean isDelComplete);

        void cancelCollectionEBookList(List<String> collectionEBookIdList);

        void setEditorAble(boolean hasAble);

        /**
         * 注册
         *
         * @param eventType
         * @param <T>
         */
        <T> void registerRxBus(Class<T> eventType, Action1<T> action);

        /**
         * 注销
         */
        void unregisterRxBus();
    }
}
