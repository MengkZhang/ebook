package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.SearchAddressBean;

import java.util.List;

/**
 * 搜索地址
 * Created by ZhiqiangJia on 2017-10-23.
 */
public interface SearchAddressContract {
    interface View extends BaseContract.BaseView {

        void setSearchAddress(List<SearchAddressBean> list, String content, boolean refresh);

        void setSearchAddressEmpty(boolean refresh);

        void setLocationDistrict(String district);

        void showProgressDialog();

        void setSearchAddressError(boolean refresh);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getLocationArea();

        void searchAddressList(int pageNum, String content, String area);
    }

}
