package com.tzpt.cloudlibrary.ui.common;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.EBookRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookCategoryVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookCategoryVo;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 高级搜索分类
 * Created by ZhiqiangJia on 2017-09-25.
 */
public class AdvancedCategoryPresenter extends RxPresenter<AdvancedCategoryContract.View> implements
        AdvancedCategoryContract.Presenter {

    private int mFromType;

    @Override
    public void setFromType(int fromType) {
        this.mFromType = fromType;
    }

    @Override
    public void getAdvancedCategoryList() {
        if (null != mView) {
            switch (mFromType) {
                case 0:
                    mView.setAdvancedGradeBeanList(getInformationGradeList());
                    break;
                case 5:
                    mView.setAdvancedGradeBeanList(getLibInformationGradeList());
                    break;
                case 1://图书高级搜索分类
                case 3://图书高级搜索分类
                    getBookGradeList();
                    break;
                case 2://电子书高级搜索分类
                case 4://电子书高级搜索分类
                    getEBookGradeList();
                    break;
                case 10://图书馆馆别
                    getLibraryTypeList();
                    break;
                case 11:
                    mView.setAdvancedGradeBeanList(getInformationIndustry());
                    break;
                case 12://附近-图书馆和书店
                    mView.setAdvancedGradeBeanList(getLibAdvancedTypeList());
                    break;
            }
        }
    }

    /**
     * 附近-图书馆和书店
     *
     * @return
     */
    private List<ClassifyInfo> getLibAdvancedTypeList() {
        List<ClassifyInfo> list = new ArrayList<>();
        list.add(new ClassifyInfo(1, "图书馆", ""));
        list.add(new ClassifyInfo(2, "书店", ""));
        return list;
    }

    //资讯分类列表
    private List<ClassifyInfo> getInformationGradeList() {
        List<ClassifyInfo> list = new ArrayList<>();
        list.add(new ClassifyInfo(1, "平台资讯", ""));
        list.add(new ClassifyInfo(2, "客户资讯", ""));
        list.add(new ClassifyInfo(3, "图书馆资讯", ""));
        return list;
    }

    //馆内资讯分类列表
    private List<ClassifyInfo> getLibInformationGradeList() {
        List<ClassifyInfo> list = new ArrayList<>();
        list.add(new ClassifyInfo(2, "客户资讯", ""));
        list.add(new ClassifyInfo(3, "图书馆资讯", ""));
        return list;
    }

    //资讯行业分类列表
    private List<ClassifyInfo> getInformationIndustry() {
        List<ClassifyInfo> list = new ArrayList<>();
        list.add(new ClassifyInfo(-1, "全部", ""));
        list.add(new ClassifyInfo(1, "文化类", ""));
        list.add(new ClassifyInfo(2, "教育类", ""));
        return list;
    }

    //图书高级搜索分类
    private void getBookGradeList() {
        Subscription subscription = DataRepository.getInstance().getBookCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<BookCategoryVo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showError();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<List<BookCategoryVo>> listBaseResultEntityVo) {
                        if (mView != null) {
                            if (listBaseResultEntityVo.status == 200) {
                                List<ClassifyInfo> bookClassifications = new ArrayList<>();
                                for (BookCategoryVo item : listBaseResultEntityVo.data) {
                                    ClassifyInfo classifyInfo = new ClassifyInfo();
                                    classifyInfo.code = item.categoryCode;
                                    classifyInfo.id = item.categoryId;
                                    classifyInfo.name = item.categoryName;
                                    bookClassifications.add(classifyInfo);
                                }
                                mView.setAdvancedGradeBeanList(bookClassifications);
                            } else {
                                mView.showError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //电子书高级搜索分类
    private void getEBookGradeList() {
        Subscription subscription = EBookRepository.getInstance().getEBookCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ClassifyTwoLevelBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showError();
                        }
                    }

                    @Override
                    public void onNext(List<ClassifyTwoLevelBean> classifyTwoLevelBeans) {
                        if (null != mView) {
                            if (null != classifyTwoLevelBeans) {
                                //一级菜单添加全部分类
                                List<ClassifyTwoLevelBean> beanList = new ArrayList<>();
                                ClassifyTwoLevelBean bean = new ClassifyTwoLevelBean();
                                bean.mName = "全部";
                                //全部子类数据
                                List<ClassifyTwoLevelBean> allSubList = new ArrayList<>();
                                ClassifyTwoLevelBean subBean = new ClassifyTwoLevelBean();
                                subBean.mName = "全部";
                                allSubList.add(subBean);

                                //二级子类数据重组
                                for (ClassifyTwoLevelBean item : classifyTwoLevelBeans) {
                                    ClassifyTwoLevelBean levelBean = new ClassifyTwoLevelBean();
                                    levelBean.mId = item.mId;
                                    levelBean.mName = item.mName;

                                    List<ClassifyTwoLevelBean> levelBeanList = new ArrayList<>();
                                    if (null != item.mSubList && item.mSubList.size() > 0) {
                                        //二级菜单添加全部
                                        ClassifyTwoLevelBean subLevelBean = new ClassifyTwoLevelBean();
                                        subLevelBean.mName = "全部";
                                        levelBeanList.add(subBean);

                                        for (ClassifyTwoLevelBean subItem : item.mSubList) {
                                            ClassifyTwoLevelBean towLevelBean = new ClassifyTwoLevelBean();
                                            towLevelBean.mId = subItem.mId;
                                            towLevelBean.mName = subItem.mName;
                                            //添加一级下的二级数据
                                            levelBeanList.add(towLevelBean);
                                            //添加全部二级数据
                                            allSubList.add(towLevelBean);
                                        }
                                    }
                                    //指定一级分类下的二级分类列表
                                    levelBean.mSubList = levelBeanList;
                                    beanList.add(levelBean);
                                }
                                bean.mSubList = allSubList;
                                beanList.add(0, bean);
                                mView.setEBookClassificationList(beanList);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //图书馆馆别
    private void getLibraryTypeList() {
        Subscription subscription = DataRepository.getInstance().getLibraryLevel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<List<String>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showError();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<List<String>> listBaseResultEntityVo) {
                        if (mView != null) {
                            if (listBaseResultEntityVo.status == 200
                                    && listBaseResultEntityVo.data != null) {
                                List<ClassifyInfo> classifyList = new ArrayList<>();
                                for (String className : listBaseResultEntityVo.data) {
                                    ClassifyInfo classify = new ClassifyInfo();
                                    classify.id = 0;
                                    classify.name = className;
                                    classify.code = "";
                                    classifyList.add(classify);
                                }
                                mView.setAdvancedGradeBeanList(classifyList);
                            } else {
                                mView.showError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
