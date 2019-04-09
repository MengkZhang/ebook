package com.tzpt.cloudlibrary.ui.search;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzpt.cloudlibrary.bean.SearchHotBean;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 搜索入口类
 * Created by ZhiqiangJia on 2017-09-28.
 */
public class SearchManager {

    private static final String SEARCH_KEY_BOOK = "search_type_book";
    private static final String SEARCH_KEY_EBOOK = "search_type_ebook";
    private static final String SEARCH_KEY_LIBRARY = "search_type_library";
    private static final String SEARCH_KEY_INFORMATION = "search_type_information";
    private static final String SEARCH_KEY_READERS = "search_type_readers";
    private static final String SEARCH_KEY_VIDEO = "search_type_video";
    private static final String SEARCH_KEY_BOOK_STORE = "search_type_book_store";
    private static final String SEARCH_KEY_NEAR = "search_type_near";

    private static final String HOT_SEARCH_KEY_BOOK = "hot_search_type_book";
    private static final String HOT_SEARCH_KEY_EBOOK = "hot_search_type_ebook";
    private static final String HOT_SEARCH_KEY_LIBRARY = "hot_search_type_library";
    private static final String HOT_SEARCH_KEY_INFORMATION = "hot_search_type_information";
    private static final String HOT_SEARCH_KEY_READERS = "hot_search_type_readers";
    private static final String HOT_SEARCH_KEY_VIDEO = "hot_search_type_video";
    private static final String HOT_SEARCH_KEY_BOOK_STORE = "hot_search_type_book_store";
    private static final String HOT_SEARCH_KEY_NEAR = "hot_search_type_near";

    private static final String LIB_HOT_SEARCH_KEY_BOOK = "lib_hot_search_type_book";
    private static final String LIB_SEARCH_KEY_EBOOK = "lib_hot_search_type_ebook";
    private static final String LIB_SEARCH_KEY_INFORMATION = "hot_search_type_information";

    private static void saveSearchInfoByType(int searchType, String value) {
        switch (searchType) {
            case 0:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_BOOK, value);
                break;
            case 1:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_EBOOK, value);
                break;
            case 2:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_LIBRARY, value);
                break;
            case 3:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_VIDEO, value);
                break;
            case 4:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_INFORMATION, value);
                break;
            case 5:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_READERS, value);
                break;
            case 6:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_BOOK_STORE, value);
                break;
            case 7:
                SharedPreferencesUtil.getInstance().putString(SEARCH_KEY_NEAR, value);
                break;
        }
    }

    private static String getSearchInfoByType(int searchType) {
        if (searchType == 0) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_BOOK);
        } else if (searchType == 1) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_EBOOK);
        } else if (searchType == 2) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_LIBRARY);
        } else if (searchType == 3) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_VIDEO);
        } else if (searchType == 4) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_INFORMATION);
        } else if (searchType == 5) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_READERS);
        } else if (searchType == 6) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_BOOK_STORE);
        } else if (searchType == 7) {
            return SharedPreferencesUtil.getInstance().getString(SEARCH_KEY_NEAR);
        }
        return null;
    }

    public static void delAllSearchTag(int searchType) {
        if (searchType == 0) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_BOOK);
        } else if (searchType == 1) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_EBOOK);
        } else if (searchType == 2) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_LIBRARY);
        } else if (searchType == 3) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_VIDEO);
        } else if (searchType == 4) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_INFORMATION);
        } else if (searchType == 5) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_READERS);
        } else if (searchType == 6) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_BOOK_STORE);
        } else if (searchType == 7) {
            SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_NEAR);
        }
    }

    //保存历史记录标签
    public static void saveHistoryTag(int searchType, String content) {
        List<String> list = new ArrayList<>();
        String lastContent = SearchManager.getSearchInfoByType(searchType);
        if (!TextUtils.isEmpty(lastContent)) {
            if (lastContent.contains(",")) {
                String[] temps = lastContent.split(",");
                list.addAll(Arrays.asList(temps));
            } else {
                list.add(lastContent);
            }
        }
        //1.去掉重复元素
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String x = it.next();
            if (x.equals(content)) {
                it.remove();
            }
        }
        //2.将最新元素放在最前面
        list.add(0, content);
        //3.取最新的10条记录
        String result = arrayToString((list.size() > 10) ? list.subList(0, 10) : list);
        result = result.replaceAll(" ", "");
        //5.保存到文件中
        saveSearchInfoByType(searchType, result);
    }

    //数组转化为字符串
    private static String arrayToString(List<String> strList) {
        String[] str = (String[]) strList.toArray(new String[0]);
        //4.转化为string
        return Arrays.toString(str).replace("[", "").replace("]", "");
    }

    //获取历史标签列表
    public static String[] getHistoryTagList(int searchType) {
        List<String> list = new ArrayList<>();
        String lastContent = getSearchInfoByType(searchType);
        if (!TextUtils.isEmpty(lastContent)) {
            if (lastContent.contains(",")) {
                String[] temps = lastContent.split(",");
                list.addAll(Arrays.asList(temps));
            } else {
                list.add(lastContent);
            }
            return listToStringArray(list);
        } else {
            return null;
        }

    }

    //列表转换为数组
    private static String[] listToStringArray(List<String> strList) {
        return (String[]) strList.toArray(new String[0]);
    }

    //删除知道标签
    public static String[] delHistoryTag(int searchType, int position) {
        List<String> list = new ArrayList<>();
        String lastContent = getSearchInfoByType(searchType);
        if (!TextUtils.isEmpty(lastContent)) {
            if (lastContent.contains(",")) {
                String[] temps = lastContent.split(",");
                list.addAll(Arrays.asList(temps));
            } else {
                list.add(lastContent);
            }
        }
        //移除list
        if (position < list.size()) {
            list.remove(position);
            //3.取最新的10条记录
            String result = arrayToString((list.size() > 10) ? list.subList(0, 10) : list);
            result = result.replaceAll(" ", "");
            //5.保存到文件中
            SearchManager.saveSearchInfoByType(searchType, result);
            return listToStringArray((list.size() > 10) ? list.subList(0, 10) : list);
        }
        return null;
    }

    //判断可以进入搜索的名称
    public static String clickToSearch(int searchType, int position) {
        List<String> list = new ArrayList<>();
        String lastContent = SearchManager.getSearchInfoByType(searchType);
        if (!TextUtils.isEmpty(lastContent)) {
            if (lastContent.contains(",")) {
                String[] temps = lastContent.split(",");
                list.addAll(Arrays.asList(temps));
            } else {
                list.add(lastContent);
            }
            if (position < list.size()) {
                return list.get(position);
            }
        }
        return null;
    }

    /**
     * 保存热门搜索列表
     *
     * @param searchType 搜索类型
     * @param list       热门搜索列表
     */
    public static void saveHotSearchList(int searchType, List<SearchHotBean> list) {
        Gson gson = new Gson();
        String strJson = gson.toJson(list);
        switch (searchType) {
            case 0:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_BOOK, strJson);
                break;
            case 1:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_EBOOK, strJson);
                break;
            case 2:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_LIBRARY, strJson);
                break;
            case 3:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_VIDEO, strJson);
                break;
            case 4:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_INFORMATION, strJson);
                break;
            case 5:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_READERS, strJson);
                break;
            case 6:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_BOOK_STORE, strJson);
                break;
            case 7:
                SharedPreferencesUtil.getInstance().putString(HOT_SEARCH_KEY_NEAR, strJson);
                break;
        }
    }

    /**
     * 获取热门搜索列表
     *
     * @param searchType 搜索类型
     * @return 热门搜索列表
     */
    public static List<SearchHotBean> getHotSearchList(int searchType) {
        String strJson = null;
        switch (searchType) {
            case 0:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_BOOK);
                break;
            case 1:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_EBOOK);
                break;
            case 2:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_LIBRARY);
                break;
            case 3:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_VIDEO);
                break;
            case 4:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_INFORMATION);
                break;
            case 5:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_READERS);
                break;
            case 6:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_BOOK_STORE);
                break;
            case 7:
                strJson = SharedPreferencesUtil.getInstance().getString(HOT_SEARCH_KEY_NEAR);
                break;
        }
        if (TextUtils.isEmpty(strJson)) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(strJson, new TypeToken<List<SearchHotBean>>() {
        }.getType());
    }

    //清除所有搜索内容
    public static void clearAllSearchListTag() {
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_BOOK);
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_EBOOK);
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_LIBRARY);
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_VIDEO);
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_INFORMATION);
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_READERS);
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_BOOK_STORE);
        SharedPreferencesUtil.getInstance().remove(SEARCH_KEY_NEAR);

        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_BOOK);
        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_EBOOK);
        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_LIBRARY);
        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_VIDEO);
        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_INFORMATION);
        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_READERS);
        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_BOOK_STORE);
        SharedPreferencesUtil.getInstance().remove(HOT_SEARCH_KEY_NEAR);
    }
}
