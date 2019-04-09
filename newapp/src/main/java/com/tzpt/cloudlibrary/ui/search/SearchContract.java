package com.tzpt.cloudlibrary.ui.search;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.SearchTypeBean;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreFragment;
import com.tzpt.cloudlibrary.ui.bookstore.BookStorePresenter;
import com.tzpt.cloudlibrary.ui.ebook.EBookFragment;
import com.tzpt.cloudlibrary.ui.ebook.EBookPresenter;
import com.tzpt.cloudlibrary.ui.information.InformationFragment;
import com.tzpt.cloudlibrary.ui.information.InformationPresenter;
import com.tzpt.cloudlibrary.ui.library.LibraryFragment;
import com.tzpt.cloudlibrary.ui.library.LibraryPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBookPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBooksFragment;
import com.tzpt.cloudlibrary.ui.readers.ActivityListFragment;
import com.tzpt.cloudlibrary.ui.readers.ActivityListPresenter;
import com.tzpt.cloudlibrary.ui.video.VideoListFragment;
import com.tzpt.cloudlibrary.ui.video.VideoListPresenter;

import java.util.List;

/**
 * 搜索协议
 * Created by ZhiqiangJia on 2017-09-28.
 */
public interface SearchContract {

    interface View extends BaseContract.BaseView {

        void setHistoryTag(String[] result);

        void toSearchResult(String searchContent);

        void setSearchTypeList(List<SearchTypeBean> searchTypeList, SearchTypeBean item);

        void setHotSearchList(List<String> hotSearchList);

        void dismissHotSearchList();

        void doHotSearch(String name, String value, boolean isBookStore);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 获取搜索类型列表
         *
         * @param isLib true馆内搜索 false首页搜索
         */
        void getSearchTypeList(boolean isLib, int searchType);

        /**
         * 清空所有历史搜索记录
         *
         * @param searchType 搜索类型
         */
        void delAllHistoryTag(int searchType);

        /**
         * 删除某一条历史搜索记录
         *
         * @param searchType 搜索类型
         * @param position   索引值
         */
        void delHistoryTag(int searchType, int position);

        /**
         * 获取搜索历史记录
         *
         * @param searchType 搜索类型
         */
        void getHistoryTag(int searchType);

        /**
         * @param searchType
         * @param position
         */
        void clickToSearch(int searchType, int position);

        /**
         * 获取热门搜索列表
         *
         * @param searchType 搜索类型
         */
        void getHotSearchList(int searchType);

        /**
         * 获取馆内热门搜索列表
         *
         * @param libCode    馆号
         * @param searchType 搜索类型
         */
        void getLibHotSearchList(String libCode, int searchType);

        /**
         * 获取热门搜索关键字
         *
         * @param position 索引值
         */
        void getHotSearchValue(int position);

        /**
         * 获取搜索关键词
         *
         * @param searchType 搜索类型
         */
        String getSearchValueBySearchType(int searchType);

        /**
         * 清除搜索关键词
         */
        void clearSearchValue();

        /**
         * presenter对数据的操作
         * @param searchType    ：搜素的类型 0 图书  1 电子书 2 图书馆 3 视频 4 资讯 5 活动 6 书店 7 图书馆和书店混合列表
         * @param content ：搜索的内容
         */
        void searchOption(int searchType, String content);


    }
}
