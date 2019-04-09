package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.HelpInfoBean;

import java.util.List;

/**
 * 帮助协议
 * Created by ZhiqiangJia on 2018-01-08.
 */
public interface LibraryHelpContract {

    interface View extends BaseContract.BaseView {

        void setLibraryHelpList(List<HelpInfoBean> helpInfoBeanList, String title);

        void setLibraryHelpListEmpty();

        void setNetError();

        void setNoLoginPermission(int kickOut);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLibraryHelpList();
    }
}
