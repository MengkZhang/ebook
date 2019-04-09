package com.tzpt.cloudlibrary.ui.ebook;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.EBookRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.EBookCategoryVo;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取电子书二级分类
 * Created by tonyjia on 2018/5/21.
 */
public class EBookTabPresenter extends RxPresenter<EBookTabContract.View> implements
        EBookTabContract.Presenter {
    @Override
    public void getEBookGradeList() {
        mView.showProgress();

        Subscription subscription = EBookRepository.getInstance().getEBookCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ClassifyTwoLevelBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setNetError();
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
                            } else {
                                mView.setEBookClassificationList(null);
                            }
                            mView.setContentView();
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
